package com.samridhoverseasfsm.features.reimbursement.model

import com.samridhoverseasfsm.base.BaseResponse

/**
 * Created by Saikat on 24-01-2019.
 */
class ReimbursementConfigFetchResponseModel : BaseResponse() {
    var maximum_allowance: String? = null
    var distance: String? = null
    var rate: String? = null
}