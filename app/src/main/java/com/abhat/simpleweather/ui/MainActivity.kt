package com.abhat.simpleweather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhat.simpleweather.R
import com.abhat.simpleweather.data.network.WeatherApi
import com.abhat.simpleweather.data.repository.Repository
import com.abhat.simpleweather.data.repository.WeatherRepository
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private fun getLogger(): HttpLoggingInterceptor {
        val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Log.d("okhttp", it)
        })
        logger.level = HttpLoggingInterceptor.Level.BODY
        return logger
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(getLogger())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .client(okHttpClient)
            .build()
    }

    private val weatherApi: WeatherApi by lazy {
        retrofit.create(WeatherApi::class.java)
    }

    private val repository: Repository by lazy {
        WeatherRepository(weatherApi = weatherApi)
    }

    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider(this, viewModelFactory {
            WeatherViewModel(repository, CoroutineContextProvider())
        }).get(WeatherViewModel::class.java)
    }

    inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        observeViewModel()
        if (savedInstanceState == null) {
            weatherViewModel.getWeatherFor(12.9762F, 77.6033F)
        }
    }

    private fun observeViewModel() {
        weatherViewModel.viewStateData.observe(this, Observer { viewState ->
            if (viewState.isLoading) {
                findViewById<ProgressBar>(R.id.pb_weather).visibility = View.VISIBLE
            } else {
                findViewById<ProgressBar>(R.id.pb_weather).visibility = View.GONE
            }
            when (viewState) {
                is WeatherViewModel.ViewState.Weather -> {
                    findViewById<TextView>(R.id.tv_temp).text = String.format("%.1f", viewState.temp) + "\u00B0"
                    findViewById<TextView>(R.id.tv_feels_like_temp).text = String.format("%.1f", viewState.feelsLike) + "\u00B0"
                    findViewById<TextView>(R.id.tv_description).text = viewState.description
                    viewState.max?.let { maxTemp ->
                        findViewById<Group>(R.id.group_max_min).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.tv_max_temp).text = String.format("%.1f", maxTemp) + "\u00B0"
                        findViewById<TextView>(R.id.tv_min_temp).text = String.format("%.1f", viewState.min) + "\u00B0"
                    } ?: run {
                        findViewById<Group>(R.id.group_max_min).visibility = View.GONE
                    }
                }

                is WeatherViewModel.ViewState.Error -> {
                    Toast.makeText(this, viewState.throwable?.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}