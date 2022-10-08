package com.android.yugioh.model.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YuGiOhApi {
	
	companion object {
		private const val LIMIT: UByte = 10u
		private const val MISC_INFO: String = "yes"
		private const val GET = "cardinfo.php"
	}
	
	@GET(GET)
	suspend fun randomCard(
		@Query("num") num: UByte = LIMIT,
		@Query("offset") offset: Long,
		@Query("misc") miscInfo: String = MISC_INFO
	): Response<JsonObject>
	
	@GET(GET)
	suspend fun searchCard(
		@Query("fname") query: String,
		@Query("misc") miscInfo: String = MISC_INFO
	): Response<JsonObject>
	
}