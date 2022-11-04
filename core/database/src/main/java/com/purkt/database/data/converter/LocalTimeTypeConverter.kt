package com.purkt.database.data.converter

import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalTimeTypeConverter {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    @TypeConverter
    fun toLocalTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString, dateTimeFormatter)
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.format(dateTimeFormatter)
    }
}
