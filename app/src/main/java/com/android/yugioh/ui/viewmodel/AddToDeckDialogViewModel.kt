package com.android.yugioh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yugioh.database.dao.DeckDao
import com.android.yugioh.database.entities.DeckEntity
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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddToDeckDialogViewModel @Inject constructor(
	private val deckDao: DeckDao
) : ViewModel() {

	private val _isCreatingMode = MutableStateFlow(false)
	private val _events = Channel<DeckDialogEvent>()
	val events = _events.receiveAsFlow()
	val uiState: StateFlow<DeckDialogState> = combine(
		deckDao.getAllDecks(),
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
				val newDeck = DeckEntity(name = name, createdAt = Date())
				deckDao.insertDeck(newDeck)
				_events.send(DeckDialogEvent.ShowToast("'$name'"))
				_isCreatingMode.value = false
			} catch (e: Exception) {
				_events.send(DeckDialogEvent.ShowToast("${e.message}"))
			} finally {
				_isLoadingSave.value = false
			}
		}
	}
}
