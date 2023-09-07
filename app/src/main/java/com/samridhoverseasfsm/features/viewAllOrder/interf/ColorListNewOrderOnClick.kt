package com.samridhoverseasfsm.features.viewAllOrder.interf

import com.samridhoverseasfsm.app.domain.NewOrderColorEntity
import com.samridhoverseasfsm.app.domain.NewOrderProductEntity

interface ColorListNewOrderOnClick {
    fun productListOnClick(color: NewOrderColorEntity)
}