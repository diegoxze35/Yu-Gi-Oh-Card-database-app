package com.android.yugioh.domain

import com.android.yugioh.model.Result
import com.android.yugioh.model.api.CardService

interface UseCase<T> {

	val service: CardService

	suspend operator fun invoke() : Result<T>

}