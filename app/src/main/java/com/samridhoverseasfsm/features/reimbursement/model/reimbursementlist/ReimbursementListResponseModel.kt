package com.samridhoverseasfsm.features.reimbursement.model.reimbursementlist

import com.samridhoverseasfsm.base.BaseResponse

/**
 * Created by Saikat on 28-01-2019.
 */
class ReimbursementListResponseModel : BaseResponse() {
    var total_claim_amount: String? = null
    var total_approved_amount: String? = null
    var expense_list: ArrayList<ReimbursementListDataModel>? = null
}