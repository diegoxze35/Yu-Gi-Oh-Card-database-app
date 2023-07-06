package com.android.yugioh.model.api

import com.android.yugioh.domain.data.Card
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardService @Inject constructor(
	private val api: YuGiOhApi,
	private val gson: Gson
) {
	private val randomProvider by lazy { RandomProvider(api) }
	companion object {
		private const val MEMBER_NAME = "data"
		private const val ARCHETYPE_PROPERTY = "archetype_name"
		private const val TIMES = 5
	}

	suspend fun getRandomCards(): List<Card> = withContext(Dispatchers.IO) {
		awaitAll(
			async { requestCardsFromOffset(MutableList<Long>::removeFirst) },
			async { requestCardsFromOffset(MutableList<Long>::removeLast) }
		).flatten()
	}

	private suspend fun requestCardsFromOffset(get: (MutableList<Long>) -> Long): List<Card> {
		val cards = mutableListOf<Card>()
		repeat(TIMES) {
			api.randomCard(offset = randomProvider.getOffset(get)).body()?.let {
				cards.add(
					gson.fromJson(it.getAsJsonArray(MEMBER_NAME)[0], Card::class.java)
				)
			}
		}
		return cards
	}

	suspend fun getAllArchetypes(): List<String> = withContext(Dispatchers.IO) {
		api.getAllArchetypes().body()!!.map {
			it.get(ARCHETYPE_PROPERTY).asString
		}
	}

	suspend fun searchCardByName(query: String): List<Card> = withContext(Dispatchers.IO) {
		val response = api.searchCard(query)
		if (!response.isSuccessful) return@withContext emptyList()
		return@withContext jsonToCardList(response.body())
	}

	suspend fun advancedSearch(options: Map<String, String>): List<Card> =
		withContext(Dispatchers.IO) {
			val response = api.advancedSearch(options)
			if (!response.isSuccessful) return@withContext emptyList()
			return@withContext jsonToCardList(response.body())
		}

	private fun jsonToCardList(json: JsonObject?): List<Card> =
		json?.get(MEMBER_NAME)?.asJsonArray?.map {
			gson.fromJson(it, Card::class.java)
		} ?: emptyList()

}