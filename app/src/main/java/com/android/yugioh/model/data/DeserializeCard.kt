package com.android.yugioh.model.data

import com.android.yugioh.domain.data.Card
import com.android.yugioh.domain.data.Card.Image
import com.android.yugioh.domain.data.DomainEnum
import com.google.gson.JsonArray
import com.google.gson.JsonObject

interface DeserializeCard {
	fun <T : DomainEnum> deserializeStringToEnumValue(value: String, enum: Array<out T>): T
	fun deserializeListImage(array: JsonArray): List<Image>
	fun deserializeFormatAndBanInfo(info: JsonObject): Card.Format
}