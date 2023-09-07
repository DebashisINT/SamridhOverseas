package com.samridhoverseasfsm.features.viewAllOrder.interf

import com.samridhoverseasfsm.app.domain.NewOrderGenderEntity
import com.samridhoverseasfsm.app.domain.NewOrderProductEntity

interface ProductListNewOrderOnClick {
    fun productListOnClick(product: NewOrderProductEntity)
}