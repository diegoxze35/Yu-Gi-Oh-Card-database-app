package com.android.yugioh.di

import android.content.Context
import androidx.room.Room
import com.android.yugioh.database.YuGiOhDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

	@Singleton
	@Provides
	fun provideYuGiOhDatabase(@ApplicationContext context: Context): YuGiOhDatabase {
		return Room.databaseBuilder(context, YuGiOhDatabase::class.java, "yugioh_database").build()
	}

	@Singleton
	@Provides
	fun provideDeckDao(database: YuGiOhDatabase) = database.deckDao()

	@Singleton
	@Provides
	fun provideCardDao(database: YuGiOhDatabase) = database.cardDao()

}