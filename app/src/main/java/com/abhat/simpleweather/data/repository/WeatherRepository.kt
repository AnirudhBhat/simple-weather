package com.abhat.simpleweather.data.repository

import com.abhat.simpleweather.data.network.WeatherApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class WeatherRepository(private val weatherApi: WeatherApi): Repository {
    override fun getWeatherFor(lat: Float, lon: Float): Flow<WeatherRepoState> {
        return flow {
            try {
                val result = weatherApi.getWeatherFor(lat, lon).await()
                emit(WeatherRepoState.Success(weatherResponse = result))
            } catch (e: Exception) {
                emit(WeatherRepoState.Error(error = e.cause))
            }
        }
    }

}
