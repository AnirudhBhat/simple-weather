package com.abhat.simpleweather.data.network

import com.abhat.simpleweather.data.model.WeatherResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("onecall")
    fun getWeatherFor(
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("units") units: String = "metric",
        @Query("appid") appId: String = "API_KEY",
    ): Deferred<WeatherResponse>
}
