package com.samridhoverseasfsm.features.location.shopRevisitStatus

import com.samridhoverseasfsm.base.BaseResponse
import com.samridhoverseasfsm.features.location.model.ShopDurationRequest
import com.samridhoverseasfsm.features.location.model.ShopRevisitStatusRequest
import io.reactivex.Observable

class ShopRevisitStatusRepository(val apiService : ShopRevisitStatusApi) {
    fun shopRevisitStatus(shopRevisitStatus: ShopRevisitStatusRequest?): Observable<BaseResponse> {
        return apiService.submShopRevisitStatus(shopRevisitStatus)
    }
}