package com.abhat.simpleweather.data.repository

sealed class WeatherRepoState<out T> {
    data class Success<T>(val weatherResponse: T): WeatherRepoState<T>()
    class Error(val error: Throwable?): WeatherRepoState<Nothing>()
}
