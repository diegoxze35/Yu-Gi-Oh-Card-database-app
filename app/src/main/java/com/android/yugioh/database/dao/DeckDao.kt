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
		    SELECT deckId as id, name, created_at,
        		SUM(CASE WHEN section = 'MAIN' THEN quantity ELSE 0 END) AS total_main, 
        		SUM(CASE WHEN section = 'SIDE' THEN quantity ELSE 0 END) AS total_side 
    		FROM deck_card_cross_ref INNER JOIN deck on deckId = deck.id GROUP BY deckId
			  """)
	fun getAllDecksMetadata(): Flow<List<DeckMetadata>>

	@Transaction
	@Query("SELECT * FROM deck")
	fun getAllDecks(): Flow<List<DeckEntity>>

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertDeck(deck: DeckEntity): Long

	@Transaction
	@Query("SELECT * FROM deck WHERE id = :deckId")
	fun getDeckWithCards(deckId: Int): DeckWithCards

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun insertCardToDeck(crossRef: DeckCardCrossRef)

	@Delete
    fun removeCardFromDeck(crossRef: DeckCardCrossRef)

}
