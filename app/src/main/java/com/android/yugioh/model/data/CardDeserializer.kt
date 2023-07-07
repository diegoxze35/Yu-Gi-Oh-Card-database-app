package com.android.yugioh.model.data

import com.android.yugioh.domain.data.Card
import com.android.yugioh.domain.data.SpellTrapCard
import com.android.yugioh.domain.data.MonsterCard
import com.android.yugioh.domain.data.MonsterCard.Companion.MonsterType
import com.android.yugioh.domain.data.SkillCard
import com.android.yugioh.domain.data.SkillCard.Companion.TypeSkill
import com.android.yugioh.domain.data.SpellTrapCard.Companion.TypeSpellTrap
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object CardDeserializer : JsonDeserializer<Card> {
	private val deserializer = Deserializer
	override fun deserialize(
		json: JsonElement,
		typeOfT: Type?,
		context: JsonDeserializationContext?
	): Card {
		return json.asJsonObject.run {

			val id = get("id").asInt
			val name = get("name").asString
			val description = get("desc").asString
			val archetype = get("archetype")?.asString
			val images = deserializer.deserializeListImage(get("card_images").asJsonArray)

			when (val type = get("type").asString) {
				TypeSpellTrap.SPELL_CARD.toString(), TypeSpellTrap.TRAP_CARD.toString() ->
					SpellTrapCard(
						id, name, deserializer.deserializeStringToEnumValue(
							type, enumValues()
						), description, deserializer.deserializeStringToEnumValue(
							get("race").asString, enumValues()
						), archetype, images, deserializer.deserializeFormatAndBanInfo(this)
					)
				TypeSkill.SKILL_CARD.toString() -> SkillCard(
					id, name, description, deserializer.deserializeStringToEnumValue(
						get("race").asString, enumValues()
					), archetype, images
				)
				else -> MonsterCard(
					id,
					name,
					deserializer.deserializeStringToEnumValue(
						type, enumValues()
					),
					description,
					deserializer.deserializeStringToEnumValue(
						get("race").asString, enumValues()
					),
					archetype,
					images,
					if (type != "${MonsterType.TOKEN}") deserializer.deserializeFormatAndBanInfo(this)
					else null,
					with(get("atk")) {
						if (!isJsonNull) return@with asShort
						0
					},
					get("def")?.let {
						if (!it.isJsonNull) return@let it.asShort
						0
					},
					get("level")?.asByte ?: get("linkval").asByte,
					deserializer.deserializeStringToEnumValue(
						get("attribute").asString, enumValues()
					),
					get("scale")?.asByte
				)
			}
		}
	}
}