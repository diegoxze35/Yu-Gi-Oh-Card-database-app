package com.android.yugioh.model.api

import android.content.Context
import com.android.yugioh.model.data.Card
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

//class using for use case

class CardProvider @Inject constructor(
	@ApplicationContext context: Context,
	private val service: YuGiOhApi,
	private val gson: Gson
) {
	companion object {
		private const val NO_RESULTS_CODE = 400
		private const val MEMBER_NAME = "data"
		private const val MIN = 0L
		var isInit = false
		private lateinit var jsonObject: JsonObject
		private val MAX by lazy {
			jsonObject.getAsJsonObject("meta").get("total_rows").asLong
		}
	}
	
	private val mutex = Mutex()
	
	private lateinit var offsets: MutableList<Long>
	private var listResult: MutableList<Card> = mutableListOf()
	
	suspend fun getListRandomCards(): List<Card>? {
		listResult.clear()
		return withContext(Dispatchers.IO) {
			if (!isInit) {
				val response = service.randomCard(offset = MIN)
				if (!response.isSuccessful || (response.body())?.also { jsonObject = it } == null)
					return@withContext null
				generateSequence(MIN) {
					if (it != MAX) return@generateSequence it + 1
					null
				}.toList().also {
					offsets = it.shuffled().toMutableList()
				}
				isInit = true
			}
			val jobMinOffsets = async {
				requestCardsFromOffset(offsets::removeFirst)
			}
			val jobMaxOffsets = async {
				requestCardsFromOffset(offsets::removeLast)
			}
			return@withContext jobMinOffsets.await()?.and(jobMaxOffsets.await() ?: false)
				?.let { success ->
					if (success) listResult
					else null
				} ?: getListRandomCards()
		}
	}
	
	private suspend fun requestCardsFromOffset(getOffset: () -> Long): Boolean? {
		var i = 0
		var offset: Long
		while (i++ < 5) {
			mutex.withLock {
				if (offsets.isEmpty()) {
					isInit = false
					return null
				}
				offset = getOffset.invoke()
			}
			val response = service.randomCard(offset = offset)
			if (!response.isSuccessful || response.body() == null) return false
			mutex.withLock {
				listResult.add(
					gson.fromJson(
						response.body()!!.getAsJsonArray(MEMBER_NAME)[0],
						Card::class.java
					)
				)
			}
		}
		return true
	}
	
	suspend fun searchCard(query: String): List<Card>? {
		return withContext(Dispatchers.IO) {
			
			val response = service.searchCard(query)
			
			if (response.code() == NO_RESULTS_CODE) return@withContext emptyList<Card>()
			if (!response.isSuccessful || response.body() == null) return@withContext null
			
			val currentArray = response.body()!!.get(MEMBER_NAME).asJsonArray!!
			val currentListSize = currentArray.size()
			
			if (currentListSize == 1) return@withContext listOf<Card>(
				gson.fromJson(
					currentArray.get(0), Card::class.java
				)
			)
			
			val currentList = currentArray.toList()
			val index = (currentListSize / 2) + 1
			
			val asyncTask = async {
				getCards(currentList.subList(index, currentListSize))
			}
			
			val result = getCards(currentList.subList(0, index))
			return@withContext result.plus(asyncTask.await())
			
		}
	}
	
	private fun getCards(list: List<JsonElement>): List<Card> = list.map {
		gson.fromJson(it, Card::class.java)
	}
	
}