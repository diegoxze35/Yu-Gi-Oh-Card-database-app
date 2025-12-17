package com.android.yugioh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yugioh.domain.AddCardToDeckUseCase
import com.android.yugioh.domain.CreateNewDeckUseCase
import com.android.yugioh.domain.GetAllDeckEntitiesUseCase
import com.android.yugioh.domain.data.Card
import com.android.yugioh.ui.view.dialog.DeckDialogEvent
import com.android.yugioh.ui.view.dialog.DeckDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddToDeckDialogViewModel @Inject constructor(
	getAllDecks: GetAllDeckEntitiesUseCase,
	private val addCardToDeckUseCase: AddCardToDeckUseCase,
	private val createNewDeckUseCase: CreateNewDeckUseCase
) : ViewModel() {

	private val _isCreatingMode = MutableStateFlow(false)
	private val _events = Channel<DeckDialogEvent>()
	val events = _events.receiveAsFlow()
	val uiState: StateFlow<DeckDialogState> = combine(
		getAllDecks(),
		_isCreatingMode
	) { decks, isCreating ->
		when {
			isCreating -> DeckDialogState.CreateNew
			decks.isEmpty() -> DeckDialogState.Empty
			else -> DeckDialogState.Success(decks)
		}
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = DeckDialogState.Loading
	)
	private val _isLoadingSave = MutableStateFlow(false)
	val finalState = combine(uiState, _isLoadingSave) { state, isSaving ->
		if (isSaving) DeckDialogState.Creating else state
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DeckDialogState.Loading)

	fun onAddClicked() {
		_isCreatingMode.value = true
	}

	fun onCancelClicked() {
		_isCreatingMode.value = false
	}

	fun createDeck(name: String) {
		viewModelScope.launch {
			_isLoadingSave.value = true
			try {
				createNewDeckUseCase(name.trim())
				_events.send(DeckDialogEvent.ShowToast("'$name'"))
				_isCreatingMode.value = false
			} catch (e: Exception) {
				_events.send(DeckDialogEvent.ShowToast("${e.message}"))
			} finally {
				_isLoadingSave.value = false
			}
		}
	}

	fun addCardToDeck(deckId: Int, card: Card, quantity: Int = 1) {
		viewModelScope.launch {
			try {
				addCardToDeckUseCase(deckId, card, quantity)
				_events.send(DeckDialogEvent.ShowToast("Card added to deck successfully"))
			} catch (e: Exception) {
				_events.send(DeckDialogEvent.ShowToast("Error: ${e.message}"))
			}
		}
	}
}
