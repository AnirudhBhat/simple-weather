package com.abhat.simpleweather.data.model

import com.google.gson.annotations.SerializedName

data class WeatherMeta(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val weatherTitle: String,
    @SerializedName("description") val weatherDescription: String
)
