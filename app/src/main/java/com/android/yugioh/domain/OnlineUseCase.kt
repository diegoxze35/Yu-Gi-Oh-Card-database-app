package com.android.yugioh.domain
import com.android.yugioh.model.api.CardService

interface OnlineUseCase {
	val service: CardService
}