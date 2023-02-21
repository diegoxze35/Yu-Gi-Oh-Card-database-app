package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.Result
import com.android.yugioh.model.api.CardService
import javax.inject.Inject

class SearchCardByOptionsOlineUseCase @Inject constructor(override val service: CardService) :
	OlineUseCaseSearchBy<Map<String, String>> {

	override suspend fun invoke(query: Map<String, String>): Result<List<Card>> {
		return try {
			Result.Success(service.advancedSearch(query))
		} catch (e: Exception) {
			Result.Error(e)
		}
	}
}