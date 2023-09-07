package com.samridhoverseasfsm.features.damageProduct.api

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.samridhoverseasfsm.app.FileUtils
import com.samridhoverseasfsm.base.BaseResponse
import com.samridhoverseasfsm.features.NewQuotation.model.*
import com.samridhoverseasfsm.features.addshop.model.AddShopRequestData
import com.samridhoverseasfsm.features.addshop.model.AddShopResponse
import com.samridhoverseasfsm.features.damageProduct.model.DamageProductResponseModel
import com.samridhoverseasfsm.features.damageProduct.model.delBreakageReq
import com.samridhoverseasfsm.features.damageProduct.model.viewAllBreakageReq
import com.samridhoverseasfsm.features.login.model.userconfig.UserConfigResponseModel
import com.samridhoverseasfsm.features.myjobs.model.WIPImageSubmit
import com.samridhoverseasfsm.features.photoReg.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class GetDamageProductListRegRepository(val apiService : GetDamageProductListApi) {

    fun viewBreakage(req: viewAllBreakageReq): Observable<DamageProductResponseModel> {
        return apiService.viewBreakage(req)
    }

    fun delBreakage(req: delBreakageReq): Observable<BaseResponse>{
        return apiService.BreakageDel(req.user_id!!,req.breakage_number!!,req.session_token!!)
    }

}