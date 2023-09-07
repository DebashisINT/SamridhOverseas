package com.samridhoverseasfsm.features.viewAllOrder.interf

import com.samridhoverseasfsm.app.domain.NewOrderGenderEntity
import com.samridhoverseasfsm.features.viewAllOrder.model.ProductOrder
import java.text.FieldPosition

interface NewOrderSizeQtyDelOnClick {
    fun sizeQtySelListOnClick(product_size_qty: ArrayList<ProductOrder>)
    fun sizeQtyListOnClick(product_size_qty: ProductOrder,position: Int)
}