package com.samridhoverseasfsm.features.viewAllOrder.interf

import com.samridhoverseasfsm.app.domain.NewOrderProductEntity
import com.samridhoverseasfsm.app.domain.NewOrderSizeEntity

interface SizeListNewOrderOnClick {
    fun sizeListOnClick(size: NewOrderSizeEntity)
}