package com.android.yugioh.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.yugioh.domain.data.Card

@Entity(tableName = "card")
data class CardEntity(
	@PrimaryKey(autoGenerate = false)
    val id: Int, //Konami ID
	val name: String,
	val description: String,
	val type: String,
	val race: String,
	val archetype: String?,
	val images: List<Card.Image>,
	@Embedded
    val formatInfo: FormatDbInfo?,
    //"MONSTER", "SPELL_TRAP", "SKILL"
	@ColumnInfo(name = "card_category")
    val cardCategory: String,
	val attack: Short? = null,
	val defense: Short? = null,
	val level: Byte? = null,
	val attribute: String? = null,
	@ColumnInfo(name = "pendulum_scale")
	val pendulumScale: Byte? = null
)
