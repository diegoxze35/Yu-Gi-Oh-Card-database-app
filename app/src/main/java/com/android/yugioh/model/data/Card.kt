package com.android.yugioh.model.data
import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
	
	data class Image(
		val imageUrl: String, val imageUrlSmall: String
	) : Parcelable {
		
		constructor(parcel: Parcel) : this(
			parcel.readString()!!, parcel.readString()!!
		)
		
		override fun writeToParcel(dest: Parcel, flags: Int) {
			with(dest) {
				writeString(imageUrl)
				writeString(imageUrlSmall)
			}
		}
		
		override fun describeContents(): Int {
			return 0
		}
		
		companion object CREATOR : Parcelable.Creator<Image> {
			override fun createFromParcel(parcel: Parcel): Image {
				return Image(parcel)
			}
			
			override fun newArray(size: Int): Array<Image?> {
				return arrayOfNulls(size)
			}
		}
		
	}
	
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
	) :
		Parcelable {
		constructor(parcel: Parcel) : this(
			kotlin.run {
				val size = 3
				val array = arrayOfNulls<String?>(size)
				parcel.readStringArray(array)
				val cardFormatArray = arrayOfNulls<CardFormat?>(size)
				array.forEachIndexed { index, s ->
					cardFormatArray[index] = s?.let { CardFormat.valueOf(s) }
				}
				cardFormatArray
			},
			parcel.readString()?.let {
				BanListState.valueOf(it)
			},
			parcel.readString()?.let {
				BanListState.valueOf(it)
			}
		)
		
		override fun writeToParcel(parcel: Parcel, flags: Int) {
			with(parcel) {
				writeStringArray(formats.map {
					it?.name
				}.toTypedArray())
				writeString(banTCG?.enum?.name)
				writeString(banOCG?.enum?.name)
			}
		}
		
		override fun describeContents(): Int {
			return 0
		}
		
		companion object CREATOR : Parcelable.Creator<Format> {
			override fun createFromParcel(parcel: Parcel): Format {
				return Format(parcel)
			}
			
			override fun newArray(size: Int): Array<Format?> {
				return arrayOfNulls(size)
			}
		}
		
	}
	override fun writeToParcel(dest: Parcel, flags: Int) {
		with(dest) {
			writeInt(id)
			writeString(name)
			writeString(type.enum.name)
			writeString(description)
			writeString(race.enum.name)
			writeString(archetype)
			writeTypedList(card_images)
			writeTypedObject(format, flags)
		}
	}
	
	override fun describeContents(): Int {
		return 0
	}
	
}