package com.samridhoverseasfsm.features.document.api

import com.samridhoverseasfsm.features.dymanicSection.api.DynamicApi
import com.samridhoverseasfsm.features.dymanicSection.api.DynamicRepo

object DocumentRepoProvider {
    fun documentRepoProvider(): DocumentRepo {
        return DocumentRepo(DocumentApi.create())
    }

    fun documentRepoProviderMultipart(): DocumentRepo {
        return DocumentRepo(DocumentApi.createImage())
    }
}