package com.abhat.simpleweather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.abhat.simpleweather.R
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
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

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
                feelsLike = 20.1F,
            )
//            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper today weather for success response`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                dailyWeatherData = listOf(
                                    WeatherResponseData.getDailyWeatherData(
                                        weatherMeta = listOf(WeatherResponseData.getWeatherMeta(
                                            description = "clear"
                                        ))
                                    ),
                                    WeatherResponseData.getDailyWeatherData(date = 1L)
                                )
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
                dailyWeatherData = listOf(
                    WeatherResponseData.getDailyWeatherData(
                        weatherMeta = listOf(
                            WeatherResponseData.getWeatherMeta(
                                description = "clear"
                            )
                        )
                    ),
                    WeatherResponseData.getDailyWeatherData(date = 1L)
                ),
                today = WeatherResponseData.getTodayWeather(
                    description = "clear",
                    icon = R.drawable.ic_sunny
                )
            )
//            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is sunny`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                currentDayWeather = WeatherResponseData.getWeatherData(
                                    weatherMeta = listOf(
                                        WeatherResponseData.getWeatherMeta(
                                            description = "clear"
                                        )
                                    )
                                )
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
                description = "clear",
                icon = R.drawable.ic_sunny
            )
//            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is few clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                currentDayWeather = WeatherResponseData.getWeatherData(
                                    weatherMeta = listOf(
                                        WeatherResponseData.getWeatherMeta(
                                            description = "few clouds"
                                        )
                                    )
                                )
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
                description = "few clouds",
                icon = R.drawable.ic_partly_cloudy
            )
//            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather contains word cloud`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                currentDayWeather = WeatherResponseData.getWeatherData(
                                    weatherMeta = listOf(
                                        WeatherResponseData.getWeatherMeta(
                                            description = "random clouds"
                                        )
                                    )
                                )
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
                description = "random clouds",
                icon = R.drawable.ic_partly_cloudy
            )
//            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is broken clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                currentDayWeather = WeatherResponseData.getWeatherData(
                                    weatherMeta = listOf(
                                        WeatherResponseData.getWeatherMeta(
                                            description = "broken clouds"
                                        )
                                    )
                                )
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
                description = "broken clouds",
                icon = R.drawable.ic_partly_cloudy
            )
//            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is scattered clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                currentDayWeather = WeatherResponseData.getWeatherData(
                                    weatherMeta = listOf(
                                        WeatherResponseData.getWeatherMeta(
                                            description = "scattered clouds"
                                        )
                                    )
                                )
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
                description = "scattered clouds",
                icon = R.drawable.ic_cloudy
            )
//            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is overcast clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                currentDayWeather = WeatherResponseData.getWeatherData(
                                    weatherMeta = listOf(
                                        WeatherResponseData.getWeatherMeta(
                                            description = "overcast clouds"
                                        )
                                    )
                                )
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
                description = "overcast clouds",
                icon = R.drawable.ic_cloudy
            )
//            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is light rain`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                currentDayWeather = WeatherResponseData.getWeatherData(
                                    weatherMeta = listOf(
                                        WeatherResponseData.getWeatherMeta(
                                            description = "light rain"
                                        )
                                    )
                                )
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
                description = "light rain",
                icon = R.drawable.ic_light_rain
            )
//            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is moderate rain`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                currentDayWeather = WeatherResponseData.getWeatherData(
                                    weatherMeta = listOf(
                                        WeatherResponseData.getWeatherMeta(
                                            description = "moderate rain"
                                        )
                                    )
                                )
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
                description = "moderate rain",
                icon = R.drawable.ic_moderate_rain
            )
//            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is thunderstorm`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                currentDayWeather = WeatherResponseData.getWeatherData(
                                    weatherMeta = listOf(
                                        WeatherResponseData.getWeatherMeta(
                                            description = "thunderstorm"
                                        )
                                    )
                                )
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
                description = "thunderstorm",
                icon = R.drawable.ic_thunderstorm
            )
//            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
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
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                currentDayWeather = WeatherResponseData.getWeatherData(
                                    temp = 21.1F,
                                    feelsLike = 20.1F
                                ),
                                dailyWeatherData = listOf(
                                    WeatherResponseData.getDailyWeatherData(
                                        temp = WeatherResponseData.getTemp(
                                            min = 19F,
                                            max = 21F
                                        )
                                    )
                                )
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
                feelsLike = 20.1F,
                min = 19F,
                max = 21F,
                dailyWeatherData = listOf(
                    WeatherResponseData.getDailyWeatherData(
                        temp = WeatherResponseData.getTemp(
                            min = 19F,
                            max = 21F
                        )
                    )
                )
            )
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is thunderstorm`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                hourlyWeatherData = listOf(
                                    WeatherResponseData.getWeatherData(
                                        weatherMeta = listOf(
                                            WeatherResponseData.getWeatherMeta(
                                                description = "thunderstorm"
                                            )
                                        )
                                    )
                                )
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
                hourlyWeatherData = listOf(
                    WeatherResponseData.getHourlyWeatherData(
                        status = "thunderstorm",
                        icon = R.drawable.ic_thunderstorm
                    )
                )
            )
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is broken clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                hourlyWeatherData = listOf(
                                    WeatherResponseData.getWeatherData(
                                        weatherMeta = listOf(
                                            WeatherResponseData.getWeatherMeta(
                                                description = "broken clouds"
                                            )
                                        )
                                    )
                                )
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
                hourlyWeatherData = listOf(
                    WeatherResponseData.getHourlyWeatherData(
                        status = "broken clouds",
                        icon = R.drawable.ic_partly_cloudy
                    )
                )
            )
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is scattered clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                hourlyWeatherData = listOf(
                                    WeatherResponseData.getWeatherData(
                                        weatherMeta = listOf(
                                            WeatherResponseData.getWeatherMeta(
                                                description = "scattered clouds"
                                            )
                                        )
                                    )
                                )
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
                hourlyWeatherData = listOf(
                    WeatherResponseData.getHourlyWeatherData(
                        status = "scattered clouds",
                        icon = R.drawable.ic_cloudy
                    )
                )
            )
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is moderate rain`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                hourlyWeatherData = listOf(
                                    WeatherResponseData.getWeatherData(
                                        weatherMeta = listOf(
                                            WeatherResponseData.getWeatherMeta(
                                                description = "moderate rain"
                                            )
                                        )
                                    )
                                )
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
                hourlyWeatherData = listOf(
                    WeatherResponseData.getHourlyWeatherData(
                        status = "moderate rain",
                        icon = R.drawable.ic_moderate_rain
                    )
                )
            )
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is light rain`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                hourlyWeatherData = listOf(
                                    WeatherResponseData.getWeatherData(
                                        weatherMeta = listOf(
                                            WeatherResponseData.getWeatherMeta(
                                                description = "light rain"
                                            )
                                        )
                                    )
                                )
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
                hourlyWeatherData = listOf(
                    WeatherResponseData.getHourlyWeatherData(
                        status = "light rain",
                        icon = R.drawable.ic_light_rain
                    )
                )
            )
            val inOrder = inOrder(weatherObserver)
            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is sunny`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherRepoState.Success(
                            weatherResponse = WeatherResponseData.getWeatherResponse(
                                hourlyWeatherData = listOf(
                                    WeatherResponseData.getWeatherData(
                                        weatherMeta = listOf(
                                            WeatherResponseData.getWeatherMeta(
                                                description = "clear"
                                            )
                                        )
                                    )
                                )
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
                hourlyWeatherData = listOf(
                    WeatherResponseData.getHourlyWeatherData(
                        status = "clear",
                        icon = R.drawable.ic_sunny
                    )
                )
            )
            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
//            val inOrder = inOrder(weatherObserver)
//            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
//            inOrder.verify(weatherObserver).onChanged(expectedState)
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