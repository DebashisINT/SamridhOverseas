package com.samridhoverseasfsm.features.stockCompetetorStock.api

import com.samridhoverseasfsm.base.BaseResponse
import com.samridhoverseasfsm.features.orderList.model.NewOrderListResponseModel
import com.samridhoverseasfsm.features.stockCompetetorStock.ShopAddCompetetorStockRequest
import com.samridhoverseasfsm.features.stockCompetetorStock.model.CompetetorStockGetData
import io.reactivex.Observable

class AddCompStockRepository(val apiService:AddCompStockApi){

    fun addCompStock(shopAddCompetetorStockRequest: ShopAddCompetetorStockRequest): Observable<BaseResponse> {
        return apiService.submShopCompStock(shopAddCompetetorStockRequest)
    }

    fun getCompStockList(sessiontoken: String, user_id: String, date: String): Observable<CompetetorStockGetData> {
        return apiService.getCompStockList(sessiontoken, user_id, date)
    }
}