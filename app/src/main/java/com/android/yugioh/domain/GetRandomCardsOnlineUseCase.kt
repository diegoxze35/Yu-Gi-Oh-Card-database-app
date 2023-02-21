package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.api.CardService
import com.android.yugioh.model.Result
import com.android.yugioh.model.Result.Success
import com.android.yugioh.model.Result.Error
import javax.inject.Inject

class GetRandomCardsOnlineUseCase @Inject constructor(override val service: CardService) :
	OnlineUseCase, UseCase<List<Card>> {

	override suspend fun invoke(): Result<List<Card>> {
		return try {
			Success(service.getRandomCards())
		} catch (e: Exception) {
			Error(e)
		}
	}
}