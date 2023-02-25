package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.Result
import javax.inject.Inject

class SearchCardByNameOfflineOlineUseCase @Inject constructor() : UseCaseSearchBy<String> {

	var from: List<Card> = emptyList()
	override suspend fun invoke(query: Searchable): Result<List<Card>> = Result.Success(
		from.filter { it.name.contains(query.query!!, ignoreCase = true) }
	)
}
