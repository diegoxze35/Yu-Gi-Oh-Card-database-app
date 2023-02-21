package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.Result

class SearchCardByNameOfflineOlineUseCase(var from: List<Card>) :
	UseCaseSearchBy<String> {

	override suspend fun invoke(query: String): Result<List<Card>> = Result.Success(
		from.filter { it.name.contains(query, ignoreCase = true) }
	)
}
