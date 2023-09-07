package com.samridhoverseasfsm.features.stockAddCurrentStock.api

import com.samridhoverseasfsm.base.BaseResponse
import com.samridhoverseasfsm.features.location.model.ShopRevisitStatusRequest
import com.samridhoverseasfsm.features.location.shopRevisitStatus.ShopRevisitStatusApi
import com.samridhoverseasfsm.features.stockAddCurrentStock.ShopAddCurrentStockRequest
import com.samridhoverseasfsm.features.stockAddCurrentStock.model.CurrentStockGetData
import com.samridhoverseasfsm.features.stockCompetetorStock.model.CompetetorStockGetData
import io.reactivex.Observable

class ShopAddStockRepository (val apiService : ShopAddStockApi){
    fun shopAddStock(shopAddCurrentStockRequest: ShopAddCurrentStockRequest?): Observable<BaseResponse> {
        return apiService.submShopAddStock(shopAddCurrentStockRequest)
    }

    fun getCurrStockList(sessiontoken: String, user_id: String, date: String): Observable<CurrentStockGetData> {
        return apiService.getCurrStockListApi(sessiontoken, user_id, date)
    }

}