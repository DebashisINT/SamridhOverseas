package com.samridhoverseasfsm.features.orderList.model

import com.samridhoverseasfsm.base.BaseResponse


class ReturnListResponseModel: BaseResponse() {
    var return_list: ArrayList<ReturnDataModel>? = null
}