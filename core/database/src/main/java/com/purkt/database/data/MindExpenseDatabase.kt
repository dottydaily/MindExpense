package com.purkt.database.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.purkt.database.data.converter.LocalDateTimeTypeConverter
import com.purkt.database.data.dao.ExpenseDao

@Database(
    entities = [],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalDateTimeTypeConverter::class)
abstract class MindExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}
