package com.samridhoverseasfsm.features.location.api

import com.samridhoverseasfsm.app.Pref
import com.samridhoverseasfsm.base.BaseResponse
import com.samridhoverseasfsm.features.location.model.AppInfoInputModel
import com.samridhoverseasfsm.features.location.model.AppInfoResponseModel
import com.samridhoverseasfsm.features.location.model.GpsNetInputModel
import com.samridhoverseasfsm.features.location.model.ShopDurationRequest
import com.samridhoverseasfsm.features.location.shopdurationapi.ShopDurationApi
import io.reactivex.Observable

/**
 * Created by Saikat on 17-Aug-20.
 */
class LocationRepo(val apiService: LocationApi) {
    fun appInfo(appInfo: AppInfoInputModel?): Observable<BaseResponse> {
        return apiService.submitAppInfo(appInfo)
    }

    fun getAppInfo(): Observable<AppInfoResponseModel> {
        return apiService.getAppInfo(Pref.session_token!!, Pref.user_id!!)
    }

    fun gpsNetInfo(appInfo: GpsNetInputModel?): Observable<BaseResponse> {
        return apiService.submitGpsNetInfo(appInfo)
    }
}