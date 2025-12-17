package com.android.yugioh.domain

import com.android.yugioh.database.dao.DeckDao
import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.mapper.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllCardsFromDeckUseCase @Inject constructor(private val deckDao: DeckDao) {

	suspend operator fun invoke(deckId: Int): List<Card> {
		return deckDao.getDeckWithCards(deckId).entries.map {
			it.card.toDomain()
		}/*.map {
			it.entries.map { entry ->
				entry.card.toDomain()
			}
		}*/
	}

}