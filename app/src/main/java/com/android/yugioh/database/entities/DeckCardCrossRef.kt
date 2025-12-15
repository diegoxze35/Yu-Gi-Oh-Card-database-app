package com.android.yugioh.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
	tableName = "deck_card_cross_ref",
	primaryKeys = ["deckId", "cardId", "section"], // Clave compuesta para permitir la misma carta en Main y Side
	foreignKeys = [
		ForeignKey(
			entity = DeckEntity::class,
			parentColumns = ["id"],
			childColumns = ["deckId"],
			onDelete = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = CardEntity::class,
			parentColumns = ["id"],
			childColumns = ["cardId"]
		) // No Cascade delete en cartas, son catálogo global
	],
	indices = [Index(value = ["cardId"]), Index(value = ["deckId"])],

)
data class DeckCardCrossRef(
	val deckId: Int,
	val cardId: Int,
	@ColumnInfo(defaultValue = "1")
	val quantity: Int,
	val section: DeckSection, // MAIN, SIDE
)