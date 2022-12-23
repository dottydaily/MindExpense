package com.purkt.database.di

import android.content.Context
import androidx.room.Room
import com.purkt.database.data.MindExpenseDatabase
import com.purkt.database.data.dao.DateRangeDao
import com.purkt.database.data.dao.IndividualExpenseDao
import com.purkt.database.data.dao.RecurringExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideMindExpenseDatabase(
        @ApplicationContext context: Context
    ): MindExpenseDatabase {
        return Room.databaseBuilder(
            context,
            MindExpenseDatabase::class.java,
            MindExpenseDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideIndividualExpenseDao(
        mindExpenseDatabase: MindExpenseDatabase
    ): IndividualExpenseDao {
        return mindExpenseDatabase.individualExpenseDao()
    }

    @Provides
    fun provideRecurringExpenseDao(
        mindExpenseDatabase: MindExpenseDatabase
    ): RecurringExpenseDao {
        return mindExpenseDatabase.recurringExpenseDao()
    }

    @Provides
    fun provideDateRangeDao(
        mindExpenseDatabase: MindExpenseDatabase
    ): DateRangeDao {
        return mindExpenseDatabase.dateRangeDao()
    }
}
