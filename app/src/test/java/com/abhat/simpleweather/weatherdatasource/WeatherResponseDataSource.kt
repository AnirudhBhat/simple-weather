package com.abhat.simpleweather.weatherdatasource

import com.abhat.simpleweather.R
import com.abhat.simpleweather.data.model.*
import com.abhat.simpleweather.data.repository.WeatherRepoState
import com.abhat.simpleweather.ui.HourlyWeatherData
import com.abhat.simpleweather.ui.WeatherViewModel

object WeatherResponseData {
    fun getWeatherResponse(
        lat: Float = 0f,
        lon: Float = 0f,
        timeZone: String = "",
        timezoneOffset: Int = 0,
        currentDayWeather: WeatherData = getWeatherData(),
        dailyWeatherData: List<DailyWeatherData> = listOf(getDailyWeatherData()),
        hourlyWeatherData: List<WeatherData> = listOf(getWeatherData())
    ): WeatherResponse {
        return WeatherResponse(
            lat,
            lon,
            timeZone,
            timezoneOffset,
            currentDayWeather,
            dailyWeatherData,
            hourlyWeatherData
        )
    }

    fun getSuccessWeatherRepoState(
        currentDayWeather: WeatherData = getWeatherData(),
        dailyWeatherData: List<DailyWeatherData> = listOf(getDailyWeatherData()),
        hourlyWeatherData: List<WeatherData> = listOf(getWeatherData())
    ): WeatherRepoState<WeatherResponse> {
        return WeatherRepoState.Success(
            weatherResponse = getWeatherResponse(
                currentDayWeather = currentDayWeather,
                dailyWeatherData = dailyWeatherData,
                hourlyWeatherData = hourlyWeatherData
            )
        )
    }

    fun getWeatherData(
        date: Long = 0L,
        sunriseTime: Long = 0L,
        sunsetTime: Long = 0L,
        temp: Float = 0F,
        feelsLike: Float = 0F,
        pressure: Int = 0,
        humidity: Int = 0,
        dewPoint: Float = 0F,
        uvi: Float = 0F,
        clouds: Int = 0,
        visibility: Int = 0,
        windSpeed: Float = 0F,
        windDegree: Int = 0,
        weatherMeta: List<WeatherMeta> = listOf(getWeatherMeta())
    ): WeatherData {
        return WeatherData(
            date,
            sunriseTime,
            sunsetTime,
            temp,
            feelsLike,
            pressure,
            humidity,
            dewPoint,
            uvi,
            clouds,
            visibility,
            windSpeed,
            windDegree,
            weatherMeta
        )
    }

    fun getWeatherState(
        temp: Float = 0F,
        feelsLike: Float = 0F,
        humidity: Int = 0,
        windSpeed: Float = 0F,
        description: String = "",
        min: Float = 0F,
        max: Float = 0F,
        icon: Int = R.drawable.ic_sunny,
        dailyWeatherData: List<DailyWeatherData> = listOf(getDailyWeatherData()),
        hourlyWeatherData: List<HourlyWeatherData> = listOf(getHourlyWeatherData()),
        today: WeatherViewModel.TodayWeather = getTodayWeather(description, icon)
    ): WeatherViewModel.ViewState.Weather {
        return WeatherViewModel.ViewState.Weather(
            temp,
            feelsLike,
            humidity,
            windSpeed,
            description,
            min,
            max,
            icon,
            dailyWeatherData,
            hourlyWeatherData,
            today
        )
    }

    fun getTodayWeather(description: String = "", icon: Int): WeatherViewModel.TodayWeather {
        return WeatherViewModel.TodayWeather(
            tempDay = 0F,
            feelsLikeDay = 0F,
            tempEvening = 0F,
            feelsLikeEvening = 0F,
            tempNight = 0F,
            feelsLikeNight = 0F,
            tempMorning = 0F,
            feelsLikeMorning = 0F,
            humidity = 0,
            windSpeed = 0F,
            description = description,
            min = 0F,
            max = 0F,
            icon = icon
        )
    }

    fun getHourlyWeatherData(
        temp: Float = 0F,
        feelsLike: Float = 0F,
        status: String = "",
        icon: Int = R.drawable.ic_sunny
    ): HourlyWeatherData {
        return HourlyWeatherData(
            date = 0L,
            sunriseTime = 0L,
            sunsetTime = 0L,
            temp = 0F,
            feelsLike = 0F,
            pressure = 0,
            humidity = 0,
            dewPoint = 0F,
            uvi = 0F,
            clouds = 0,
            visibility = 0,
            windSpeed = 0F,
            windDegree = 0,
            weatherMeta = listOf(getWeatherMeta(description = status)),
            icon = icon
        )
    }

    fun getDailyWeatherData(
        date: Long = 0L,
        sunriseTime: Long = 0L,
        sunsetTime: Long = 0L,
        moonRiseTime: Long = 0L,
        moonSetTime: Long = 0L,
        moonPhase: Float = 0F,
        temp: Temp = getTemp(),
        feelsLike: FeelsLike = getFeelsLike(),
        pressure: Int = 0,
        humidity: Int = 0,
        dewPoint: Float = 0F,
        uvi: Float = 0F,
        clouds: Int = 0,
        windSpeed: Float = 0F,
        windDegree: Int = 0,
        weatherMeta: List<WeatherMeta> = listOf(getWeatherMeta())
    ): DailyWeatherData {
        return DailyWeatherData(
            date,
            sunriseTime,
            sunsetTime,
            moonRiseTime,
            moonSetTime,
            moonPhase,
            temp,
            feelsLike,
            pressure,
            humidity,
            dewPoint,
            uvi,
            clouds,
            windSpeed,
            windDegree,
            weatherMeta
        )
    }

    fun getFeelsLike(
        day: Float = 0F,
        night: Float = 0F,
        evening: Float = 0F,
        morning: Float = 0F
    ): FeelsLike {
        return FeelsLike(
            day, night, evening, morning
        )
    }

    fun getTemp(
        day: Float = 0F,
        min: Float = 0F,
        max: Float = 0F,
        night: Float = 0F,
        evening: Float = 0F,
        morning: Float = 0F
    ): Temp {
        return Temp(
            day, min, max, night, evening, morning
        )
    }

    fun getWeatherMeta(
        id: Int = 0,
        weatherTitle: String = "",
        description: String = ""
    ): WeatherMeta {
        return WeatherMeta(
            id, weatherTitle, description
        )
    }

    fun getCloudsWeatherData(description: String): WeatherData {
        return getWeatherData(
            weatherMeta = listOf(
                getWeatherMeta(
                    description = description
                )
            )
        )
    }


    fun getCloudsDailyWeatherData(description: String): DailyWeatherData {
        return getDailyWeatherData(
            weatherMeta = listOf(
                getWeatherMeta(
                    description = description
                )
            )
        )
    }

    fun getCloudsWeatherDataWithTempDetails(
        humidity: Int = 0,
        windSpeed: Float = 0F,
        temp: Float = 0F,
        feelsLike: Float = 0F
    ): WeatherData {
        return getWeatherData(
            windSpeed = windSpeed,
            humidity = humidity,
            temp = temp,
            feelsLike = feelsLike
        )
    }

    fun getCloudsDailyWeatherDataWithTempDetails(
        min: Float = 0F,
        max: Float = 0F,
        temp: Float = 0F,
        feelsLike: Float = 0F
    ): DailyWeatherData {
        return getDailyWeatherData(
            temp = getTemp(
                min = min,
                max = max
            )
        )
    }
}