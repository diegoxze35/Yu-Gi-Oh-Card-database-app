package com.android.yugioh.instances.module

import com.android.yugioh.model.data.Card.BanListState
import com.android.yugioh.model.data.Card.CardFormat
import com.android.yugioh.model.data.Card.Format
import com.android.yugioh.model.data.Card.Image
import com.google.gson.JsonArray
import com.google.gson.JsonObject

interface DeserializeCard {
	
	fun <T : Enum<T>> deserializeStringToEnumValue(value: String, enum: Array<T>): T {
		return enum.find {
			value == it.toString()
		}!!
	}
	
	fun deserializeListImage(array: JsonArray): List<Image> {
		val images = mutableListOf<Image>()
		array.forEach {
			with(it.asJsonObject) {
				images.add(Image(get("image_url").asString, get("image_url_small").asString))
			}
		}
		return images
	}
	
	fun deserializeFormatAndBanInfo(info: JsonObject): Format {
		
		/*	formats = [
				0 - TCG
				1 - OCG
				2 - Rush Duel
			]
		*/
		val formats = CardFormat.values()
		val currentFormats = arrayOfNulls<CardFormat?>(formats.size)
		val arrayFormats =
			info.get("misc_info").asJsonArray[0].asJsonObject.getAsJsonArray("formats")
		for (index in currentFormats.indices) {
			currentFormats[index] = arrayFormats.find {
				it.asString == formats[index].toString()
			}?.let {
				deserializeStringToEnumValue(it.asString, formats)
			}
		}
		val currentsStatesBan = arrayOfNulls<BanListState?>(formats.size - 1) //[TCG, OCG]
		val formatsBan = arrayOf("ban_tcg", "ban_ocg")
		val banListInfo: JsonObject? = info.getAsJsonObject("banlist_info")
		for (index in formatsBan.indices) {
			if (currentFormats[index] == null)
				continue
			currentsStatesBan[index] = banListInfo?.get(formatsBan[index])?.let {
				deserializeStringToEnumValue(it.asString, BanListState.values())
			} ?: BanListState.UNLIMITED
		}
		return Format(currentFormats, currentsStatesBan[0], currentsStatesBan[1])
	}
}