package com.abhat.simpleweather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.abhat.simpleweather.R
import com.abhat.simpleweather.data.model.City
import com.abhat.simpleweather.data.repository.CityRepoState
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

class WeatherViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherObserver: Observer<WeatherViewModel.ViewState>
    private lateinit var eventObserver: Observer<WeatherViewModel.ViewEvent>

    @Before
    fun setUp() {
        weatherRepository = mock()
        weatherObserver = mock()
        eventObserver = mock()
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
            val weatherViewModel = WeatherViewModel(dailyWeatherRepositoryWhen("broken clouds"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(dailyWeatherRepositoryWhen("random clouds"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(dailyWeatherRepositoryWhen("thunderstorm"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(dailyWeatherRepositoryWhen("light rain"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(dailyWeatherRepositoryWhen("moderate rain"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(currentWeatherRepositoryWhen("clear"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(currentWeatherRepositoryWhen("few clouds"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(currentWeatherRepositoryWhen("random clouds"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(currentWeatherRepositoryWhen("broken clouds"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(currentWeatherRepositoryWhen("scattered clouds"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(currentWeatherRepositoryWhen("overcast clouds"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(currentWeatherRepositoryWhen("light rain"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(currentWeatherRepositoryWhen("moderate rain"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(currentWeatherRepositoryWhen("thunderstorm"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(dailyWeatherRepositoryWhenTempIs(min = 19F, max = 21F), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(weatherRepositoryWhen("thunderstorm"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(weatherRepositoryWhen("broken clouds"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(weatherRepositoryWhen("scattered clouds"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(weatherRepositoryWhen("moderate rain"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(weatherRepositoryWhen("light rain"), TestContextProvider())
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
            val weatherViewModel = WeatherViewModel(weatherRepositoryWhen("sunny"), TestContextProvider())
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
    fun `fetching weather humidity must return proper humidity for success response`() {
        runBlocking {
            // Given
            val weatherViewModel = WeatherViewModel(currentDayWeatherRepositoryForHumidity(humidity = 65), TestContextProvider())
            weatherViewModel.viewStateData.observeForever(weatherObserver)

            // When
            weatherViewModel.getWeatherFor(10F, 10F)

            // Then
            val expectedState = WeatherResponseData.getWeatherState(
                humidity = 65
            )
            Assert.assertEquals(expectedState.humidity, (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).humidity)
        }
    }

    @Test
    fun `fetching weather windspeed must return proper windspeed for success response`() {
        runBlocking {
            // Given
            val weatherViewModel = WeatherViewModel(currentDayWeatherRepositoryForWindSpeed(windSpeed = 9.5F), TestContextProvider())
            weatherViewModel.viewStateData.observeForever(weatherObserver)

            // When
            weatherViewModel.getWeatherFor(10F, 10F)

            // Then
            val expectedState = WeatherResponseData.getWeatherState(
                windSpeed = 9.5F
            )
            Assert.assertEquals(expectedState.windSpeed, (weatherViewModel.viewStateData.value as WeatherViewModel.ViewState.Weather).windSpeed)
        }
    }

    @Test
    fun `fetching weather details must return proper state for error response`() {
        runBlocking {
            // Given
            val error = RuntimeException()
            val weatherViewModel = WeatherViewModel(failingWeatherRepository(error), TestContextProvider())
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

    @Test
    fun `given city name, when getLatLon is called, then TriggerWeatherForCity is called on success`() {
        val weatherViewModel = WeatherViewModel(weatherRepository, TestContextProvider())
        weatherViewModel.viewStateData.observeForever(weatherObserver)
        weatherViewModel.event.observeForever(eventObserver)
        whenever(weatherRepository.getLatLongFor("Bengaluru")).thenReturn(
            flowOf(
                CityRepoState.Success(
                    City(
                    coordination = City.Coordination(
                        lat = 0f,
                        lon = 0f
                    )
                ))
            )
        )

        weatherViewModel.getLatLongFor("Bengaluru")

        Assert.assertEquals(weatherViewModel.event.value, WeatherViewModel.ViewEvent.TriggerWeatherForCity(0f, 0f))
    }

    @Test
    fun `given city name, when getLatLon is called, then proper state is called on error`() {
        runBlocking {
            // Given
            val error = CityRepoState.Error(RuntimeException())
            val weatherViewModel = WeatherViewModel(weatherRepository, TestContextProvider())
            whenever(weatherRepository.getLatLongFor("Bengaluru")).thenReturn(
                flowOf(
                    error
                )
            )
            weatherViewModel.viewStateData.observeForever(weatherObserver)

            // When
            weatherViewModel.getLatLongFor("Bengaluru")

            // Then
            val expectedState = WeatherViewModel.ViewState.Error(throwable = error.error)

            Assert.assertEquals(expectedState, weatherViewModel.viewStateData.value)
        }
    }



    private fun failingWeatherRepository(error: Throwable): WeatherRepository {
        whenever(weatherRepository.getWeatherFor(any(), any()))
            .thenReturn(
                flowOf(
                    WeatherRepoState.Error(error = error)
                )
            )
        return weatherRepository
    }

    private fun dailyWeatherRepositoryWhenTempIs(min: Float, max: Float): WeatherRepository {
        whenever(weatherRepository.getWeatherFor(any(), any()))
            .thenReturn(
                flowOf(
                    WeatherResponseData.getSuccessWeatherRepoState(
                        dailyWeatherData = listOf(
                            WeatherResponseData.getCloudsDailyWeatherDataWithTempDetails(
                                min = min,
                                max = max
                            )
                        )
                    )
                )
            )
        return weatherRepository
    }

    private fun weatherRepositoryWhen(weather: String): WeatherRepository {
        whenever(weatherRepository.getWeatherFor(any(), any()))
            .thenReturn(
                flowOf(
                    WeatherResponseData.getSuccessWeatherRepoState(
                        hourlyWeatherData = listOf(WeatherResponseData.getCloudsWeatherData(weather))
                    )
                )
            )
        return weatherRepository
    }

    private fun currentWeatherRepositoryWhen(weather: String): WeatherRepository {
        whenever(weatherRepository.getWeatherFor(any(), any()))
            .thenReturn(
                flowOf(
                    WeatherResponseData.getSuccessWeatherRepoState(
                        currentDayWeather = WeatherResponseData.getCloudsWeatherData(weather)
                    )
                )
            )
        return weatherRepository
    }

    private fun dailyWeatherRepositoryWhen(weather: String): WeatherRepository {
        whenever(weatherRepository.getWeatherFor(any(), any()))
            .thenReturn(
                flowOf(
                    WeatherResponseData.getSuccessWeatherRepoState(
                        dailyWeatherData = listOf(
                            WeatherResponseData.getCloudsDailyWeatherData(weather)
                        )
                    )
                )
            )
        return weatherRepository
    }

    private fun currentDayWeatherRepositoryForWindSpeed(windSpeed: Float): WeatherRepository {
        whenever(weatherRepository.getWeatherFor(any(), any()))
            .thenReturn(
                flowOf(
                    WeatherResponseData.getSuccessWeatherRepoState(
                        currentDayWeather = WeatherResponseData.getCloudsWeatherDataWithTempDetails(windSpeed = windSpeed)
                    )
                )
            )
        return weatherRepository
    }

    private fun currentDayWeatherRepositoryForHumidity(humidity: Int): WeatherRepository {
        whenever(weatherRepository.getWeatherFor(any(), any()))
            .thenReturn(
                flowOf(
                    WeatherResponseData.getSuccessWeatherRepoState(
                        currentDayWeather = WeatherResponseData.getCloudsWeatherDataWithTempDetails(humidity = humidity)
                    )
                )
            )
        return weatherRepository
    }

    class TestContextProvider : CoroutineContextProvider() {
        override val Main: CoroutineDispatcher = Dispatchers.Unconfined
        override val IO: CoroutineDispatcher = Dispatchers.Unconfined
    }
}