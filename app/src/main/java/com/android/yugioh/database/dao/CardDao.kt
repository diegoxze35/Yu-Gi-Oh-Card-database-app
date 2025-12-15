package com.android.yugioh.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.yugioh.database.entities.CardEntity

@Dao
interface CardDao {
    // Inserta una carta. Si el ID ya existe, no hace nada (retorna -1).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCard(card: CardEntity): Long

    // Inserta una lista masiva de cartas (útil para la carga inicial de la base de datos)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllCards(cards: List<CardEntity>)

    @Query("SELECT * FROM card WHERE id = :cardId LIMIT 1")
    suspend fun getCardById(cardId: Int): CardEntity?
}