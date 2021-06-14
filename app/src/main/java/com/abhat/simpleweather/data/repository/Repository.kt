package com.abhat.simpleweather.data.repository

import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getWeatherFor(lat: Float, lon: Float): Flow<WeatherRepoState>
}
