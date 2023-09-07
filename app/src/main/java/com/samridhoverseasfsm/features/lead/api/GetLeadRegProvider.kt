package com.samridhoverseasfsm.features.lead.api

import com.samridhoverseasfsm.features.NewQuotation.api.GetQuotListRegRepository
import com.samridhoverseasfsm.features.NewQuotation.api.GetQutoListApi


object GetLeadRegProvider {
    fun provideList(): GetLeadListRegRepository {
        return GetLeadListRegRepository(GetLeadListApi.create())
    }
}