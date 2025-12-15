package com.android.yugioh.domain.data

import java.util.Date

data class DeckItem(
	val id: Int,
	val name: String,
	val createdAt: Date,
	val isLegal: Boolean
)
