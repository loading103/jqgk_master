package com.daqsoft.module_statistics.di

import com.daqsoft.library_base.net.RetrofitClient
import com.daqsoft.module_statistics.repository.StatisticsApiService
import com.daqsoft.module_statistics.repository.StatisticsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class StatisticsModule {

    @Singleton
    @Provides
    fun provideStatisticsApiService() = RetrofitClient.Builder().build().create(StatisticsApiService::class.java)

    @Singleton
    @Provides
    fun provideStatisticsRepository(statisticsApiService: StatisticsApiService)  =
        StatisticsRepository(statisticsApiService)
}