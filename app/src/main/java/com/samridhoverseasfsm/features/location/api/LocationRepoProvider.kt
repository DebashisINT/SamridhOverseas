package com.samridhoverseasfsm.features.location.api

import com.samridhoverseasfsm.features.location.shopdurationapi.ShopDurationApi
import com.samridhoverseasfsm.features.location.shopdurationapi.ShopDurationRepository


object LocationRepoProvider {
    fun provideLocationRepository(): LocationRepo {
        return LocationRepo(LocationApi.create())
    }
}