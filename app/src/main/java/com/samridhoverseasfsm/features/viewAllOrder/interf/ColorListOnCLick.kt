package com.samridhoverseasfsm.features.viewAllOrder.interf

import com.samridhoverseasfsm.app.domain.NewOrderGenderEntity
import com.samridhoverseasfsm.features.viewAllOrder.model.ProductOrder

interface ColorListOnCLick {
    fun colorListOnCLick(size_qty_list: ArrayList<ProductOrder>, adpPosition:Int)
}