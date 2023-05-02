package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.api.CardService
import javax.inject.Inject

class SearchCardByNameOnlineUseCase @Inject constructor(override val service: CardService) :
	UseCaseOnlineSearchBy<String> {
	private val searchData: MutableMap<String, List<Card>> = mutableMapOf()

	override suspend fun invoke(query: String): Result<List<Card>> =
		searchData[query]?.let {
			Result.success(it)
		} ?: runCatching {
			service.searchCardByName(query).also { cardList ->
				if (cardList.isNotEmpty()) searchData[query] = cardList
			}
		}
}