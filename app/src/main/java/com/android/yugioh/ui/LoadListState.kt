package com.android.yugioh.ui

import com.android.yugioh.domain.data.Card

data class LoadListState(
	val mainList: List<Card>,
	val isLoadingGone: Boolean
)