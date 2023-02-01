package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.Result
import com.android.yugioh.model.api.CardService

interface UseCaseSearchBy<in T> {

	val service: CardService

	suspend operator fun invoke(query: T): Result<List<Card>>

}