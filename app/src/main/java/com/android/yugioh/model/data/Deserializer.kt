package com.android.yugioh.model.data

import com.android.yugioh.domain.data.Card
import com.android.yugioh.domain.data.DomainEnum
import com.google.gson.JsonArray
import com.google.gson.JsonObject

object Deserializer : DeserializeCard {

	private const val IMAGE_URL_FIELD = "image_url"
	private const val IMAGE_URL_FIELD_SMALL = "image_url_small"
	private const val BAN_FIELD = "banlist_info"
	private const val MISC_INFO = "misc_info"
	private const val FORMATS_FIELD = "formats"
	private const val BAN_TCG = "ban_tcg"
	private const val BAN_OCG = "ban_ocg"
	private const val BAN_GOAT = "ban_goat"

	override fun <T : DomainEnum> deserializeStringToEnumValue(
		value: String,
		enum: Array<out T>
	): T {
		return enum.find {
			value == it.toString()
		} ?: enum.last()
	}

	override fun deserializeListImage(array: JsonArray): List<Card.Image> {
		return array.map {
			with(it.asJsonObject) {
				Card.Image(get(IMAGE_URL_FIELD).asString, get(IMAGE_URL_FIELD_SMALL).asString)
			}
		}
	}

	override fun deserializeFormatAndBanInfo(info: JsonObject): Card.Format {
		val listJsonInfo = with(info) {
			get(MISC_INFO).asJsonArray[0].asJsonObject.getAsJsonArray(FORMATS_FIELD).map {
				it.asString
			}
		}

		operator fun <E> List<E>.component6(): E {
			return this[5]
		}

		val banResponse: JsonObject? = info.getAsJsonObject(BAN_FIELD)
		val (tcg, ocg, goat, rushDuel, duelLinks, speedDuel) = listOf(
			Card.FormatCard.TCG(banListState = null) to BAN_TCG,
			Card.FormatCard.OCG(banListState = null) to BAN_OCG,
			Card.FormatCard.GOAT(banListState = null) to BAN_GOAT,
			Card.FormatCard.RushDuel(banListState = null) to null,
			Card.FormatCard.DuelLinks(banListState = null) to null,
			Card.FormatCard.SpeedDuel(banListState = null) to null
		).map {
			if (listJsonInfo.contains(it.first.format)) {
				it.first.banListState = banResponse?.get(it.second)?.asString?.let { strBan ->
					deserializeStringToEnumValue(
						strBan,
						enumValues<Card.BanListState>()
					)
				} ?: Card.BanListState.UNLIMITED
				it.first
			} else null
		}
		return Card.Format(
			tcg as? Card.FormatCard.TCG?,
			ocg as? Card.FormatCard.OCG?,
			goat as? Card.FormatCard.GOAT?,
			rushDuel as? Card.FormatCard.DuelLinks?,
			duelLinks as? Card.FormatCard.RushDuel?,
			speedDuel as? Card.FormatCard.SpeedDuel?
		)
	}

}

