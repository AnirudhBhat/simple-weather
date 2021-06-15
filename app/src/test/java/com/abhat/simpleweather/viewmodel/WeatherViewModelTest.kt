package com.abhat.simpleweather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.abhat.simpleweather.data.model.WeatherData
import com.abhat.simpleweather.data.model.WeatherMeta
import com.abhat.simpleweather.data.model.WeatherResponse
import com.abhat.simpleweather.data.repository.WeatherRepoState
import com.abhat.simpleweather.data.repository.WeatherRepository
import com.abhat.simpleweather.ui.CoroutineContextProvider
import com.abhat.simpleweather.ui.WeatherViewModel
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherObserver: Observer<WeatherViewModel.ViewState>

    @Before
    fun setUp() {
        weatherRepository = mock()
        weatherObserver = mock()
    }

    @Test
    fun `fetching weather details must return proper state for success response`() {
        runBlocking {
            // Given
            val expectedState = WeatherViewModel.ViewState.Weather(
                temp = 21.1F,
                feelsLike = 20.1F,
                humidity = 0,
                windSpeed = 0F,
                description = "",
                dailyWeatherData = listOf()
            )
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(weatherResponse = getWeatherResponse())
                    ))
            val weatherViewModel = WeatherViewModel(weatherRepository, TestContextProvider())
            weatherViewModel.viewStateData.observeForever(weatherObserver)

            // When
            weatherViewModel.getWeatherFor(10F, 10F)

            // Then
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper state for error response`() {
        runBlocking {
            // Given
            val error = RuntimeException()
            val expectedState = WeatherViewModel.ViewState.Error(
                throwable = error
            )
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Error(error = error)
                    ))
            val weatherViewModel = WeatherViewModel(weatherRepository, TestContextProvider())
            weatherViewModel.viewStateData.observeForever(weatherObserver)

            // When
            weatherViewModel.getWeatherFor(10F, 10F)

            // Then
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }




    class TestContextProvider : CoroutineContextProvider() {
        override val Main: CoroutineDispatcher = Dispatchers.Unconfined
        override val IO: CoroutineDispatcher = Dispatchers.Unconfined
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
                weatherMeta = WeatherMeta(
                    id = 0,
                    weatherTitle = "",
                    weatherDescription = ""
                )
            ),
            dailyWeatherData = listOf(

            )
        )
    }
}