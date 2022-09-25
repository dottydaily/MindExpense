package com.purkt.database.di

import android.content.Context
import androidx.room.Room
import com.purkt.database.data.MindExpenseDatabase
import com.purkt.database.data.dao.ExpenseDao
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
    fun provideExpenseDao(
        mindExpenseDatabase: MindExpenseDatabase
    ): ExpenseDao {
        return mindExpenseDatabase.expenseDao()
    }
}