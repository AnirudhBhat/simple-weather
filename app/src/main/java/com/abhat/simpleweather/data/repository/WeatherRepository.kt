package com.abhat.simpleweather.data.repository

import com.abhat.simpleweather.data.model.WeatherResponse
import com.abhat.simpleweather.data.network.WeatherApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class WeatherRepository(private val weatherApi: WeatherApi): Repository<WeatherResponse> {
    override fun getWeatherFor(lat: Float, lon: Float): Flow<WeatherRepoState<WeatherResponse>> {
        return flow {
            try {
                val result = weatherApi.getWeatherFor(lat, lon).await()
                emit(WeatherRepoState.Success(weatherResponse = result))
            } catch (e: Exception) {
                emit(WeatherRepoState.Error(error = e.cause))
            }
        }
    }

    override fun getLatLongFor(city: String): Flow<CityRepoState> {
        return flow {
            try {
                val result = weatherApi.getLatLongFor(city).await()
                emit(CityRepoState.Success(response = result))
            } catch (e: Exception) {
                emit(CityRepoState.Error(error = e.cause))
            }
        }
    }

}
