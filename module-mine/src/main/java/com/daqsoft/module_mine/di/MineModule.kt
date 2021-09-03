package com.daqsoft.module_mine.di

import com.daqsoft.module_mine.repository.MineApiService
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.library_base.net.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class MineModule {
    @Singleton
    @Provides
    fun provideMineApiService(): MineApiService = RetrofitClient.Builder().build().create(MineApiService::class.java)

    @Singleton
    @Provides
    fun provideMineRepository(mineApiService: MineApiService) = MineRepository(mineApiService)
}