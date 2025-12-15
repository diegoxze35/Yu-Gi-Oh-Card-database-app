package com.android.yugioh.domain.data

import com.android.yugioh.R
import kotlin.Enum

data class SkillCard(
	override val id: Int,
	override val name: String,
	override val description: String,
	override val race: RaceSkill,
	override val archetype: String?,
	override val cardImages: List<Image>
) : Card(
	id, name, TypeSkill.SKILL_CARD,
	description,
	race, archetype, cardImages, null
) {
	
	companion object {
		
		enum class TypeSkill(override val typeName: String) : Type {
			
			SKILL_CARD("Skill Card");
			
			override fun toString(): String = typeName
			
			override val color: Int
				get() = R.color.color_skill_card
			
			override val icon: Int
				get() = R.drawable.skill_s
			
			override val enum: Enum<*>
				get() = this
		}
		
		enum class RaceSkill(override val raceName: String) : Race {
			
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
			NIGHTSHROUD("Nightshroud"),
			ABIDOS_THE_TH("Abidos the Th"),
			TITAN("Titan"),
			THELONIOUS_VI("Thelonious Vi"),
			THE_SUPREME_K("The Supreme K"),
			ADRIAN_GECKO("Adrian Gecko"),
			DON_ZALOOG("Don Zaloog"),
			AMNAEL("Amnael"),
			CAMULA("Camula"),
			KAGEMARU("Kagemaru"),
			LUMIS_AND_UMB("Lumis and Umb");

			
			override fun toString(): String = raceName
			
			override val icon: Int
				get() = R.drawable.race_skill_s
			
			override val enum: Enum<*>
				get() = this
		}
	}
	
}