package com.android.yugioh.model.data

import android.os.Parcel
import android.os.Parcelable
import com.android.yugioh.R

data class SpellTrapCard(
	override val id: Int,
	override val name: String,
	override val type: TypeSpellTrap,
	override val description: String,
	override val race: RaceSpellTrap,
	override val archetype: String?,
	override val card_images: List<Image>,
	override val banListInfo: BanListInfo?
) : Card(id, name, type, description, race, archetype, card_images, banListInfo) {
	
	constructor(parcel: Parcel) : this(
		parcel.readInt(),
		parcel.readString()!!,
		TypeSpellTrap.valueOf(parcel.readString()!!),
		parcel.readString()!!,
		RaceSpellTrap.valueOf(parcel.readString()!!),
		parcel.readString(),
		with(mutableListOf<Image>()) {
			parcel.readTypedList(this, Image.CREATOR)
			this
		},
		parcel.readTypedObject(BanListInfo.CREATOR)
	)
	
	companion object CREATOR : Parcelable.Creator<SpellTrapCard> {
		
		override fun createFromParcel(source: Parcel): SpellTrapCard {
			return SpellTrapCard(source)
		}
		
		override fun newArray(size: Int): Array<SpellTrapCard?> {
			return arrayOfNulls(size)
		}
		
		enum class TypeSpellTrap(
			override val type: String,
			override val color: Int,
			override val icon: Int
		) : Type {
			
			SPELL_CARD("Spell Card", R.color.colorSpellCard, R.drawable.spell_card_s),
			TRAP_CARD("Trap Card", R.color.colorTrampCard, R.drawable.trap_card_s);
			
			override fun getEnumName(): String = this.name
			
			override fun toString(): String = type
			
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
			
			override fun getEnumName(): String = this.name
			
		}
	}
}
