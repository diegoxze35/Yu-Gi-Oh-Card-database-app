package com.android.yugioh.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
	tableName = "deck",
	indices = [Index(value = ["name"], unique = true)]
)
data class DeckEntity(
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "created_at") val createdAt: Date
)