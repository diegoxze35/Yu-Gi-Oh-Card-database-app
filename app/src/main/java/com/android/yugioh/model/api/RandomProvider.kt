package com.android.yugioh.model.api

import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class RandomProvider(api: YuGiOhApi) {

	companion object {
		private const val META = "meta"
		private const val TOTAL_ROWS = "total_rows"
	}

	private var offsets: MutableList<Long> = mutableListOf()
	private val totalRows: Long by lazy {
		runBlocking {
			suspend {
				api.randomCard(offset = 0L).body()?.getAsJsonObject(META)?.get(TOTAL_ROWS)?.asLong
					?: 1000L
			}()
		}
	}

	@Synchronized
	fun getOffset(get: (MutableList<Long>) -> Long): Long {
		if (offsets.isEmpty())
			offsets = generateSequence(0L) {
				return@generateSequence if (it < totalRows) it + 1L else null
			}.shuffled(Random(System.currentTimeMillis())).toMutableList()
		return get(offsets)
	}

}