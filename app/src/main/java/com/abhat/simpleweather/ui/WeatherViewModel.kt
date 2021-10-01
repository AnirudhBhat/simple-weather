package com.abhat.simpleweather.ui

import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhat.simpleweather.R
import com.abhat.simpleweather.data.model.DailyWeatherData
import com.abhat.simpleweather.data.model.WeatherData
import com.abhat.simpleweather.data.model.WeatherResponse
import com.abhat.simpleweather.data.repository.CityRepoState
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

    private val _event: MutableLiveData<ViewEvent> = MutableLiveData()
    val event: LiveData<ViewEvent> = _event

    sealed class ViewEvent {
        data class TriggerWeatherForCity(val lat: Float, val lon: Float): ViewEvent()
    }

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
            @DrawableRes val icon: Int,
            val dailyWeatherData: List<DailyWeatherData>,
            val hourlyWeatherData: List<HourlyWeatherData>,
            val today: TodayWeather
        ) : ViewState()

        data class Error(val throwable: Throwable?) : ViewState()
    }

    data class TodayWeather(
        val tempDay: Float,
        val feelsLikeDay: Float,
        val tempEvening: Float,
        val feelsLikeEvening: Float,
        val tempNight: Float,
        val feelsLikeNight: Float,
        val tempMorning: Float,
        val feelsLikeMorning: Float,
        val humidity: Int,
        val windSpeed: Float,
        val description: String,
        val min: Float?,
        val max: Float?,
        @DrawableRes val icon: Int,
    )

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

    fun getLatLongFor(city: String) {
        viewModelScope.launch(coroutineContextProvider.IO) {
            weatherRepository.getLatLongFor(city).collect { cityRepoState ->
                when(cityRepoState) {
                    is CityRepoState.Success -> {
                        cityRepoState.response.coordination?.let { coord ->
                            withContext(coroutineContextProvider.Main) {
                                _event.value = ViewEvent.TriggerWeatherForCity(
                                    coord.lat,
                                    coord.lon)
                            }
                        }
                    }
                    is CityRepoState.Error -> {
                        withContext(coroutineContextProvider.Main) {
                            viewState.value = ViewState.Error(cityRepoState.error)
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
            icon = getWeatherIconFrom(weatherResponse.currentDayWeather.weatherMeta[0].weatherDescription),
            dailyWeatherData = weatherResponse.dailyWeatherData,
            hourlyWeatherData = hourlyWeatherMapper(weatherResponse.hourlyWeatherData),
            today = todayWeatherMapper(weatherResponse.dailyWeatherData[0])
        )
    }

    private fun todayWeatherMapper(dailyWeatherData: DailyWeatherData): TodayWeather {
        return TodayWeather(
            tempDay = dailyWeatherData.temp.day,
            feelsLikeDay = dailyWeatherData.feelsLike.day,
            tempMorning = dailyWeatherData.temp.morning,
            feelsLikeMorning = dailyWeatherData.feelsLike.morning,
            tempEvening = dailyWeatherData.temp.evening,
            feelsLikeEvening = dailyWeatherData.feelsLike.evening,
            tempNight = dailyWeatherData.temp.night,
            feelsLikeNight = dailyWeatherData.feelsLike.night,
            humidity = dailyWeatherData.humidity,
            windSpeed = dailyWeatherData.windSpeed,
            description = dailyWeatherData.weatherMeta[0].weatherDescription,
            min = dailyWeatherData.temp.min,
            max = dailyWeatherData.temp.max,
            icon = getWeatherIconFrom(dailyWeatherData.weatherMeta[0].weatherDescription)
        )
    }

    private fun hourlyWeatherMapper(hourlyWeatherData: List<WeatherData>): List<HourlyWeatherData> {
        return hourlyWeatherData.map { weatherData ->
            HourlyWeatherData(
                date = weatherData.date,
                sunsetTime = weatherData.sunsetTime,
                sunriseTime = weatherData.sunriseTime,
                temp = weatherData.temp,
                feelsLike = weatherData.feelsLike,
                pressure = weatherData.pressure,
                humidity = weatherData.humidity,
                dewPoint = weatherData.dewPoint,
                uvi = weatherData.uvi,
                clouds = weatherData.clouds,
                visibility = weatherData.visibility,
                windSpeed = weatherData.windSpeed,
                windDegree = weatherData.windDegree,
                weatherMeta = weatherData.weatherMeta,
                icon = getWeatherIconFrom(weatherData.weatherMeta[0].weatherDescription)
            )
        }
    }

    private fun getWeatherIconFrom(status: String): Int {
        return when {
            status.contains("scattered cloud") -> {
                R.drawable.ic_cloudy
            }
            status.contains("broken clouds") -> {
                R.drawable.ic_partly_cloudy
            }
            status.contains("few clouds") -> {
                R.drawable.ic_partly_cloudy
            }
            status.contains("overcast") -> {
                R.drawable.ic_cloudy
            }
            status.contains("cloud") -> {
                R.drawable.ic_partly_cloudy
            }
            status.contains("thunderstorm") -> {
                R.drawable.ic_thunderstorm
            }
            status.contains("moderate rain") -> {
                R.drawable.ic_moderate_rain
            }
            status.contains("light rain") -> {
                R.drawable.ic_light_rain
            }
            status.contains("rain") -> {
                R.drawable.ic_moderate_rain
            }
            status.contains("clear") -> {
                R.drawable.ic_sunny
            }
            else -> {
                R.drawable.ic_sunny
            }
        }
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
