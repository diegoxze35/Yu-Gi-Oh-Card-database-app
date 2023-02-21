package com.android.yugioh.domain

import com.android.yugioh.model.Result

interface UseCase<T> {
	suspend operator fun invoke() : Result<T>
}