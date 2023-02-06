package com.android.yugioh.di

import android.content.Context
import android.widget.ArrayAdapter
import com.android.yugioh.R
import com.android.yugioh.domain.GetAllArchetypesUseCase
import com.android.yugioh.domain.data.SpellTrapCard.Companion.RaceSpellTrap
import com.android.yugioh.domain.data.MonsterCard.Companion.RaceMonsterCard
import com.android.yugioh.domain.data.MonsterCard.Companion.MonsterType
import com.android.yugioh.domain.data.MonsterCard.Companion.AttributeMonster
import com.android.yugioh.domain.data.SkillCard.Companion.RaceSkill
import com.android.yugioh.model.Result
import com.android.yugioh.model.data.CardDeserializer
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.runBlocking
import javax.inject.Named

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

	private const val LAYOUT_RESOURCE = R.layout.item_auto_complete_text_view
	private const val INIT = 1u
	private const val STEP = 50u
	private const val MAX_LEVEL = 12u
	private const val MAX_SCALE = 13u
	private const val MAX_ATK = 5000u
	private const val FIRST_ELEMENT_DROP_DOWN_MENU = "Any"  //should be a string resource
	const val MONSTER_TYPE_VALUES = "MONSTER_TYPE_VALUES"
	const val ATTRIBUTE_MONSTER_VALUES = "ATTRIBUTE_MONSTER_VALUES"
	const val LEVEL_MONSTERS_SEQUENCE = "LEVEL_MONSTERS_SEQUENCE"
	const val ATK_AND_DEFENSE_MONSTERS_SEQUENCE = "ATK_AND_DEFENSE_MONSTERS_SEQUENCE"
	const val SCALE_PENDUMULUM_SEQUENCE = "SCALE_PENDUMULUM_SEQUENCE"
	const val RACE_MONSTER_CARD_VALUES = "RACE_MONSTER_CARD_VALUES"
	const val RACE_SPELL_CARD_VALUES = "RACE_SPELL_TRAP_VALUES"
	const val RACE_TRAP_CARD_VALUES = "RACE_TRAP_VALUES"
	const val RACE_SKILL_CARD_VALUES = "RACE_SKILL_CARD_VALUES"
	const val CARD_FORMATS = "CARD_FORMATS"

	@Provides
	@FragmentScoped
	fun providePicasso(): Picasso = Picasso.get()

	@Named("archetypes")
	@Provides
	@FragmentScoped
	fun provideArchetypes(getAllArchetypesUseCase: GetAllArchetypesUseCase): List<String> =
		runBlocking {
			when (val archetypes = getAllArchetypesUseCase()) {
				is Result.Success -> archetypes.body.sorted()
				else -> emptyList()
			}
		}

	@Provides
	@FragmentScoped
	fun provideAdapters(@ApplicationContext context: Context): Map<String, ArrayAdapter<Any>> =
		with(listOf(FIRST_ELEMENT_DROP_DOWN_MENU)) {
			mapOf(
				MONSTER_TYPE_VALUES to ArrayAdapter(
					context, LAYOUT_RESOURCE, plus(
						MonsterType.values()
					)
				),
				ATTRIBUTE_MONSTER_VALUES to ArrayAdapter(
					context, LAYOUT_RESOURCE, plus(
						AttributeMonster.values()
					)
				),
				LEVEL_MONSTERS_SEQUENCE to ArrayAdapter(context,
					LAYOUT_RESOURCE,
					plus(
						generateSequence(INIT) {
							return@generateSequence if (it < MAX_LEVEL) it + 1u else null
						}
					)),
				ATK_AND_DEFENSE_MONSTERS_SEQUENCE to ArrayAdapter(context,
					LAYOUT_RESOURCE,
					plus(
						generateSequence(STEP) {
							return@generateSequence if (it != MAX_ATK) it + STEP else null
						}
					)),
				SCALE_PENDUMULUM_SEQUENCE to ArrayAdapter(context,
					LAYOUT_RESOURCE,
					plus(
						generateSequence(INIT) {
							return@generateSequence if (it < MAX_SCALE) it + 1u else null
						}
					)),
				RACE_MONSTER_CARD_VALUES to ArrayAdapter(
					context,
					LAYOUT_RESOURCE,
					plus(RaceMonsterCard.values())
				),
				RACE_SPELL_CARD_VALUES to ArrayAdapter(
					context,
					LAYOUT_RESOURCE,
					plus(RaceSpellTrap.values().run { take(size - 1) })
				),
				RACE_TRAP_CARD_VALUES to ArrayAdapter(
					context, LAYOUT_RESOURCE, plus(
						listOf(
							"${RaceSpellTrap.NORMAL}",
							"${RaceSpellTrap.CONTINUOUS}",
							"${RaceSpellTrap.COUNTER}"
						)
					)
				),
				RACE_SKILL_CARD_VALUES to ArrayAdapter(
					context, LAYOUT_RESOURCE, plus(
						RaceSkill.values()
					)
				),
				CARD_FORMATS to ArrayAdapter(context, LAYOUT_RESOURCE, plus(
					CardDeserializer.getFormatList().map { it.format }
				))
			)
		}
}