package com.android.yugioh.model.data

import com.android.yugioh.domain.data.Card
import com.android.yugioh.domain.data.SpellTrapCard
import com.android.yugioh.domain.data.MonsterCard
import com.android.yugioh.domain.data.MonsterCard.Companion.MonsterType
import com.android.yugioh.domain.data.SkillCard
import com.android.yugioh.domain.data.SkillCard.Companion.TypeSkill
import com.android.yugioh.domain.data.Card.FormatCard
import com.android.yugioh.domain.data.SpellTrapCard.Companion.TypeSpellTrap
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object CardDeserializer : JsonDeserializer<Card>, DeserializeCard {

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
			val images = deserializeListImage(get("card_images").asJsonArray)

			when (val type = get("type").asString) {
				TypeSpellTrap.SPELL_CARD.toString(), TypeSpellTrap.TRAP_CARD.toString() ->
					SpellTrapCard(
						id, name, deserializeStringToEnumValue(
							type, enumValues()
						), description, deserializeStringToEnumValue(
							get("race").asString, enumValues()
						), archetype, images, deserializeFormatAndBanInfo(this)
					)
				TypeSkill.SKILL_CARD.toString() -> SkillCard(
					id, name, description, deserializeStringToEnumValue(
						get("race").asString, enumValues()
					), archetype, images
				)
				else -> MonsterCard(
					id,
					name,
					deserializeStringToEnumValue(
						type, enumValues()
					),
					description,
					deserializeStringToEnumValue(
						get("race").asString, enumValues()
					),
					archetype,
					images,
					if (type != "${MonsterType.TOKEN}") deserializeFormatAndBanInfo(this)
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
					deserializeStringToEnumValue(
						get("attribute").asString, enumValues()
					),
					get("scale")?.asByte
				)
			}
		}
	}

	override fun getFormatList(): List<FormatCard> = listOf(
		FormatCard.TCG(banListState = null),
		FormatCard.OCG(banListState = null),
		FormatCard.RushDuel(banListState = null),
		FormatCard.DuelLinks(banListState = null)
	)

}