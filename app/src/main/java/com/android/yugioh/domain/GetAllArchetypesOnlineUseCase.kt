package com.android.yugioh.domain

import com.android.yugioh.model.api.CardService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllArchetypesOnlineUseCase @Inject constructor(override val service: CardService) :
	OnlineUseCase, UseCase<List<String>> {

	private lateinit var archetypes: List<String>

	override suspend fun invoke(): Result<List<String>> {
		return if (this::archetypes.isInitialized)
			Result.success(archetypes)
		else runCatching {
			service.getAllArchetypes().sorted().also { archetypes = it }
		}
	}
}