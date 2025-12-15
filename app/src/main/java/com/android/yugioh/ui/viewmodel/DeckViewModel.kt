package com.android.yugioh.ui.viewmodel

import com.android.yugioh.database.dao.DeckDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
	private val decksDao: DeckDao
) {

	suspend fun allDecks() = decksDao.getAllDecksMetadata()
}