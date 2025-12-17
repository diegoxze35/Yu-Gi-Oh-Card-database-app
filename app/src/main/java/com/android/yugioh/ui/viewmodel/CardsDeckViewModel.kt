package com.android.yugioh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yugioh.domain.GetAllCardsFromDeckUseCase
import com.android.yugioh.domain.data.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsDeckViewModel @Inject constructor(
	private val getAllCardsFromDeckUseCase: GetAllCardsFromDeckUseCase
) : ViewModel() {

	private val _allCards = MutableStateFlow<List<Card>>(emptyList())
	val allCards = _allCards.asStateFlow()

	fun getAllCards(deckId: Int) {
		viewModelScope.launch {
			_allCards.value = getAllCardsFromDeckUseCase(deckId)
		}
	}

}