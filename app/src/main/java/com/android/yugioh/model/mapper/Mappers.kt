package com.android.yugioh.model.mapper

import com.android.yugioh.database.entities.CardEntity
import com.android.yugioh.database.entities.DeckMetadata
import com.android.yugioh.database.entities.FormatDbInfo
import com.android.yugioh.domain.data.Card
import com.android.yugioh.domain.data.Card.BanListState
import com.android.yugioh.domain.data.Card.FormatCard
import com.android.yugioh.domain.data.DeckItem
import com.android.yugioh.domain.data.MonsterCard
import com.android.yugioh.domain.data.MonsterCard.Companion.AttributeMonster
import com.android.yugioh.domain.data.MonsterCard.Companion.MonsterType
import com.android.yugioh.domain.data.MonsterCard.Companion.RaceMonsterCard
import com.android.yugioh.domain.data.SkillCard
import com.android.yugioh.domain.data.SkillCard.Companion.RaceSkill
import com.android.yugioh.domain.data.SpellTrapCard
import com.android.yugioh.domain.data.SpellTrapCard.Companion.TypeSpellTrap

fun CardEntity.toDomain(): Card {
	val domainFormat = this.formatInfo?.let { info ->
		Card.Format(
			tcg = info.tcgState?.let { FormatCard.TCG(BanListState.valueOf(it)) }, // Asumiendo BanListState es un enum con valueOf
			ocg = info.ocgState?.let { FormatCard.OCG(BanListState.valueOf(it)) },
			goat = info.goatState?.let { FormatCard.GOAT(BanListState.valueOf(it)) },
			duelLinks = info.duelLinksState?.let { FormatCard.DuelLinks(BanListState.valueOf(it)) },
			rushDuel = info.rushState?.let { FormatCard.RushDuel(BanListState.valueOf(it)) },
			speedDuel = info.speedState?.let { FormatCard.SpeedDuel(BanListState.valueOf(it)) }
		)
	}

	return when (this.cardCategory) {
		"MONSTER" -> MonsterCard(
			id = this.id,
			name = this.name,
			// Debes tener una función que busque el Enum TypeMonster basado en el string this.type
			type = MonsterType.values().first { it.typeName == this.type },
			description = this.description,
			race = RaceMonsterCard.values().first { it.raceName == this.race },
			archetype = this.archetype,
			cardImages = this.images,
			format = domainFormat,
			attack = this.attack,
			defense = this.defense,
			level = this.level,
			attribute = AttributeMonster.valueOf(this.attribute ?: "DARK"),
			scaleOfPendulum = this.pendulumScale
		)

		"SPELL_TRAP" -> SpellTrapCard(
			id = this.id,
			name = this.name,
			type = TypeSpellTrap.values().first { it.typeName == this.type },
			description = this.description,
			race = SpellTrapCard.Companion.RaceSpellTrap.values()
				.first { it.raceName == this.race },
			archetype = this.archetype,
			cardImages = this.images,
			format = domainFormat
		)

		"SKILL" -> SkillCard(
			// Mapeo similar para Skill...
			id = this.id, name = this.name, description = this.description,
			race = RaceSkill.valueOf(this.race), // Ojo: RaceSkill parece ser Enum directo, no por string value
			archetype = this.archetype, cardImages = this.images
		)

		else -> throw IllegalArgumentException("Uknown card category: ${this.cardCategory}")
	}
}

fun Card.toEntity(): CardEntity {

	val attack: Short?
	val defense: Short?
	val level: Byte?
	val attribute: String?
	val pendulumScale: Byte?
	if (this is MonsterCard) {
		attack = this.attack
		defense = this.defense
		level = this.level
		attribute = this.attribute?.name
		pendulumScale = this.scaleOfPendulum
	} else {
		attack = null
		defense = null
		level = null
		attribute = null
		pendulumScale = null
	}

	val category = when (this) {
		is MonsterCard -> "MONSTER"
		is SpellTrapCard -> "SPELL_TRAP"
		is SkillCard -> "SKILL"
	}

	val formatDbInfo = this.format?.let { fmt ->
		FormatDbInfo(
			tcgState = fmt.tcg?.banListState?.name,
			ocgState = fmt.ocg?.banListState?.name,
			goatState = fmt.goat?.banListState?.name,
			duelLinksState = fmt.duelLinks?.banListState?.name,
			rushState = fmt.rushDuel?.banListState?.name,
			speedState = fmt.speedDuel?.banListState?.name
		)
	}

	return CardEntity(
		id = id,
		name = name,
		description = description,
		type = type.typeName,
		race = race.raceName,
		archetype = archetype,
		images = cardImages,
		formatInfo = formatDbInfo,
		cardCategory = category,
		attack = attack,
		defense = defense,
		level = level,
		attribute = attribute,
		pendulumScale = pendulumScale
	)
}

fun DeckMetadata.toDomain() = DeckItem(
	id = deck.id,
	name = deck.name,
	createdAt = deck.createdAt,
	isLegal = totalMain >= 40 && totalMain <= 60 && totalSide >= 0 && totalSide <= 15,
)
