package com.samridhoverseasfsm.features.viewAllOrder.orderOptimized

import com.samridhoverseasfsm.app.domain.ProductOnlineRateTempEntity
import com.samridhoverseasfsm.base.BaseResponse
import com.samridhoverseasfsm.features.login.model.productlistmodel.ProductRateDataModel
import java.io.Serializable

class ProductRateOnlineListResponseModel: BaseResponse(), Serializable {
    var product_rate_list: ArrayList<ProductOnlineRateTempEntity>? = null
}