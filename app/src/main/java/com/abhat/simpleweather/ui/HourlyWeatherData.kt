package com.abhat.simpleweather.ui

import androidx.annotation.DrawableRes
import com.abhat.simpleweather.data.model.WeatherMeta

data class HourlyWeatherData(
    val date: Long,
    val sunriseTime: Long,
    val sunsetTime: Long,
    val temp: Float,
    val feelsLike: Float,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Float,
    val uvi: Float,
    val clouds: Int,
    val visibility: Int,
    val windSpeed: Float,
    val windDegree: Int,
    val weatherMeta: List<WeatherMeta>,
    @DrawableRes val icon: Int
)
