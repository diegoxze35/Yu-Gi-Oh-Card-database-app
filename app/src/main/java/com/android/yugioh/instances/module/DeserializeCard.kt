package com.android.yugioh.instances.module

import com.android.yugioh.model.data.Card.Image
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.android.yugioh.model.data.Card.BanListInfo
import com.android.yugioh.model.data.Card.BanListInfo.CREATOR.BanListState

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
	
	fun deserializeBanInfo(info: JsonObject?): BanListInfo {
		val currentStates = arrayOf(BanListState.UNLIMITED, BanListState.UNLIMITED)
		return info?.let {
			arrayOf(
				it.get("ban_tcg")?.asString,
				it.get("ban_ocg")?.asString
			).forEachIndexed { index, str ->
				str?.let {
					currentStates[index] = deserializeStringToEnumValue(str, BanListState.values())
				}
			}
			BanListInfo(currentStates[0], currentStates[1])
		} ?: BanListInfo(currentStates[0], currentStates[1])
	}
}