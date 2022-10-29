package com.purkt.database.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.purkt.database.data.converter.LocalDateTimeTypeConverter
import com.purkt.database.data.dao.IndividualExpenseDao
import com.purkt.database.data.entity.IndividualExpenseEntity

@Database(
    entities = [IndividualExpenseEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalDateTimeTypeConverter::class)
abstract class MindExpenseDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME: String = "mind-expense-database"
    }
    abstract fun individualExpenseDao(): IndividualExpenseDao
}
