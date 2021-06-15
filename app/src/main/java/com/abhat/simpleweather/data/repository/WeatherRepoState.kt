package com.abhat.simpleweather.data.repository

import com.abhat.simpleweather.data.model.WeatherResponse

sealed class WeatherRepoState {
    data class Success(val weatherResponse: WeatherResponse): WeatherRepoState()
    class Error(val error: Throwable?): WeatherRepoState()
}
