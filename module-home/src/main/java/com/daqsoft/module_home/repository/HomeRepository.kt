package com.daqsoft.module_home.repository

import com.daqsoft.mvvmfoundation.base.BaseModel
import timber.log.Timber
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeApiService:HomeApiService) : BaseModel(),HomeApiService {


    fun test (){
        Timber.e(" MineRepository test")
    }
}