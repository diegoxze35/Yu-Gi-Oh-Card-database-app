package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.Result

sealed interface UseCaseSearchBy<T> {
	suspend operator fun invoke(query: Searchable): Result<List<Card>>
}