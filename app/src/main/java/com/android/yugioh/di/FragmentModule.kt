package com.android.yugioh.di

import android.content.Context
import android.widget.ArrayAdapter
import com.android.yugioh.R
import com.android.yugioh.domain.GetAllArchetypesUseCase
import com.android.yugioh.domain.data.SpellTrapCard.Companion.RaceSpellTrap
import com.android.yugioh.domain.data.SkillCard.Companion.RaceSkill
import com.android.yugioh.domain.data.MonsterCard.Companion.RaceMonsterCard
import com.android.yugioh.domain.data.MonsterCard.Companion.MonsterType
import com.android.yugioh.domain.data.MonsterCard.Companion.AttributeMonster
import com.android.yugioh.model.Result
import com.android.yugioh.model.data.CardDeserializer
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.*
import javax.inject.Named

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

	private const val EXTRA = "Any" //should be a string resource
	private const val LAYOUT_RESOURCE = R.layout.item_auto_complete_text_view
	private const val INIT = 1u
	private const val STEP = 50u
	private const val MAX_LEVEL = 12u
	private const val MAX_SCALE = 13u
	private const val MAX_ATK = 5000u

	@Provides
	@FragmentScoped
	fun providePicasso(): Picasso = Picasso.get()

	@Named("archetypes")
	@Provides
	@FragmentScoped
	fun provideArchetypes(getAllArchetypesUseCase: GetAllArchetypesUseCase): List<String> =
		runBlocking {
			when (val archetypes = getAllArchetypesUseCase()) {
				is Result.Success -> archetypes.body
				else -> emptyList()
			}
		}

	@Provides
	@FragmentScoped
	fun provideAdapters(@ApplicationContext context: Context): Array<Array<ArrayAdapter<*>>> {

		val firstElement = listOf<Any>(EXTRA)

		val sequenceLevels = generateSequence(INIT) {
			return@generateSequence if (it < MAX_LEVEL) it + 1u else null
		}

		val sequenceAtkAndDef = generateSequence(STEP) {
			return@generateSequence if (it != MAX_ATK) it + STEP else null
		}

		return arrayOf(
			arrayOf(
				ArrayAdapter(context, LAYOUT_RESOURCE, firstElement.plus(MonsterType.values())),
				ArrayAdapter(
					context, LAYOUT_RESOURCE, firstElement.plus(AttributeMonster.values())
				),
				ArrayAdapter(context, LAYOUT_RESOURCE, firstElement.plus(sequenceLevels)),
				ArrayAdapter(context, LAYOUT_RESOURCE, firstElement.plus(sequenceAtkAndDef)),
				ArrayAdapter(context, LAYOUT_RESOURCE, firstElement.plus(sequenceAtkAndDef)),
				ArrayAdapter(
					context, LAYOUT_RESOURCE, firstElement.plus(sequenceLevels).plus(MAX_SCALE)
				)
			),
			arrayOf(
				ArrayAdapter(
					context,
					LAYOUT_RESOURCE,
					firstElement.plus(RaceMonsterCard.values())
				),
				ArrayAdapter(
					context,
					LAYOUT_RESOURCE,
					firstElement.plus(RaceSpellTrap.values().run { (take(size - 1)) })
				),
				ArrayAdapter(
					context, LAYOUT_RESOURCE, arrayOf(
						EXTRA, RaceSpellTrap.NORMAL, RaceSpellTrap.CONTINUOUS, RaceSpellTrap.COUNTER
					)
				),
				ArrayAdapter(context, LAYOUT_RESOURCE, firstElement.plus(RaceSkill.values()))
			),
			arrayOf(
				ArrayAdapter(
					context,
					LAYOUT_RESOURCE,
					firstElement.plus(CardDeserializer.getFormatList().map { it.format })
				),
			)
		)
	}

}