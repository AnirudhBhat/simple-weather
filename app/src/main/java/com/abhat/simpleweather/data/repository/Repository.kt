package com.abhat.simpleweather.data.repository

import kotlinx.coroutines.flow.Flow

interface Repository<T> {
    fun getWeatherFor(lat: Float, lon: Float): Flow<WeatherRepoState<T>>
}
