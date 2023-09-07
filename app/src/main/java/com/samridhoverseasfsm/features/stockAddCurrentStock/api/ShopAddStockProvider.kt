package com.samridhoverseasfsm.features.stockAddCurrentStock.api

import com.samridhoverseasfsm.features.location.shopRevisitStatus.ShopRevisitStatusApi
import com.samridhoverseasfsm.features.location.shopRevisitStatus.ShopRevisitStatusRepository

object ShopAddStockProvider {
    fun provideShopAddStockRepository(): ShopAddStockRepository {
        return ShopAddStockRepository(ShopAddStockApi.create())
    }
}