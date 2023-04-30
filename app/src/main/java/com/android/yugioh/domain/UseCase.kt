package com.android.yugioh.domain

interface UseCase<T> {
	suspend operator fun invoke() : Result<T>
}