package com.android.yugioh.model.data

import android.os.Parcel
import android.os.Parcelable
import com.android.yugioh.R

data class SkillCard(
	override val id: Int,
	override val name: String,
	override val description: String,
	override val race: RaceSkill,
	override val archetype: String?,
	override val card_images: List<Image>
) : Card(
	id, name, TypeSkill.SKILL_CARD,
	description,
	race, archetype, card_images, null
) {
	
	constructor(parcel: Parcel) : this(
		parcel.readInt(),
		parcel.readString()!!,
		parcel.readString()!!,
		RaceSkill.valueOf(parcel.readString()!!),
		parcel.readString(),
		with(mutableListOf<Image>()) {
			parcel.readTypedList(this, Image.CREATOR)
			this
		}
	)
	
	override fun writeToParcel(dest: Parcel, flags: Int) {
		with(dest) {
			writeInt(id)
			writeString(name)
			writeString(description)
			writeString(race.name)
			writeString(archetype)
			writeTypedList(card_images)
		}
	}
	
	companion object CREATOR : Parcelable.Creator<SkillCard> {
		
		override fun createFromParcel(source: Parcel): SkillCard {
			return SkillCard(source)
		}
		
		override fun newArray(size: Int): Array<SkillCard?> {
			return arrayOfNulls(size)
		}
		
		enum class TypeSkill(override val type: String) : Type {
			
			SKILL_CARD("Skill Card");
			
			override fun getEnumName(): String = this.name
			
			override fun toString(): String = type
			
			override val color: Int
				get() = R.color.colorSkillCard
			
			override val icon: Int
				get() = R.drawable.skill_s
			
		}
		
		enum class RaceSkill(override val race: String) : Race {
			
			DR_VELLIAN_C("Dr. Vellian C"),
			CHAZZ_PRINCET("Chazz Princet"),
			MAI("Mai"),
			KEITH("Keith"),
			YAMI_YUGI("Yami Yugi"),
			KAIBA("Kaiba"),
			AXEL_BRODIE("Axel Brodie"),
			BONZ("Bonz"),
			MAKO("Mako"),
			WEEVIL("Weevil"),
			YUBEL("Yubel"),
			JESSE_ANDERSO("Jesse Anderso"),
			ALEXIS_RHODES("Alexis Rhodes"),
			ZANE_TRUESDAL("Zane Truesdal"),
			YUGI("Yugi"),
			DAVID("David"),
			REX("Rex"),
			ODION("Odion"),
			BASTION_MISAW("Bastion Misaw"),
			CHRISTINE("Christine"),
			ISHIZU("Ishizu"),
			JOEY("Joey"),
			JADEN_YUKI("Jaden Yuki"),
			YAMI_MARIK("Yami Marik"),
			JOEY_WHEELER("Joey Wheeler"),
			TYRANNO_HASSL("Tyranno Hassl"),
			YAMI_BAKURA("Yami Bakura"),
			PEGASUS("Pegasus"),
			ESPA_ROBA("Espa Roba"),
			SETO_KAIBA("Seto Kaiba"),
			ASTER_PHOENIX("Aster Phoenix"),
			ANDREW("Andrew"),
			ARKANA("Arkana"),
			MAI_VALENTINE("Mai Valentine"),
			TEA_GARDNER("Tea Gardner"),
			ISHIZU_ISHTAR("Ishizu Ishtar"),
			EMMA("Emma"),
			SYRUS_TRUESDA("Syrus Truesda"),
			LUMIS_UMBRA("Lumis Umbra"),
			PARADOX_BROTH("Paradox Broth"),
			CHUMLEY_HUFFI("Chumley Huffi"),
			LUMIS_AND_UMB("Lumis and Umb"),
			EMPTY("");
			
			override fun getEnumName(): String = this.name
			
			override fun toString(): String = race
			
			override val icon: Int
				get() = R.drawable.race_skill_s
		}
	}
	
}