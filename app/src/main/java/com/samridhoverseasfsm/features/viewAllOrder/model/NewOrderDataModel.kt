package com.samridhoverseasfsm.features.viewAllOrder.model

import com.samridhoverseasfsm.app.domain.NewOrderColorEntity
import com.samridhoverseasfsm.app.domain.NewOrderGenderEntity
import com.samridhoverseasfsm.app.domain.NewOrderProductEntity
import com.samridhoverseasfsm.app.domain.NewOrderSizeEntity
import com.samridhoverseasfsm.features.stockCompetetorStock.model.CompetetorStockGetDataDtls

class NewOrderDataModel {
    var status:String ? = null
    var message:String ? = null
    var Gender_list :ArrayList<NewOrderGenderEntity>? = null
    var Product_list :ArrayList<NewOrderProductEntity>? = null
    var Color_list :ArrayList<NewOrderColorEntity>? = null
    var size_list :ArrayList<NewOrderSizeEntity>? = null
}

