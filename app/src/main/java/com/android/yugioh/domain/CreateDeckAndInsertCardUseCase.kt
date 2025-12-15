package com.android.yugioh.domain

import com.android.yugioh.database.dao.CardDao
import com.android.yugioh.database.dao.DeckDao
import com.android.yugioh.database.entities.DeckCardCrossRef
import com.android.yugioh.database.entities.DeckEntity
import com.android.yugioh.database.entities.DeckSection
import com.android.yugioh.domain.data.Card
import com.android.yugioh.domain.data.MonsterCard
import com.android.yugioh.model.mapper.toEntity
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateDeckAndInsertCardUseCase @Inject constructor(
	private val cardDao: CardDao,
	private val deckDao: DeckDao
) {
	suspend operator fun invoke(
		deckName: String,
		card: Card,
		quantity: Int = 1
	) {
		val newCardEntity = card.toEntity()
		val cardId = cardDao.insertCard(newCardEntity)
		assert(card.id == cardId.toInt()) {
			"Error inserting card with id ${card.id}"
		}
		val newDeck = DeckEntity(
			name = deckName,
			createdAt = Date(),
		)
		val newId = deckDao.insertDeck(newDeck)
		assert(newId > 0) {
			"Error inserting deck with name $deckName"
		}
		val deckSection = if (card is MonsterCard) {
			when (card.type) {
				MonsterCard.Companion.MonsterType.FUSION_MONSTER,
				MonsterCard.Companion.MonsterType.PENDULUM_EFFECT_FUSION_MONSTER,
				MonsterCard.Companion.MonsterType.XYZ_MONSTER,
				MonsterCard.Companion.MonsterType.XYZ_PENDULUM_EFFECT_MONSTER,
				MonsterCard.Companion.MonsterType.SYNCHRO_MONSTER,
				MonsterCard.Companion.MonsterType.SYNCHRO_TUNER_MONSTER,
				MonsterCard.Companion.MonsterType.SYNCHRO_PENDULUM_EFFECT_MONSTER,
				MonsterCard.Companion.MonsterType.LINK_MONSTER -> DeckSection.SIDE
				else -> DeckSection.MAIN
			}
		} else {
			DeckSection.MAIN
		}
		val crossRef = DeckCardCrossRef(
			deckId = newId.toInt(),
			cardId = card.id,
			section = deckSection,
			quantity = quantity
		)
		deckDao.insertCardToDeck(crossRef = crossRef)

	}
}


