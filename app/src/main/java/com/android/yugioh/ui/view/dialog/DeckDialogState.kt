package com.android.yugioh.ui.view.dialog

import com.android.yugioh.database.entities.DeckEntity

sealed interface DeckDialogState {
    object Loading : DeckDialogState
    data class Success(val decks: List<DeckEntity>) : DeckDialogState
    object Empty : DeckDialogState
    object CreateNew : DeckDialogState
    object Creating : DeckDialogState
}
