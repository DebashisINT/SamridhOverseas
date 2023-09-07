package com.samridhoverseasfsm.features.newcollectionreport

import com.samridhoverseasfsm.features.photoReg.model.UserListResponseModel

interface PendingCollListner {
    fun getUserInfoOnLick(obj: PendingCollData)
}