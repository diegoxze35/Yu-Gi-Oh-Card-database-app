package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.Result
import com.android.yugioh.model.api.CardService
import javax.inject.Inject

class SearchCardByNameUseCase @Inject constructor(override val service: CardService) :
	UseCaseSearchBy<String> {

	override suspend fun invoke(query: String): Result<List<Card>> {
		return try {
			Result.Success(service.searchCardByName(query))
		} catch (e: Exception) {
			Result.Error(e)
		}
	}
}