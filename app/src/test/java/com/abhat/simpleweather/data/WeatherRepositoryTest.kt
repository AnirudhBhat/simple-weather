package com.abhat.simpleweather.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.abhat.simpleweather.data.model.City
import com.abhat.simpleweather.data.model.WeatherData
import com.abhat.simpleweather.data.model.WeatherMeta
import com.abhat.simpleweather.data.model.WeatherResponse
import com.abhat.simpleweather.data.network.WeatherApi
import com.abhat.simpleweather.data.repository.CityRepoState
import com.abhat.simpleweather.data.repository.WeatherRepoState
import com.abhat.simpleweather.data.repository.WeatherRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var weatherApi: WeatherApi

    @Before
    fun setUp() {
        weatherApi = mock()
    }

    private fun getWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            lat = 0F,
            lon = 0F,
            timezone = "",
            timezoneOffset = 0,
            currentDayWeather = WeatherData(
                date = 0L,
                sunriseTime = 0L,
                sunsetTime = 0L,
                temp = 21.1F,
                feelsLike = 20.1F,
                pressure = 0,
                humidity = 0,
                dewPoint = 0F,
                uvi = 0F,
                clouds = 0,
                visibility = 0,
                windDegree = 0,
                windSpeed = 0F,
                weatherMeta = listOf(WeatherMeta(
                    id = 0,
                    weatherTitle = "",
                    weatherDescription = ""
                ))
            ),
            dailyWeatherData = emptyList(),
            hourlyWeatherData = emptyList()
        )
    }

    private fun getLatLon(): City {
        return City(
            coordination = City.Coordination(
                lat = 0f,
                lon = 0f
            )
        )
    }

    @Test
    fun `given weather data, weather repository must return proper state on success response`() {
        runBlocking {
            // Given
            val expectedState = WeatherRepoState.Success(weatherResponse = getWeatherResponse())
            whenever(weatherApi.getWeatherFor(any(), any(), any(), any())).thenReturn(
                CompletableDeferred(getWeatherResponse()))
            val weatherRepository = WeatherRepository(weatherApi)

            // When
            val actualState = weatherRepository.getWeatherFor(0F, 0F).toList()

            // Then
            Assert.assertEquals(expectedState, actualState[0])
        }
    }

    @Test
    fun `weather repository must return proper state on error response`() {
        runBlocking {
            // Given
            val exception = RuntimeException()
            whenever(weatherApi.getWeatherFor(any(), any(), any(), any())).thenThrow(exception)
            val weatherRepository = WeatherRepository(weatherApi)

            // When
            val actualState = weatherRepository.getWeatherFor(0F, 0F).toList()

            // Then
            Assert.assertTrue(actualState[0] is WeatherRepoState.Error)
        }
    }

    @Test
    fun `given city, weather repository must return proper lat lon on success response`() {
        runBlocking {
            // Given
            whenever(weatherApi.getLatLongFor(query = eq("Bengaluru"), appId = any())).thenReturn(
                CompletableDeferred(getLatLon()))
            val weatherRepository = WeatherRepository(weatherApi)
            val expectedState = CityRepoState.Success(response = getLatLon())

            // When
            val actualState = weatherRepository.getLatLongFor("Bengaluru").toList()

            // Then
            Assert.assertEquals(expectedState, actualState[0])
        }
    }

    @Test
    fun `given city, weather repository must return proper error response on failure`() {
        runBlocking {
            // Given
            val exception = RuntimeException()
            whenever(weatherApi.getLatLongFor(query = eq("random city"), appId = any())).thenThrow(exception)
            val weatherRepository = WeatherRepository(weatherApi)

            // When
            val actualState = weatherRepository.getLatLongFor("random city").toList()

            // Then
            Assert.assertTrue(actualState[0] is CityRepoState.Error)
        }
    }
}