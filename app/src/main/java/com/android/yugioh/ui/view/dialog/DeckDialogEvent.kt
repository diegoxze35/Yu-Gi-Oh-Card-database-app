package com.android.yugioh.ui.view.dialog

sealed interface DeckDialogEvent {
    data class ShowToast(val message: String) : DeckDialogEvent
}
