package com.abhat.simpleweather.data.repository

import com.abhat.simpleweather.data.model.City

sealed class CityRepoState {
    data class Success(val response: City): CityRepoState()
    class Error(val error: Throwable?): CityRepoState()
}