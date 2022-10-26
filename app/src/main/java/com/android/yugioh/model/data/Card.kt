package com.android.yugioh.model.data
import com.android.yugioh.R

sealed class Card(
	open val id: Int,
	open val name: String,
	open val type: Type,
	open val description: String,
	open val race: Race,
	open val archetype: String?,
	open val card_images: List<Image>,
	open val format: Format?
) {
	
	data class Image(
		val imageUrl: String, val imageUrlSmall: String
	)
	
	enum class CardFormat(private val format: String) {
		
		TCG("TCG"),
		OCG("OCG"),
		RUSH_DUEL("Rush Duel");
		
		override fun toString(): String = this.format
		
	}
	
	enum class BanListState(private val status: String, override val icon: Int) : Enum {
		
		BANNED("Banned", R.drawable.banlist_banned_s),
		LIMITED("Limited", R.drawable.banlist_limited_s),
		SEMI_LIMITED("Semi-Limited", R.drawable.banlist_semilimited_s),
		UNLIMITED("Unlimited", R.drawable.banlist_unlimited_s);
		
		override val enum: kotlin.Enum<*>
			get() = this
		
		override fun toString(): String = status
	}
	
	data class Format(
		val formats: Array<CardFormat?>,
		val banTCG: BanListState?,
		val banOCG: BanListState?
	)
	
}