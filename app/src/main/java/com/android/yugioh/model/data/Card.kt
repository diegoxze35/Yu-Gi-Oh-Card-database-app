package com.android.yugioh.model.data

import androidx.annotation.DrawableRes
import com.android.yugioh.R

sealed class Card(
	open val id: Int,
	open val name: String,
	open val type: Type,
	open val description: String,
	open val race: Race,
	open val archetype: String?,
	open val cardImages: List<Image>,
	open val format: Format?
) {
	
	data class Image(
		val imageUrl: String, val imageUrlSmall: String
	)
	
	sealed interface FormatCard {
		val format: String
		var banListState: BanListState?
		
		object TCG : FormatCard {
			override val format = "TCG"
			override var banListState: BanListState? = null
		}
		
		object OCG : FormatCard {
			override val format = "OCG"
			override var banListState: BanListState? = null
		}
		
		object RushDuel : FormatCard {
			override val format = "Rush Duel"
			override var banListState: BanListState? = null
		}
		
	}
	
	data class Format(
		val tcg: FormatCard.TCG?,
		val ocg: FormatCard.OCG?,
		val rushDuel: FormatCard.RushDuel?
	)
	
	enum class BanListState(private val status: String, @DrawableRes override val icon: Int) :
		DomainEnum {
		
		BANNED("Banned", R.drawable.banlist_banned_s),
		LIMITED("Limited", R.drawable.banlist_limited_s),
		SEMI_LIMITED("Semi-Limited", R.drawable.banlist_semilimited_s),
		UNLIMITED("Unlimited", R.drawable.banlist_unlimited_s);
		
		override val enum: Enum<*>
			get() = this
		
		override fun toString(): String = status
	}
	
}