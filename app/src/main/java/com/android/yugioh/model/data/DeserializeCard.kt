package com.android.yugioh.model.data

import com.android.yugioh.domain.data.Card
import com.android.yugioh.domain.data.Card.BanListState
import com.android.yugioh.domain.data.Card.FormatCard
import com.android.yugioh.domain.data.Card.Image
import com.android.yugioh.domain.data.DomainEnum
import com.google.gson.JsonArray
import com.google.gson.JsonObject

interface DeserializeCard {
	
	fun getFormatList(): List<FormatCard>
	
	companion object {
		const val IMAGE_URL_FIELD = "image_url"
		const val IMAGE_URL_FIELD_SMALL = "image_url_small"
		const val BAN_FIELD = "banlist_info"
		const val MISC_INFO = "misc_info"
		const val FORMATS_FIELD = "formats"
	}
	
	fun <T : DomainEnum> deserializeStringToEnumValue(value: String, enum: Array<T>): T {
		return enum.find {
			value == it.toString()
		} ?: enum.last()
	}
	
	fun deserializeListImage(array: JsonArray): List<Image> = array.map {
		with(it.asJsonObject) {
			Image(get(IMAGE_URL_FIELD).asString, get(IMAGE_URL_FIELD_SMALL).asString)
		}
	}
	
	fun deserializeFormatAndBanInfo(info: JsonObject): Card.Format {
		val listJsonInfo = with(info) {
			get(MISC_INFO).asJsonArray[0].asJsonObject.getAsJsonArray(FORMATS_FIELD).map {
				it.asString
			}
		}
		val banObject: JsonObject? = info.getAsJsonObject(BAN_FIELD)
		var formatFinal = Card.Format(null, null, null, null, null, null)
		getFormatList().forEach { current ->
			when (current) {
				is FormatCard.TCG -> formatFinal = formatFinal.copy(tcg = current.also {
					it.getFromJsonResponse(listJsonInfo, banObject?.get("ban_tcg")?.asString)
				})
				is FormatCard.OCG -> formatFinal = formatFinal.copy(ocg = current.also {
					it.getFromJsonResponse(listJsonInfo, banObject?.get("ban_ocg")?.asString)
				})
				is FormatCard.GOAT -> formatFinal = formatFinal.copy(
					goat = current.also {
						it.getFromJsonResponse(listJsonInfo, banObject?.get("ban_goat")?.asString)
					}
				)
				is FormatCard.DuelLinks -> formatFinal = formatFinal.copy(duelLinks = current.also {
					it.getFromJsonResponse(listJsonInfo, null)
				})
				is FormatCard.RushDuel -> formatFinal = formatFinal.copy(rushDuel = current.also {
					it.getFromJsonResponse(listJsonInfo, null)
				})
				is FormatCard.SpeedDuel -> formatFinal = formatFinal.copy(speedDuel = current.also {
					it.getFromJsonResponse(listJsonInfo, null)
				})
			}
		}
		return formatFinal
	}
	
	private fun FormatCard.getFromJsonResponse(
		response: List<String>, banResponse: String?
	): FormatCard? {
		return if (response.contains(format)) {
			banListState =
				banResponse?.let { deserializeStringToEnumValue(it, BanListState.values()) }
					?: BanListState.UNLIMITED
			this
		} else null
	}
}


