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
	open val banListInfo: BanListInfo?
) : Parcelable {
	
	data class Image(
		val imageUrl: String,
		val imageUrlSmall: String
	) : Parcelable {
		
		constructor(parcel: Parcel) : this(
			parcel.readString()!!,
			parcel.readString()!!
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
	
	data class BanListInfo(val banTCG: BanListState, val banOCG: BanListState) : Parcelable {
		
		companion object CREATOR : Parcelable.Creator<BanListInfo> {
			
			enum class BanListState(private val status: String, override val icon: Int) : Enum {
				
				BANNED("Banned", R.drawable.banlist_banned_s),
				LIMITED("Limited", R.drawable.banlist_limited_s),
				SEMI_LIMITED("Semi-Limited", R.drawable.banlist_semilimited_s),
				UNLIMITED("Unlimited", R.drawable.banlist_unlimited_s);
				
				override fun getEnumName(): String = this.name
				
				override fun toString(): String = status
			}
			
			override fun createFromParcel(source: Parcel): BanListInfo {
				return BanListInfo(source)
			}
			
			override fun newArray(size: Int): Array<BanListInfo?> {
				return arrayOfNulls(size)
			}
		}
		
		constructor(parcel: Parcel) : this(
			BanListState.valueOf(parcel.readString()!!), BanListState.valueOf(parcel.readString()!!)
		)
		
		override fun describeContents(): Int {
			return 0
		}
		
		override fun writeToParcel(dest: Parcel, flags: Int) {
			with(dest) {
				writeString(banTCG.getEnumName())
				writeString(banOCG.getEnumName())
			}
		}
	}
	
	override fun writeToParcel(dest: Parcel, flags: Int) {
		with(dest) {
			writeInt(id)
			writeString(name)
			writeString(type.getEnumName())
			writeString(description)
			writeString(race.getEnumName())
			writeString(archetype)
			writeTypedList(card_images)
			writeTypedObject(banListInfo, flags)
		}
	}
	
	override fun describeContents(): Int {
		return 0
	}
	
}