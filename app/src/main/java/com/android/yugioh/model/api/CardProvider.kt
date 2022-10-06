package com.android.yugioh.model.api

import com.android.yugioh.model.data.Card
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.Stack
import javax.inject.Inject
import kotlin.random.Random

//class using for use case

class CardProvider @Inject constructor(private val service: YuGiOhApi, private val gson: Gson) {
	
	private val stack: Stack<Set<Long>> = Stack()
	
	companion object {
		private const val MIN = 0L
	}
	
	suspend fun getListRandomCards(): List<Card>? {
		val currentList = mutableListOf<Card>()
		return withContext(Dispatchers.IO) {
			lateinit var response: Response<JsonObject>
			while (currentList.size < 10) {
				if (stack.empty()) {
					response = service.randomCard(offset = 0L)
					if (!response.isSuccessful || response.body() == null) return@withContext null
					val jsonObject = response.body()!!
					val max = jsonObject.getAsJsonObject("meta").get("total_rows").asLong
					stack.push(getFirstSet(max))
				}
				getRandoms().forEach {
					response = service.randomCard(offset = it)
					if (!response.isSuccessful || response.body() == null) return@withContext null
					currentList.add(
						gson.fromJson(
							response.body()!!.getAsJsonArray("data")[0],
							Card::class.java
						)
					)
				}
			}
			return@withContext currentList
		}
	}
	
	private fun getFirstSet(max: Long): Set<Long> = generateSequence(MIN) {
		if (it < max) return@generateSequence it + 1
		null
	}.toSet()
	
	private fun getRandoms(): Set<Long> {
		val random = Random(System.currentTimeMillis())
		val currentSet = stack.peek()
		if (currentSet.size <= 5)
			return stack.pop()
		val currentSetWithoutFirstAndLast = currentSet.run {
			minusElement(currentSet.first()).minusElement(currentSet.last())
		}
		stack[stack.size - 1] -= currentSetWithoutFirstAndLast
		val numberRandom =
			(currentSetWithoutFirstAndLast.first() + 1 until currentSetWithoutFirstAndLast.last()).random(random)
		stack.apply {
			if (numberRandom % 2L == 0L) {
				push((currentSetWithoutFirstAndLast.first() until numberRandom).toSet())
				push((numberRandom + 1..currentSetWithoutFirstAndLast.last()).toSet())
			} else {
				push((numberRandom + 1..currentSetWithoutFirstAndLast.last()).toSet())
				push((currentSetWithoutFirstAndLast.first() until numberRandom).toSet())
			}
		}
		return setOf(numberRandom)
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