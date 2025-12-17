package com.android.yugioh.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.android.yugioh.domain.GetAllDeckMetadaUseCase
import com.android.yugioh.model.mapper.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
	private val getAllDeckMetadata: GetAllDeckMetadaUseCase
): ViewModel() {

	fun allDecks() = getAllDeckMetadata()
		.map {
			it.map { deck -> deck.toDomain() }
		}

}
