package com.android.yugioh.model.api

import com.android.yugioh.domain.data.Card
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class CardService @Inject constructor(private val api: YuGiOhApi, private val gson: Gson) {

	private var offsets: MutableList<Long> = mutableListOf()
	private val mutex = Mutex()

	companion object {
		private const val META = "meta"
		private const val TOTAL_ROWS = "total_rows"
		private const val MEMBER_NAME = "data"
		private const val ARCHETYPE_PROPERTY = "archetype_name"
		private const val TIMES = 5
	}

	suspend fun getRandomCards(): List<Card> = withContext(Dispatchers.IO) {
		if (offsets.isEmpty()) {
			val max =
				api.randomCard(offset = 0).body()?.getAsJsonObject(META)?.get(TOTAL_ROWS)?.asLong
					?: 1000L
			offsets.addAll(
				generateSequence(0L) {
					if (it != max) return@generateSequence it + 1
					null
				}.shuffled(Random(System.currentTimeMillis()))
			)
		}
		val finalList = mutableListOf<Card>()
		awaitAll(
			async { requestCardsFromOffset(offsets::removeFirst, finalList) },
			async { requestCardsFromOffset(offsets::removeLast, finalList) }
		)
		finalList
	}

	private suspend fun requestCardsFromOffset(getOffset: () -> Long, result: MutableList<Card>) {
		var offset: Long
		repeat(TIMES) {
			mutex.withLock {
				try {
					offset = getOffset()
				} catch (e: NoSuchElementException) {
					return
				}
			}
			val resposne = api.randomCard(offset = offset)
			resposne.body()?.let {
				val card = gson.fromJson(it.getAsJsonArray(MEMBER_NAME)[0], Card::class.java)
				mutex.withLock {
					result.add(card)
				}
			}
		}
	}

	suspend fun getAllArchetypes(): List<String> = withContext(Dispatchers.IO) {
		api.getAllArchetypes().body()?.map {
			it.get(ARCHETYPE_PROPERTY).asString
		} ?: emptyList()
	}

	suspend fun searchCardByName(query: String): List<Card> = withContext(Dispatchers.IO) {
		val response = api.searchCard(query)
		if (!response.isSuccessful) return@withContext emptyList()
		response.body()?.get(MEMBER_NAME)?.asJsonArray?.map {
			gson.fromJson(it, Card::class.java)
		} ?: emptyList()
	}

}