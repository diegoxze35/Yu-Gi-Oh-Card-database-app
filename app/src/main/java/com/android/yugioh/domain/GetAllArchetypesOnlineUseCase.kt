package com.android.yugioh.domain

import com.android.yugioh.model.Result
import com.android.yugioh.model.api.CardService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllArchetypesOnlineUseCase @Inject constructor(override val service: CardService) :
	OnlineUseCase, UseCase<List<String>> {

	private lateinit var archetypes: List<String>

	override suspend fun invoke(): Result<List<String>> {
		if (this::archetypes.isInitialized) return Result.Success(archetypes)
		return try {
			service.getAllArchetypes().run {
				when {
					isNotEmpty() -> {
						with(sorted()) {
							archetypes = this
							Result.Success(this)
						}
					}
					else -> {
						Result.Error(Exception("Can not get archetypes"))
					}
				}
			}
		} catch (e: Exception) {
			Result.Error(e)
		}
	}
}