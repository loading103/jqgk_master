package com.daqsoft.module_work.di

import com.daqsoft.library_base.net.RetrofitClient
import com.daqsoft.module_work.repository.WorkApiService
import com.daqsoft.module_work.repository.WorkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class WorkModule {

    @Singleton
    @Provides
    fun provideWorkApiService() = RetrofitClient.Builder().build().create(WorkApiService::class.java)

    @Singleton
    @Provides
    fun provideWorkRepository(workApiService: WorkApiService)  = WorkRepository(workApiService)
}