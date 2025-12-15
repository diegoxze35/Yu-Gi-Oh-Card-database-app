package com.android.yugioh.domain

import com.android.yugioh.database.dao.DeckDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAvailableDecksUseCase @Inject constructor(
	private val decksDao: DeckDao
) {
	suspend operator fun invoke() = decksDao.getAllDecks()
}