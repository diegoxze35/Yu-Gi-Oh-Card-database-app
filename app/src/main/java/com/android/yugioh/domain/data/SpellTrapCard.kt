package com.android.yugioh.domain.data

import com.android.yugioh.R
import kotlin.Enum

data class SpellTrapCard(
	override val id: Int,
	override val name: String,
	override val type: TypeSpellTrap,
	override val description: String,
	override val race: RaceSpellTrap,
	override val archetype: String?,
	override val cardImages: List<Image>,
	override val format: Format?
) : Card(id, name, type, description, race, archetype, cardImages, format) {
	
	companion object {
		
		enum class TypeSpellTrap(
			override val type: String,
			override val color: Int,
			override val icon: Int
		) : Type {
			
			SPELL_CARD("Spell Card", R.color.color_spell_card, R.drawable.spell_card_s_info),
			TRAP_CARD("Trap Card", R.color.color_tramp_card, R.drawable.trap_card_s_info);
			
			override fun toString(): String = type
			
			override val enum: Enum<*>
				get() = this
			
		}
		
		enum class RaceSpellTrap(override val race: String, override val icon: Int) : Race {
			
			/*Spell and trap*/
			NORMAL("Normal", R.drawable.normal_s),
			CONTINUOUS("Continuous", R.drawable.continuous_s),
			
			/*Only Spell*/
			EQUIP("Equip", R.drawable.equip_s),
			FIELD("Field", R.drawable.field_s),
			QUICK_PLAY("Quick-Play", R.drawable.quick_game_s),
			RITUAL("Ritual", R.drawable.ritual_s),
			
			/*Only Trap*/
			COUNTER("Counter", R.drawable.counter_s);
			
			override fun toString(): String = race
			
			override val enum: Enum<*>
				get() = this
			
		}
	}
}
