package com.android.yugioh.di

import com.android.yugioh.domain.data.Card
import com.android.yugioh.model.api.YuGiOhApi
import com.android.yugioh.model.data.CardDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

	@Singleton
	@Provides
	fun provideApiService(): YuGiOhApi =
		Retrofit.Builder().addConverterFactory(ScalarsConverterFactory.create())
			.addConverterFactory(GsonConverterFactory.create())
			.baseUrl("https://db.ygoprodeck.com/api/v7/")
			.build()
			.create(YuGiOhApi::class.java)


	@Singleton
	@Provides
	fun provideGson(): Gson = GsonBuilder().run {
		registerTypeAdapter(Card::class.java, CardDeserializer)
		create()
	}

}