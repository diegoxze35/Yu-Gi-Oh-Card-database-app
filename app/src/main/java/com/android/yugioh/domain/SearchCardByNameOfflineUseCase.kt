package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import javax.inject.Inject

class SearchCardByNameOfflineUseCase @Inject constructor() : UseCaseSearchBy<String> {

	var from: List<Card> = emptyList()
	override suspend fun invoke(query: String): Result<List<Card>> = Result.success(
		from.filter { it.name.contains(query, ignoreCase = true) }
	)
}
