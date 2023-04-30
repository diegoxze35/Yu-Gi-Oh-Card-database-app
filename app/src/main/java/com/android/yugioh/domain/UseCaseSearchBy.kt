package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card

sealed interface UseCaseSearchBy<T> {
	suspend operator fun invoke(query: Searchable): Result<List<Card>>
}