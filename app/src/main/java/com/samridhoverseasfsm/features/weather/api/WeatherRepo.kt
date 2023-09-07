package com.samridhoverseasfsm.features.weather.api

import com.samridhoverseasfsm.base.BaseResponse
import com.samridhoverseasfsm.features.task.api.TaskApi
import com.samridhoverseasfsm.features.task.model.AddTaskInputModel
import com.samridhoverseasfsm.features.weather.model.ForeCastAPIResponse
import com.samridhoverseasfsm.features.weather.model.WeatherAPIResponse
import io.reactivex.Observable

class WeatherRepo(val apiService: WeatherApi) {
    fun getCurrentWeather(zipCode: String): Observable<WeatherAPIResponse> {
        return apiService.getTodayWeather(zipCode)
    }

    fun getWeatherForecast(zipCode: String): Observable<ForeCastAPIResponse> {
        return apiService.getForecast(zipCode)
    }
}