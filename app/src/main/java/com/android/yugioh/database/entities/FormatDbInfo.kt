package com.android.yugioh.database.entities

import androidx.room.ColumnInfo

data class FormatDbInfo(
    // Si el valor es NULL, significa que el formato no aplica o no existe.
    // Si tiene valor, es el estado de la BanList (ej: "BANNED", "LIMITED", "UNLIMITED")
	@ColumnInfo(name = "format_tcg_state") val tcgState: String?,
	@ColumnInfo(name = "format_ocg_state") val ocgState: String?,
	@ColumnInfo(name = "format_goat_state") val goatState: String?,
	@ColumnInfo(name = "format_duel_links_state") val duelLinksState: String?,
	@ColumnInfo(name = "format_rush_state") val rushState: String?,
	@ColumnInfo(name = "format_speed_state") val speedState: String?
)