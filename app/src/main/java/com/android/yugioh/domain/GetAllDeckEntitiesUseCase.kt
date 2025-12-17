package com.android.yugioh.domain

import com.android.yugioh.database.dao.DeckDao
import com.android.yugioh.database.entities.DeckEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllDeckEntitiesUseCase @Inject constructor(private val deckDao: DeckDao) {

	operator fun invoke(): Flow<List<DeckEntity>> {
		return deckDao.getAllDecks()
	}

}