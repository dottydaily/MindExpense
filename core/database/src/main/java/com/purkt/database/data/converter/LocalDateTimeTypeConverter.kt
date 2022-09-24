package com.purkt.database.data.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeTypeConverter {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeString, dateTimeFormatter)
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(dateTimeFormatter)
    }
}