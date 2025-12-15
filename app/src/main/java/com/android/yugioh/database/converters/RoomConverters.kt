package com.android.yugioh.database.converters

import androidx.room.TypeConverter
import com.android.yugioh.domain.data.Card
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class RoomConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromImageList(value: String?): List<Card.Image>? {
        if (value == null) return null
        val type = object : TypeToken<List<Card.Image>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun imageListToString(list: List<Card.Image>?): String? {
        return gson.toJson(list)
    }
}
