package com.android.yugioh.domain

import com.android.yugioh.database.dao.DeckDao
import com.android.yugioh.database.entities.DeckEntity
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateNewDeckUseCase @Inject constructor(private val deckDao: DeckDao) {

	suspend operator fun invoke(newDeckName: String): Long {
		val newDeck = DeckEntity(
			name = newDeckName,
			createdAt = Date(),
		)
		return deckDao.insertDeck(newDeck)
	}

}