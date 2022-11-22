package com.android.yugioh.model.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YuGiOhApi {
	
	companion object {
		private const val LIMIT: UByte = 10u
		private const val MISC_INFO: String = "yes"
		private const val GET_CARD_INFO = "cardinfo.php"
		private const val GET_ALL_ARCHETYPES = "archetypes.php"
	}
	
	@GET(GET_CARD_INFO)
	suspend fun randomCard(
		@Query("num") num: UByte = LIMIT,
		@Query("offset") offset: Long,
		@Query("misc") miscInfo: String = MISC_INFO
	): Response<JsonObject>
	
	@GET(GET_CARD_INFO)
	suspend fun searchCard(
		@Query("fname") query: String,
		@Query("misc") miscInfo: String = MISC_INFO
	): Response<JsonObject>
	
	@GET(GET_ALL_ARCHETYPES)
	suspend fun getAllArchetypes(): Response<JsonArray>
	
}