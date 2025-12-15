package com.android.yugioh.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.yugioh.database.dao.CardDao
import com.android.yugioh.database.dao.DeckDao
import com.android.yugioh.database.entities.CardEntity
import com.android.yugioh.database.entities.DeckCardCrossRef
import com.android.yugioh.database.entities.DeckEntity

@Database(entities = [DeckEntity::class, CardEntity::class, DeckCardCrossRef::class], version = 1)
abstract class YuGiOhDatabase : RoomDatabase() {

	abstract fun deckDao(): DeckDao

	abstract fun cardDao(): CardDao

}
