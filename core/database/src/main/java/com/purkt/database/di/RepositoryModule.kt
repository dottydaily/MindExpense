package com.purkt.database.di

import com.purkt.database.data.impl.repo.IndividualExpenseRepositoryImpl
import com.purkt.database.domain.repo.IndividualExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideIndividualExpenseRepository(
        individualExpenseRepositoryImpl: IndividualExpenseRepositoryImpl
    ): IndividualExpenseRepository
}
