package com.samridhoverseasfsm.features.weather.api

import com.samridhoverseasfsm.features.task.api.TaskApi
import com.samridhoverseasfsm.features.task.api.TaskRepo

object WeatherRepoProvider {
    fun weatherRepoProvider(): WeatherRepo {
        return WeatherRepo(WeatherApi.create())
    }
}