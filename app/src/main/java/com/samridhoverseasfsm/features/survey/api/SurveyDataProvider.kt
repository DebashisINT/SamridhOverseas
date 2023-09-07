package com.samridhoverseasfsm.features.survey.api

import com.samridhoverseasfsm.features.photoReg.api.GetUserListPhotoRegApi
import com.samridhoverseasfsm.features.photoReg.api.GetUserListPhotoRegRepository

object SurveyDataProvider{

    fun provideSurveyQ(): SurveyDataRepository {
        return SurveyDataRepository(SurveyDataApi.create())
    }

    fun provideSurveyQMultiP(): SurveyDataRepository {
        return SurveyDataRepository(SurveyDataApi.createImage())
    }
}