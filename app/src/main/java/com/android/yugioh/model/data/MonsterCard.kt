package com.android.yugioh.model.data

import com.android.yugioh.R

data class MonsterCard(
	override val id: Int,
	override val name: String,
	override val type: MonsterType,
	override val description: String,
	override val race: RaceMonsterCard,
	override val archetype: String?,
	override val cardImages: List<Image>,
	override val format: Format?,
	val attack: Short,
	val defense: Short?, //link monsters defense is null
	val level: Byte, /*Represents a level monster or link rating value in link monsters*/
	val attribute: AttributeMonster,
	val scaleOfPendulum: Byte?
) : Card(id, name, type, description, race, archetype, cardImages, format) {
	
	companion object {
		
		enum class MonsterType(
			override val type: String,
			override val color: Int,
			override val icon: Int
		) : Type {
			/*NORMAL*/
			NORMAL_MONSTER(
				"Normal Monster",
				R.color.color_normal_monster,
				R.drawable.normal_monster_s
			),
			NORMAL_TUNER_MONSTER(
				"Normal Tuner Monster",
				R.color.color_normal_monster,
				R.drawable.normal_tuner_monster_s
			),
			
			/*EFFECT*/
			EFFECT_MONSTER(
				"Effect Monster",
				R.color.colorEffectMonster,
				R.drawable.effect_monster_s
			),
			TUNER_MONSTER("Tuner Monster", R.color.colorEffectMonster, R.drawable.tuner_monster_s),
			FLIP_EFFECT_MONSTER(
				"Flip Effect Monster",
				R.color.colorEffectMonster,
				R.drawable.flip_effect_monster_s
			),
			FLIP_TUNER_EFFECT_MONSTER(
				"Flip Tuner Effect Monster",
				R.color.colorEffectMonster,
				R.drawable.flip_tuner_effect_monster_s
			),
			
			//*FUSION*//*
			FUSION_MONSTER(
				"Fusion Monster",
				R.color.colorFusionMonster,
				R.drawable.fusion_monster_s
			),
			PENDULUM_EFFECT_FUSION_MONSTER(
				"Pendulum Effect Fusion Monster",
				R.color.colorFusionMonster,
				R.drawable.pendulum_fusion_monster_s
			),
			
			//*XYZ*//*
			XYZ_MONSTER("XYZ Monster", R.color.colorXYZMonster, R.drawable.xyz_monster_s),
			XYZ_PENDULUM_EFFECT_MONSTER(
				"XYZ Pendulum Effect Monster",
				R.color.colorXYZMonster,
				R.drawable.xyz_pendulum_monster_s
			),
			
			//*SYNCHRO*//*
			SYNCHRO_MONSTER(
				"Synchro Monster",
				R.color.colorSynchronyMonster,
				R.drawable.synchro_monster_s
			),
			SYNCHRO_TUNER_MONSTER(
				"Synchro Tuner Monster",
				R.color.colorSynchronyMonster,
				R.drawable.synchro_tuner_monster_s
			),
			SYNCHRO_PENDULUM_EFFECT_MONSTER(
				"Synchro Pendulum Effect Monster",
				R.color.colorSynchronyMonster,
				R.drawable.synchro_pendulum_monster_s
			),
			
			/*LINK*/
			LINK_MONSTER("Link Monster", R.color.colorLinkMonster, R.drawable.link_monster_s),
			
			//*RITUAL*//*
			RITUAL_MONSTER(
				"Ritual Monster",
				R.color.colorRitualMonster,
				R.drawable.ritual_monster_s
			),
			RITUAL_EFFECT_MONSTER(
				"Ritual Effect Monster",
				R.color.colorRitualMonster,
				R.drawable.ritual_monster_s
			),
			PENDULUM_EFFECT_RITUAL_MONSTER(
				"Pendulum Effect Ritual Monster",
				R.color.colorRitualMonster,
				R.drawable.pendulum_ritual_monster_s
			),
			
			/*PENDULUM*/
			PENDULUM_NORMAL_MONSTER(
				"Pendulum Normal Monster",
				R.color.color_normal_monster,
				R.drawable.pendulum_normal_monster
			),
			PENDULUM_EFFECT_MONSTER(
				"Pendulum Effect Monster",
				R.color.colorEffectMonster,
				R.drawable.pendulum_effect_monster_s
			),
			PENDULUM_TUNER_EFFECT_MONSTER(
				"Pendulum Tuner Effect Monster",
				R.color.colorEffectMonster,
				R.drawable.pendulum_tuner_effect_monster
			),
			PENDULUM_FLIP_EFFECT_MONSTER(
				"Pendulum Flip Effect Monster",
				R.color.colorEffectMonster,
				R.drawable.flip_pendulum_effect_monster_s
			),
			
			//*TOKEN*//*
			TOKEN("Token", R.color.colorTokenCard, R.drawable.token_s),
			
			//*Other types*//*
			TOON_MONSTER("Toon Monster", R.color.colorEffectMonster, R.drawable.effect_monster_s),
			SPIRIT_MONSTER(
				"Spirit Monster",
				R.color.colorEffectMonster,
				R.drawable.effect_monster_s
			),
			UNION_EFFECT_MONSTER(
				"Union Effect Monster",
				R.color.colorEffectMonster,
				R.drawable.effect_monster_s
			),
			GEMINI_MONSTER(
				"Gemini Monster",
				R.color.colorEffectMonster,
				R.drawable.effect_monster_s
			);
			
			override val enum: kotlin.Enum<*>
				get() = this
			
			override fun toString(): String = type
		}
		
		enum class RaceMonsterCard(override val race: String, override val icon: Int) : Race {
			
			SPELLCASTER("Spellcaster", R.drawable.spellcaster_s),
			DRAGON("Dragon", R.drawable.dragon_s),
			ZOMBIE("Zombie", R.drawable.zoombie_s),
			WARRIOR("Warrior", R.drawable.warrior_s),
			BEST_WARRIOR("Beast-Warrior", R.drawable.beast_warrior_s),
			BEAST("Beast", R.drawable.beast_s),
			WINGED_BEAST("Winged Beast", R.drawable.winged_beast_s),
			MACHINE("Machine", R.drawable.machine_s),
			FIEND("Fiend", R.drawable.fiend_s),
			FAIRY("Fairy", R.drawable.fairi_s),
			INSECT("Insect", R.drawable.insect_s),
			DINOSAUR("Dinosaur", R.drawable.dinosaur_s),
			REPTILE("Reptile", R.drawable.reptile_s),
			FISHER("Fish", R.drawable.fish_s),
			SEA_SERPENT("Sea Serpent", R.drawable.sea_serpent_s),
			AQUA("Aqua", R.drawable.aqua_s),
			PYRO("Pyro", R.drawable.pyro_s),
			THUNDER("Thunder", R.drawable.thunder_s),
			ROCK("Rock", R.drawable.rock_s),
			PLANT("Plant", R.drawable.plant_s),
			PSYCHIC("Psychic", R.drawable.psychic_s),
			WYRM("Wyrm", R.drawable.wyrm_s),
			CYBERSE("Cyberse", R.drawable.cyberse_s),
			DIVINE_BEAST("Divine-Beast", R.drawable.divine_beast_s),
			
			//*RUSH DUEL*//*
			CYBORG("Cyborg", R.drawable.cyborg_s),
			MAGICAL_KNIGHT("Magical Knigh", R.drawable.magical_knigh_s),
			HIGH_DRAGON("High Dragon", R.drawable.high_dragon_s),
			CELESTIAL_WAR("Celestial War", R.drawable.celestial_war_s),
			OMEGA_PSYCHIC("Omega Psychic", R.drawable.omega_psychic_s),
			
			//*OTHER*//*
			CREATOR_GOD("Creator-God", R.drawable.divine_beast_s);
			
			override val enum: kotlin.Enum<*>
				get() = this
			
			override fun toString(): String = race
			
		}
		
		enum class AttributeMonster(override val icon: Int) : Enum {
			
			LIGHT(R.drawable.light_s),
			DARK(R.drawable.dark_s),
			WATER(R.drawable.water_s),
			FIRE(R.drawable.fire_s),
			EARTH(R.drawable.earth_s),
			WIND(R.drawable.wind_s),
			DIVINE(R.drawable.divine_s);
			
			override val enum: kotlin.Enum<*>
				get() = this
			
		}
	}
}