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
import kotlinx.coroutines.flow.flow
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
                        WeatherResponseData.getSuccessWeatherRepoState(
                            dailyWeatherData = listOf(
                                WeatherResponseData.getCloudsDailyWeatherData("clear"),
                                WeatherResponseData.getCloudsDailyWeatherData("moderate rain")
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
                today = WeatherResponseData.getTodayWeather(
                    description = "clear",
                    icon = R.drawable.ic_sunny
                )
            )
            Assert.assertEquals(
                expectedState.today.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.description
            )
            Assert.assertEquals(
                expectedState.today.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper today weather icon when weather is scattered clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            dailyWeatherData = listOf(
                                WeatherResponseData.getCloudsDailyWeatherData("scattered clouds"),
                                WeatherResponseData.getCloudsDailyWeatherData("moderate rain")
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
                today = WeatherResponseData.getTodayWeather(
                    description = "scattered clouds",
                    icon = R.drawable.ic_cloudy
                )
            )
            Assert.assertEquals(
                expectedState.today.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.description
            )
            Assert.assertEquals(
                expectedState.today.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper today weather icon when weather is broken clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            dailyWeatherData = listOf(
                                WeatherResponseData.getCloudsDailyWeatherData("broken clouds")
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
                today = WeatherResponseData.getTodayWeather(
                    description = "broken clouds",
                    icon = R.drawable.ic_partly_cloudy
                )
            )
            Assert.assertEquals(
                expectedState.today.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.description
            )
            Assert.assertEquals(
                expectedState.today.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper today weather icon when weather is random clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            dailyWeatherData = listOf(
                                WeatherResponseData.getCloudsDailyWeatherData("random clouds")
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
                today = WeatherResponseData.getTodayWeather(
                    description = "random clouds",
                    icon = R.drawable.ic_partly_cloudy
                )
            )
            Assert.assertEquals(
                expectedState.today.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.description
            )
            Assert.assertEquals(
                expectedState.today.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper today weather icon when weather is thunderstorm`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            dailyWeatherData = listOf(
                                WeatherResponseData.getCloudsDailyWeatherData("thunderstorm")
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
                today = WeatherResponseData.getTodayWeather(
                    description = "thunderstorm",
                    icon = R.drawable.ic_thunderstorm
                )
            )
            Assert.assertEquals(
                expectedState.today.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.description
            )
            Assert.assertEquals(
                expectedState.today.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper today weather icon when weather is light rain`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            dailyWeatherData = listOf(
                                WeatherResponseData.getCloudsDailyWeatherData("light rain")
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
                today = WeatherResponseData.getTodayWeather(
                    description = "light rain",
                    icon = R.drawable.ic_light_rain
                )
            )
            Assert.assertEquals(
                expectedState.today.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.description
            )
            Assert.assertEquals(
                expectedState.today.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper today weather icon when weather is moderate rain`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            dailyWeatherData = listOf(
                                WeatherResponseData.getCloudsDailyWeatherData("moderate rain")
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
                today = WeatherResponseData.getTodayWeather(
                    description = "moderate rain",
                    icon = R.drawable.ic_moderate_rain
                )
            )
            Assert.assertEquals(expectedState.today.description, (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.description)
            Assert.assertEquals(expectedState.today.icon, (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).today.icon)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is sunny`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            currentDayWeather = WeatherResponseData.getCloudsWeatherData("clear")
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
            Assert.assertEquals(
                expectedState.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).description
            )
            Assert.assertEquals(
                expectedState.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is few clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            currentDayWeather = WeatherResponseData.getCloudsWeatherData("few clouds")
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
            Assert.assertEquals(
                expectedState.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).description
            )
            Assert.assertEquals(
                expectedState.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather contains word cloud`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            currentDayWeather = WeatherResponseData.getCloudsWeatherData("random clouds")
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
            Assert.assertEquals(
                expectedState.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).description
            )
            Assert.assertEquals(
                expectedState.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is broken clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            currentDayWeather = WeatherResponseData.getCloudsWeatherData("broken clouds")
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
            Assert.assertEquals(expectedState.description, (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).description)
            Assert.assertEquals(expectedState.icon, (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).icon)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is scattered clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            currentDayWeather = WeatherResponseData.getCloudsWeatherData("scattered clouds")
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
            Assert.assertEquals(
                expectedState.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).description
            )
            Assert.assertEquals(
                expectedState.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).icon
            )
//            val inOrder = inOrder(weatherObserver)
//            inOrder.verify(weatherObserver).onChanged(WeatherViewModel.ViewState.Loading(true))
//            inOrder.verify(weatherObserver).onChanged(expectedState)
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is overcast clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            currentDayWeather = WeatherResponseData.getCloudsWeatherData("overcast clouds")
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
            Assert.assertEquals(
                expectedState.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).description
            )
            Assert.assertEquals(
                expectedState.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is light rain`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            currentDayWeather = WeatherResponseData.getCloudsWeatherData("light rain")
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
            Assert.assertEquals(
                expectedState.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).description
            )
            Assert.assertEquals(
                expectedState.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is moderate rain`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            currentDayWeather = WeatherResponseData.getCloudsWeatherData("moderate rain")
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
            Assert.assertEquals(
                expectedState.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).description
            )
            Assert.assertEquals(
                expectedState.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper icon when weather is thunderstorm`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            currentDayWeather = WeatherResponseData.getCloudsWeatherData("thunderstorm")
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
            Assert.assertEquals(
                expectedState.description,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).description
            )
            Assert.assertEquals(
                expectedState.icon,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).icon
            )
        }
    }

    @Test
    fun `fetching weather details must return proper state with min and max for success response`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            dailyWeatherData = listOf(
                                WeatherResponseData.getCloudsDailyWeatherDataWithTempDetails(
                                    min = 19F,
                                    max = 21F
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
                min = 19F,
                max = 21F
            )
            Assert.assertEquals(
                expectedState.min,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).min
            )
            Assert.assertEquals(
                expectedState.max,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).max
            )
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is thunderstorm`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            hourlyWeatherData = listOf(WeatherResponseData.getCloudsWeatherData("thunderstorm"))
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
            Assert.assertEquals(
                expectedState.hourlyWeatherData,
                (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).hourlyWeatherData
            )
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is broken clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            hourlyWeatherData = listOf(WeatherResponseData.getCloudsWeatherData("broken clouds"))
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
            Assert.assertEquals(expectedState.hourlyWeatherData, (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).hourlyWeatherData)
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is scattered clouds`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            hourlyWeatherData = listOf(WeatherResponseData.getCloudsWeatherData("scattered clouds"))
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
            Assert.assertEquals(expectedState.hourlyWeatherData, (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).hourlyWeatherData)
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is moderate rain`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            hourlyWeatherData = listOf(WeatherResponseData.getCloudsWeatherData("moderate rain"))
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
            Assert.assertEquals(expectedState.hourlyWeatherData, (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).hourlyWeatherData)
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is light rain`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            hourlyWeatherData = listOf(WeatherResponseData.getCloudsWeatherData("light rain"))
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
            Assert.assertEquals(expectedState.hourlyWeatherData, (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).hourlyWeatherData)
        }
    }

    @Test
    fun `fetching weather details must return proper hourly details with proper icon when weather is sunny`() {
        runBlocking {
            // Given
            whenever(weatherRepository.getWeatherFor(any(), any()))
                .thenReturn(
                    flowOf(
                        WeatherResponseData.getSuccessWeatherRepoState(
                            hourlyWeatherData = listOf(WeatherResponseData.getCloudsWeatherData("sunny"))
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
                        status = "sunny",
                        icon = R.drawable.ic_sunny
                    )
                )
            )
            Assert.assertEquals(expectedState.hourlyWeatherData, (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).hourlyWeatherData)
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