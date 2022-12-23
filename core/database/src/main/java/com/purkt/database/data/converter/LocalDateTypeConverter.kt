package com.purkt.database.data.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateConverter {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, dateFormatter)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }
}
