package com.android.yugioh.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.android.yugioh.database.dao.DeckDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
	private val decksDao: DeckDao
): ViewModel() {

	suspend fun allDecks() = decksDao.getAllDecksMetadata()
}