package com.abhat.simpleweather.data.model

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("coord") val coordination: Coordination? = null
) {
    data class Coordination(
        val lat: Float,
        val lon: Float
    )
}