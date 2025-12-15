package com.android.yugioh.database.repository

import com.android.yugioh.database.dao.DeckDao
import javax.inject.Inject

class DeckRepository @Inject constructor(private val dao: DeckDao) {

	suspend fun getAllDecks() = dao.getAllDecksMetadata()

}
