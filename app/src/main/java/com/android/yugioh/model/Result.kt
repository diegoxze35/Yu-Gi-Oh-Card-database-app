package com.android.yugioh.model

sealed class Result<out R> {
	data class Success<T>(val body: T) : Result<T>()
	data class Error(val exception: Exception) : Result<Nothing>()
}
