package com.android.yugioh.domain

import com.android.yugioh.database.dao.DeckDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllDeckMetadaUseCase @Inject constructor(private val dao: DeckDao) {

	operator fun invoke() = dao.getAllDecksMetadata()

}
