package com.samridhoverseasfsm.features.login.model.productlistmodel

import com.samridhoverseasfsm.app.domain.ModelEntity
import com.samridhoverseasfsm.app.domain.ProductListEntity
import com.samridhoverseasfsm.base.BaseResponse

class ModelListResponse: BaseResponse() {
    var model_list: ArrayList<ModelEntity>? = null
}