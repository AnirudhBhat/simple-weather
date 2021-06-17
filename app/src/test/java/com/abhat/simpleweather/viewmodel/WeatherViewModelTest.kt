package com.abhat.simpleweather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.abhat.simpleweather.data.model.*
import com.abhat.simpleweather.data.repository.WeatherRepoState
import com.abhat.simpleweather.data.repository.WeatherRepository
import com.abhat.simpleweather.ui.CoroutineContextProvider
import com.abhat.simpleweather.ui.WeatherViewModel
import com.abhat.simpleweather.weatherdatasource.WeatherResponseData
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
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                currentDayWeather = WeatherResponseData.getWeatherData(
                                    temp = 21.1F,
                                    feelsLike = 20.1F
                                ),
                                dailyWeatherData = listOf(WeatherResponseData.getDailyWeatherData())
                            )
                        )
                    )
                )
            val weatherViewModel = WeatherViewModel(weatherRepository, TestContextProvider())
            weatherViewModel.viewStateData.observeForever(weatherObserver)

            // When
            weatherViewModel.getWeatherFor(10F, 10F)

            // Then
            val expectedState = WeatherResponseData.getWeatherState(
                temp = 21.1F,
                feelsLike = 20.1F
            )
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper state with min and max for success response`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(weatherResponse = WeatherResponseData.getWeatherResponse(
                            currentDayWeather = WeatherResponseData.getWeatherData(
                                temp = 21.1F,
                                feelsLike = 20.1F
                            ),
                            dailyWeatherData = listOf(WeatherResponseData.getDailyWeatherData(
                                temp = WeatherResponseData.getTemp(
                                    min = 19F,
                                    max = 21F
                                )
                            ))
                        ))
                    )
                )
            val weatherViewModel = WeatherViewModel(weatherRepository, TestContextProvider())
            weatherViewModel.viewStateData.observeForever(weatherObserver)

            // When
            weatherViewModel.getWeatherFor(10F, 10F)

            // Then
            val expectedState = WeatherResponseData.getWeatherState(
                temp = 21.1F,
                feelsLike = 20.1F,
                min = 19F,
                max = 21F,
                dailyWeatherData = listOf(WeatherResponseData.getDailyWeatherData(
                    temp = WeatherResponseData.getTemp(
                        min = 19F,
                        max = 21F
                    )
                ))
            )
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper state along with hourly details for success response`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse()
                        )
                    )
                )
            val weatherViewModel = WeatherViewModel(weatherRepository, TestContextProvider())
            weatherViewModel.viewStateData.observeForever(weatherObserver)

            // When
            weatherViewModel.getWeatherFor(10F, 10F)

            // Then
            val expectedState = WeatherResponseData.getWeatherState()
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
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Error(error = error)
                    )
                )
            val weatherViewModel = WeatherViewModel(weatherRepository, TestContextProvider())
            weatherViewModel.viewStateData.observeForever(weatherObserver)

            // When
            weatherViewModel.getWeatherFor(10F, 10F)

            // Then
            val expectedState = WeatherViewModel.ViewState.Error(
                throwable = error
            )
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }


    class TestContextProvider : CoroutineContextProvider() {
        override val Main: CoroutineDispatcher = Dispatchers.Unconfined
        override val IO: CoroutineDispatcher = Dispatchers.Unconfined
    }
}