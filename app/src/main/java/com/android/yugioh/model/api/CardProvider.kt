package com.android.yugioh.model.api

import com.android.yugioh.model.data.Card
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

//class using for use case

class CardProvider @Inject constructor(private val service: YuGiOhApi, private val gson: Gson) {
	
	companion object {
		private const val MIN = 0L
		var isInit = false
		private lateinit var jsonObject: JsonObject
		private val MAX by lazy {
			jsonObject.getAsJsonObject("meta").get("total_rows").asLong
		}
	}
	
	private val mutex = Mutex()
	
	
	private lateinit var list1: List<Long>
	private lateinit var list2: List<Long>
	private var listResult: MutableList<Card> = mutableListOf()
	
	suspend fun getListRandomCards(): List<Card>? {
		listResult.clear()
		return withContext(Dispatchers.IO) {
			if (!isInit) {
				val sequence by lazy {
					generateSequence(MIN) {
						if (it != MAX) return@generateSequence it + 1
						null
					}
				}
				val response = service.randomCard(offset = MIN)
				if (!response.isSuccessful || (response.body())?.also { jsonObject = it } == null)
					return@withContext null
				val allNumbers = sequence.toList()
				val index = ((allNumbers.size / 2) + 1)
				list1 = allNumbers.subList(0, index).shuffled()
				list2 = allNumbers.subList(index, allNumbers.size).shuffled()
				isInit = true
			}
			if (list1.isEmpty() && list2.isEmpty()) {
				isInit = false
				return@withContext getListRandomCards()
			}
			val job1 = async {
				var index = 0
				randomCardsList(
					list1.takeWhile {
						index++ < 5
					}.also { reduceList ->
						list1 = list1.filter { it !in reduceList }
					}
				)
			}
			val job2 = async {
				var index = 0
				randomCardsList(
					list2.takeWhile {
						index++ < 5
					}.also { reduceList ->
						list2 = list2.filter { it !in reduceList }
					}
				)
			}
			joinAll(job1, job2)
			listResult
		}
	}
	
	private suspend fun randomCardsList(list: List<Long>) {
		list.map {
			val response = service.randomCard(offset = it)
			if (!response.isSuccessful || response.body() == null) {
				return
			}
			val card = gson.fromJson(
				response.body()!!.getAsJsonArray("data")[0],
				Card::class.java
			)
			addCardMutex(card)
		}
	}
	
	private suspend fun addCardMutex(card: Card) {
		mutex.withLock {
			listResult.add(card)
		}
	}
	
	suspend fun searchCard(query: String): List<Card>? {
		return withContext(Dispatchers.IO) {
			val response = service.searchCard(query)
			if (response.code() == 400) return@withContext emptyList<Card>()
			if (!response.isSuccessful || response.body() == null) return@withContext null
			with(response.body()!!) {
				val currentArray = get("data").asJsonArray!!
				val currentListSize = currentArray.size()
				if (currentListSize == 1) return@withContext listOf<Card>(
					gson.fromJson(
						currentArray.get(0), Card::class.java
					)
				)
				val currentList = currentArray.toList()
				val index = (currentListSize / 2) + 1
				val asyncTask = async {
					getCards(currentList.subList(0, index))
				}
				val result = getCards(currentList.subList(index, currentList.size))
				return@withContext result.plus(asyncTask.await())
			}
		}
	}
	
	private fun getCards(list: List<JsonElement>): List<Card> = list.map {
		gson.fromJson(it, Card::class.java)
	}
}