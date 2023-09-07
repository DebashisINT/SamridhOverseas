package com.samridhoverseasfsm.features.location.shopRevisitStatus

import com.samridhoverseasfsm.features.location.shopdurationapi.ShopDurationApi
import com.samridhoverseasfsm.features.location.shopdurationapi.ShopDurationRepository

object ShopRevisitStatusRepositoryProvider {
    fun provideShopRevisitStatusRepository(): ShopRevisitStatusRepository {
        return ShopRevisitStatusRepository(ShopRevisitStatusApi.create())
    }
}