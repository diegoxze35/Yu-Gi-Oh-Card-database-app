package com.android.yugioh.domain

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.api.CardService
import com.android.yugioh.ui.view.dialog.DialogAdvancedSearch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchCardByOptionsOlineUseCase @Inject constructor(override val service: CardService) : UseCaseOnlineSearchBy<Map<String, String>> {

	override suspend fun invoke(query: String): Result<List<Card>> = runCatching {
		service.advancedSearch(query.split(DialogAdvancedSearch.SPLIT).associate {
			val (key, value) = it.lowercase().split(DialogAdvancedSearch.EQ)
			(key to value)
		})
	}
}