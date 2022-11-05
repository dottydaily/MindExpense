package com.purkt.database.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.purkt.database.data.converter.LocalDateTimeTypeConverter
import com.purkt.database.data.converter.LocalTimeTypeConverter
import com.purkt.database.data.dao.IndividualExpenseDao
import com.purkt.database.data.dao.RecurringExpenseDao
import com.purkt.database.data.entity.IndividualExpenseEntity
import com.purkt.database.data.entity.RecurringExpenseEntity
import com.purkt.database.data.migrationspec.MindExpenseAutoMigrationSpec

@Database(
    entities = [IndividualExpenseEntity::class, RecurringExpenseEntity::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = MindExpenseAutoMigrationSpec.AutoMigrationFromVersion1To2::class
        ),
        AutoMigration(from = 2, to = 3)
    ]
)
@TypeConverters(LocalDateTimeTypeConverter::class, LocalTimeTypeConverter::class)
abstract class MindExpenseDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME: String = "mind-expense-database"
    }
    abstract fun individualExpenseDao(): IndividualExpenseDao
    abstract fun recurringExpenseDao(): RecurringExpenseDao
}
