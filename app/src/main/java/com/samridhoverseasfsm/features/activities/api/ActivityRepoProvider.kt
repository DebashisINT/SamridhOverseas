package com.samridhoverseasfsm.features.activities.api

import com.samridhoverseasfsm.features.member.api.TeamApi
import com.samridhoverseasfsm.features.member.api.TeamRepo

object ActivityRepoProvider {
    fun activityRepoProvider(): ActivityRepo {
        return ActivityRepo(ActivityApi.create())
    }

    fun activityImageRepoProvider(): ActivityRepo {
        return ActivityRepo(ActivityApi.createImage())
    }
}