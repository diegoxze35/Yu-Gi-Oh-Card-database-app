package com.android.yugioh.instances.module

import com.android.yugioh.model.data.Card
import com.android.yugioh.model.data.Card.BanListState
import com.android.yugioh.model.data.Card.FormatCard
import com.android.yugioh.model.data.Card.Image
import com.google.gson.JsonArray
import com.google.gson.JsonObject

interface DeserializeCard {
	
	companion object {
		const val IMAGE_URL_FIELD = "image_url"
		const val IMAGE_URL_FIELD_SMALL = "image_url_small"
		const val BAN_FIELD = "banlist_info"
		const val MISC_INFO = "misc_info"
		const val FORMATS_FIELD = "formats"
	}
	
	fun <T : Enum<T>> deserializeStringToEnumValue(value: String, enum: Array<T>): T {
		return enum.find {
			value == it.toString()
		}!!
	}
	
	fun deserializeListImage(array: JsonArray): List<Image> = array.map {
		with(it.asJsonObject) {
			Image(get(IMAGE_URL_FIELD).asString, get(IMAGE_URL_FIELD_SMALL).asString)
		}
	}
	
	fun deserializeFormatAndBanInfo(info: JsonObject): Card.Format {
		
		var formatFinal = Card.Format(null, null, null)
		val formats = FormatCard::class.sealedSubclasses.map { it.objectInstance!! }
		formats.forEach { current ->
			when (current) {
				FormatCard.TCG -> formatFinal = formatFinal.copy(
					tcg = current.getFromJson("ban_tcg", info) as FormatCard.TCG?
				)
				FormatCard.OCG -> formatFinal = formatFinal.copy(
					ocg = current.getFromJson("ban_ocg", info) as FormatCard.OCG?
				)
				FormatCard.RushDuel -> {
					info.get(MISC_INFO).asJsonArray[0].asJsonObject.getAsJsonArray(FORMATS_FIELD)
						.find { it.asString == current.format }?.let {
							formatFinal =
								formatFinal.copy(rushDuel = current as FormatCard.RushDuel)
						}
				}
			}
		}
		return formatFinal
	}
	
	private fun FormatCard.getFromJson(fieldName: String, json: JsonObject): FormatCard? {
		val arrayFormats: JsonArray =
			json.get(MISC_INFO).asJsonArray[0].asJsonObject.getAsJsonArray(FORMATS_FIELD)
		return arrayFormats.find { it.asString == format }?.let { _ ->
			banListState = json.getAsJsonObject(BAN_FIELD)?.get(fieldName)?.let {
				deserializeStringToEnumValue(it.asString, BanListState.values())
			} ?: BanListState.UNLIMITED
			this
		}
	}
}