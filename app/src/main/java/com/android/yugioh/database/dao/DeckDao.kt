package com.android.yugioh.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.android.yugioh.database.entities.DeckCardCrossRef
import com.android.yugioh.database.entities.DeckEntity
import com.android.yugioh.database.entities.DeckMetadata
import com.android.yugioh.database.entities.DeckWithCards
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

	@Transaction
	@Query("""
		SELECT deckId,
  			SUM(quantity) FILTER (WHERE section = 'MAIN') AS total_main,
			SUM(quantity) FILTER (WHERE section = 'SIDE') AS total_side
			FROM deck_card_cross_ref GROUP BY deckId;
			  """)
	suspend fun getAllDecksMetadata(): Flow<List<DeckMetadata>>

	@Transaction
	@Query("SELECT * FROM deck")
	fun getAllDecks(): Flow<List<DeckEntity>>

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertDeck(deck: DeckEntity): Long

	@Transaction
	@Query("SELECT * FROM deck WHERE id = :deckId")
	suspend fun getDeckWithCards(deckId: Int): DeckWithCards

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertCardToDeck(crossRef: DeckCardCrossRef)

	@Delete
    suspend fun removeCardFromDeck(crossRef: DeckCardCrossRef)

}
