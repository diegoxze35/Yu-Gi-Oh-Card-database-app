package com.android.yugioh.model.api

import com.android.yugioh.model.data.Card
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import kotlin.random.Random

//class using for use case
class CardProvider @Inject constructor(
	private val service: YuGiOhApi, private val gson: Gson
) {
	companion object {
		var isInit = false
		private const val FIRST_INDEX = 0
		private const val REPEAT = 5
		private const val MEMBER_NAME = "data"
		private const val ARCHETYPE_PROPERTY = "archetype_name"
		private const val MIN_OFFSET = 0L
		private lateinit var jsonObject: JsonObject
		private val MAX_OFFSET by lazy {
			jsonObject.getAsJsonObject("meta").get("total_rows").asLong
		}
	}
	
	private val mutex = Mutex()
	private lateinit var offsets: MutableList<Long>
	private val listResult: MutableList<Card> = mutableListOf()
	
	suspend fun getListRandomCards(): List<Card>? = withContext(Dispatchers.IO) {
		if (!isInit) {
			val response = service.randomCard(offset = MIN_OFFSET)
			if (!response.onSuccess()) return@withContext null
			jsonObject = response.body()!!
			generateSequence(MIN_OFFSET) {
				if (it != MAX_OFFSET) return@generateSequence it + 1
				null
			}.also {
				offsets = it.shuffled(Random(System.currentTimeMillis())).toMutableList()
			}
			isInit = true
		}
		listResult.clear()
		val jobMinOffsets = async {
			requestCardsFromOffset(offsets::removeFirst)
		}
		val jobMaxOffsets = async {
			requestCardsFromOffset(offsets::removeLast)
		}
		return@withContext if (jobMinOffsets.await() && jobMaxOffsets.await())
			listResult
		else
			null
	}
	
	private suspend fun requestCardsFromOffset(getOffset: () -> Long): Boolean {
		var offset: Long
		repeat(REPEAT) {
			mutex.withLock {
				if (offsets.isEmpty()) return false
				offset = getOffset.invoke()
			}
			val response = service.randomCard(offset = offset)
			if (!response.onSuccess()) return false
			mutex.withLock {
				listResult.add(
					gson.fromJson(
						response.body()!!.getAsJsonArray(MEMBER_NAME)[FIRST_INDEX], Card::class.java
					)
				)
			}
		}
		return true
	}
	
	suspend fun searchCard(query: String): List<Card> = withContext(Dispatchers.IO) {
		val response = service.searchCard(query)
		if (!response.onSuccess()) return@withContext emptyList()
		val currentArray = response.body()!!.get(MEMBER_NAME).asJsonArray!!
		val currentListSize = currentArray.size()
		if (currentListSize == 1) return@withContext listOf<Card>(
			gson.fromJson(currentArray.get(FIRST_INDEX), Card::class.java)
		)
		val currentList = currentArray.toList()
		val index = (currentListSize / 2) + 1
		val asyncTask = async {
			getCards(currentList.subList(index, currentListSize))
		}
		val result = getCards(currentList.subList(0, index))
		return@withContext result.plus(asyncTask.await())
	}
	
	
	private fun getCards(list: List<JsonElement>): List<Card> = list.map {
		gson.fromJson(it, Card::class.java)
	}
	
	suspend fun getAllArchetypes(): Array<String>? = withContext(Dispatchers.IO) {
		val response = service.getAllArchetypes()
		if (!response.onSuccess()) return@withContext null
		return@withContext response.body()!!.asJsonArray!!.run {
			Array<String>(size()) { this[it].asJsonObject.get(ARCHETYPE_PROPERTY).asString }
		}
	}
	
	private fun <T> Response<T>.onSuccess(): Boolean = (isSuccessful && body() != null)
	
}