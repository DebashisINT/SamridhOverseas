package com.samridhoverseasfsm.features.orderhistory.model

import com.samridhoverseasfsm.base.BaseResponse

/**
 * Created by Pratishruti on 30-11-2017.
 */
class FetchLocationResponse:BaseResponse(){
    var location_details:List<LocationData>?=null
    var visit_distance = ""
}