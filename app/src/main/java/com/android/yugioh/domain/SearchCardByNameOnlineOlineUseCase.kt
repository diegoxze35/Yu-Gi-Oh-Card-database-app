package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.Result
import com.android.yugioh.model.api.CardService
import javax.inject.Inject

class SearchCardByNameOnlineOlineUseCase @Inject constructor(override val service: CardService) : UseCaseOnlineSearchBy<String> {
	private val searchData: MutableMap<String, List<Card>> = mutableMapOf()
	override suspend fun invoke(query: Searchable): Result<List<Card>> {
		searchData[query.query!!]?.let { return Result.Success(it) }
		return try {
			Result.Success(service.searchCardByName(query.query).also { cardList ->
				if (cardList.isNotEmpty()) searchData[query.query] = cardList
			})
		} catch (e: Exception) {
			Result.Error(e)
		}
	}
}