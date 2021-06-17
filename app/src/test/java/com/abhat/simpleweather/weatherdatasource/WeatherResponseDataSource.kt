package com.abhat.simpleweather.weatherdatasource

import com.abhat.simpleweather.data.model.*
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
            lat, lon, timeZone, timezoneOffset, currentDayWeather, dailyWeatherData, hourlyWeatherData
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
        dailyWeatherData: List<DailyWeatherData> = listOf(getDailyWeatherData()),
        hourlyWeatherData: List<WeatherData> = listOf(getWeatherData())
    ): WeatherViewModel.ViewState.Weather {
        return WeatherViewModel.ViewState.Weather(
            temp, feelsLike, humidity, windSpeed, description, min, max, dailyWeatherData, hourlyWeatherData
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
}