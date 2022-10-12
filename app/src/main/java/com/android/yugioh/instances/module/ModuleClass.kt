package com.android.yugioh.instances.module

import com.android.yugioh.model.api.YuGiOhApi
import com.android.yugioh.model.data.Card
import com.android.yugioh.model.data.MonsterCard
import com.android.yugioh.model.data.SkillCard
import com.android.yugioh.model.data.SpellTrapCard
import com.android.yugioh.model.data.SpellTrapCard.CREATOR.TypeSpellTrap
import com.android.yugioh.model.data.SpellTrapCard.CREATOR.RaceSpellTrap
import com.android.yugioh.model.data.SkillCard.CREATOR.RaceSkill
import com.android.yugioh.model.data.SkillCard.CREATOR.TypeSkill
import com.android.yugioh.model.data.MonsterCard.CREATOR.RaceMonsterCard
import com.android.yugioh.model.data.MonsterCard.CREATOR.MonsterType
import com.android.yugioh.model.data.MonsterCard.CREATOR.AttributeMonster
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.Type
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleClass {
	
	@Singleton
	@Provides
	fun provideApiService(): YuGiOhApi = Retrofit.Builder().run {
		addConverterFactory(ScalarsConverterFactory.create())
		addConverterFactory(GsonConverterFactory.create())
		baseUrl("https://db.ygoprodeck.com/api/v7/")
		build().create(YuGiOhApi::class.java)
	}
	
	@Singleton
	@Provides
	fun provideGson(): Gson = GsonBuilder().run {
		registerTypeAdapter(Card::class.java, CardDeserializeAdapter())
		create()
	}
	
	private class CardDeserializeAdapter : JsonDeserializer<Card>, DeserializeCard {
		
		override fun deserialize(
			json: JsonElement,
			typeOfT: Type,
			context: JsonDeserializationContext
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
							id,
							name,
							deserializeStringToEnumValue(
								type,
								TypeSpellTrap.values()
							),
							description,
							deserializeStringToEnumValue(
								get("race").asString,
								RaceSpellTrap.values()
							),
							archetype,
							images,
							deserializeFormatAndBanInfo(this)
						)
					TypeSkill.SKILL_CARD.toString() ->
						SkillCard(
							id,
							name,
							description,
							deserializeStringToEnumValue(
								get("race").asString,
								RaceSkill.values()
							),
							archetype,
							images
						)
					else ->
						MonsterCard(
							id,
							name,
							deserializeStringToEnumValue(
								type,
								MonsterType.values()
							),
							description,
							deserializeStringToEnumValue(
								get("race").asString,
								RaceMonsterCard.values()
							),
							archetype,
							images,
							if (type != "${MonsterType.TOKEN}")
								deserializeFormatAndBanInfo(this)
							else
								null,
							with(get("atk")) {
								if (!isJsonNull)
									return@with asShort
								0
							},
							get("def")?.let {
								if (!it.isJsonNull)
									return@let it.asShort
								0
							},
							get("level")?.asByte ?: get("linkval").asByte,
							deserializeStringToEnumValue(
								get("attribute").asString,
								AttributeMonster.values()
							),
							get("scale")?.asByte
						)
				}
			}
			
		}
	}
}