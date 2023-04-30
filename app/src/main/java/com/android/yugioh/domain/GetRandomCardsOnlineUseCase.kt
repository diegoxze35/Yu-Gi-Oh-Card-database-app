package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.api.CardService
import javax.inject.Inject

class GetRandomCardsOnlineUseCase @Inject constructor(override val service: CardService) :
	OnlineUseCase, UseCase<List<Card>> {

	override suspend fun invoke(): Result<List<Card>> = runCatching {
		service.getRandomCards()
	}
}