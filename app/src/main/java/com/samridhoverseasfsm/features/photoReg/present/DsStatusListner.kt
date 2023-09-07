package com.samridhoverseasfsm.features.photoReg.present

import com.samridhoverseasfsm.app.domain.ProspectEntity
import com.samridhoverseasfsm.features.photoReg.model.UserListResponseModel

interface DsStatusListner {
    fun getDSInfoOnLick(obj: ProspectEntity)
}