package com.android.yugioh.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation

data class DeckWithCards(
	@Embedded val deck: DeckEntity,

	@Relation(
        parentColumn = "id",
        entityColumn = "deckId",
        entity = DeckCardCrossRef::class
    )
    val entries: List<DeckCardEntry>
)

// Clase intermedia para traer la info de la carta + en qué sección está
data class DeckCardEntry(
    @Embedded val crossRef: DeckCardCrossRef,

    @Relation(
        parentColumn = "cardId",
        entityColumn = "id"
    )
    val card: CardEntity
)

data class DeckMetadata(
    @Embedded val deck: DeckEntity,
    @ColumnInfo(name = "total_main") val totalMain: Int,
    @ColumnInfo(name = "total_side") val totalSide: Int
)