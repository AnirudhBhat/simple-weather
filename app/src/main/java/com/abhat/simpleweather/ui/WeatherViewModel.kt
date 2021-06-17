package com.abhat.simpleweather.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhat.simpleweather.data.model.DailyWeatherData
import com.abhat.simpleweather.data.model.WeatherData
import com.abhat.simpleweather.data.model.WeatherResponse
import com.abhat.simpleweather.data.repository.Repository
import com.abhat.simpleweather.data.repository.WeatherRepoState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(
    private val weatherRepository: Repository<WeatherResponse>,
    private val coroutineContextProvider: CoroutineContextProvider
) : ViewModel() {

    private val viewState: MutableLiveData<ViewState> = MutableLiveData()
    val viewStateData: LiveData<ViewState> = viewState

    sealed class ViewState(open var isLoading: Boolean = false) {
        data class Loading(override var isLoading: Boolean = true) : ViewState()

        data class Weather(
            val temp: Float,
            val feelsLike: Float,
            val humidity: Int,
            val windSpeed: Float,
            val description: String,
            val min: Float?,
            val max: Float?,
            val dailyWeatherData: List<DailyWeatherData>,
            val hourlyWeatherData: List<WeatherData>
        ) : ViewState()

        data class Error(val throwable: Throwable?) : ViewState()
    }

    fun getWeatherFor(lat: Float, lon: Float) {
        viewState.value = ViewState.Loading(true)
        viewModelScope.launch(coroutineContextProvider.IO) {
            weatherRepository.getWeatherFor(lat, lon).collect { weatherRepoState ->
                when (weatherRepoState) {
                    is WeatherRepoState.Success -> {
                        val weatherData = weatherMapper(weatherRepoState.weatherResponse)
                        withContext(coroutineContextProvider.Main) {
                            viewState.value = weatherData
                        }
                    }
                    is WeatherRepoState.Error -> {
                        withContext(coroutineContextProvider.Main) {
                            viewState.value = ViewState.Error(weatherRepoState.error)
                        }
                    }
                }
            }
        }
    }

    private fun weatherMapper(weatherResponse: WeatherResponse): ViewState.Weather {
        return ViewState.Weather(
            temp = weatherResponse.currentDayWeather.temp,
            feelsLike = weatherResponse.currentDayWeather.feelsLike,
            humidity = weatherResponse.currentDayWeather.humidity,
            windSpeed = weatherResponse.currentDayWeather.windSpeed,
            description = weatherResponse.currentDayWeather.weatherMeta[0].weatherDescription,
            min = getMinTempForCurrentDay(weatherResponse),
            max = getMaxTempForCurrentDay(weatherResponse),
            dailyWeatherData = weatherResponse.dailyWeatherData,
            hourlyWeatherData = weatherResponse.hourlyWeatherData
        )
    }

    private fun getMaxTempForCurrentDay(weatherResponse: WeatherResponse): Float? {
        if (weatherResponse.dailyWeatherData.isNotEmpty()) {
            return weatherResponse.dailyWeatherData[0].temp.max
        }
        return null
    }

    private fun getMinTempForCurrentDay(weatherResponse: WeatherResponse): Float? {
        if (weatherResponse.dailyWeatherData.isNotEmpty()) {
            return weatherResponse.dailyWeatherData[0].temp.min
        }
        return null
    }
}
