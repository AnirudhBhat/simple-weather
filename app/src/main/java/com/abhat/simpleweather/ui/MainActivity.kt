package com.abhat.simpleweather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhat.simpleweather.R
import com.abhat.simpleweather.data.model.WeatherData
import com.abhat.simpleweather.data.model.WeatherResponse
import com.abhat.simpleweather.data.network.WeatherApi
import com.abhat.simpleweather.data.repository.Repository
import com.abhat.simpleweather.data.repository.WeatherRepository
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val latLongMap = hashMapOf(
        "Bengaluru" to Pair(12.9762F, 77.6033F),
        "Shivamogga" to Pair(13.9167F, 75.5667F),
        "Udupi" to Pair(13.35F, 74.75F),
        "Madikeri" to Pair(12.4167F, 75.7333F),
        "Chikmagaluru" to Pair(13.3167F, 75.7833F),
        "Ooty" to Pair(11.4F, 76.7F)
    )

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

    private val repository: Repository<WeatherResponse> by lazy {
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
        observeEvent()
        observeEditText()
        if (savedInstanceState == null) {
            weatherViewModel.getWeatherFor(12.9762F, 77.6033F)
        }
        findViewById<Button>(R.id.btn_bengaluru).setOnClickListener {
            val pair = latLongMap["Bengaluru"]
            weatherViewModel.getWeatherFor(pair?.first!!, pair?.second)
        }

        findViewById<Button>(R.id.btn_shivamogga).setOnClickListener {
            val pair = latLongMap["Shivamogga"]
            weatherViewModel.getWeatherFor(pair?.first!!, pair?.second)
        }

        findViewById<Button>(R.id.btn_udupi).setOnClickListener {
            val pair = latLongMap["Udupi"]
            weatherViewModel.getWeatherFor(pair?.first!!, pair?.second)
        }

        findViewById<Button>(R.id.btn_madikeri).setOnClickListener {
            val pair = latLongMap["Madikeri"]
            weatherViewModel.getWeatherFor(pair?.first!!, pair?.second)
        }

        findViewById<Button>(R.id.btn_chikmagaluru).setOnClickListener {
            val pair = latLongMap["Chikmagaluru"]
            weatherViewModel.getWeatherFor(pair?.first!!, pair?.second)
        }

        findViewById<Button>(R.id.btn_ooty).setOnClickListener {
            val pair = latLongMap["Ooty"]
            weatherViewModel.getWeatherFor(pair?.first!!, pair?.second)
        }
    }

    private fun observeEvent() {
        weatherViewModel.event.observe(this, Observer { viewEvent ->
            when (viewEvent) {
                is WeatherViewModel.ViewEvent.TriggerWeatherForCity -> {
                    weatherViewModel.getWeatherFor(viewEvent.lat, viewEvent.lon)
                }
            }
        })
    }

    private fun observeEditText() {
        findViewById<EditText>(R.id.et_search).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                GlobalScope.launch {
                    delay(800)
                    weatherViewModel.getLatLongFor(s.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
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
                    // Current time weather
                    findViewById<TextView>(R.id.tv_temp).text = viewState.temp.roundToInt().toString() + "\u00B0"
                    findViewById<TextView>(R.id.tv_feels_like_temp).text = viewState.feelsLike.roundToInt().toString() + "\u00B0"
                    findViewById<TextView>(R.id.tv_description).text = viewState.description
                    findViewById<TextView>(R.id.tv_humidity).text = viewState.humidity.toString() + "%"
                    findViewById<TextView>(R.id.tv_wind_speed).text = viewState.windSpeed.toString() + " km/h"
                    findViewById<ImageView>(R.id.iv_weather_image).setBackgroundDrawable(ContextCompat.getDrawable(this, viewState.icon))
                    viewState.max?.let { maxTemp ->
                        findViewById<Group>(R.id.group_max_min).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.tv_max_temp).text = maxTemp.roundToInt().toString() + "\u00B0"
                        findViewById<TextView>(R.id.tv_min_temp).text = viewState.min?.roundToInt()?.toString() + "\u00B0"
                    } ?: run {
                        findViewById<Group>(R.id.group_max_min).visibility = View.GONE
                    }

                    // Today weather
                    findViewById<TextView>(R.id.tv_today_weather).text = viewState.today.tempDay.roundToInt().toString() + "\u00B0"
                    findViewById<TextView>(R.id.tv_today_feels_like_temp).text = viewState.today.feelsLikeDay.roundToInt().toString() + "\u00B0"
                    findViewById<TextView>(R.id.tv_today_weather_description).text = viewState.today.description
                    findViewById<ImageView>(R.id.iv_today_weather).setBackgroundDrawable(ContextCompat.getDrawable(this, viewState.today.icon))

                    // Morning weather
                    findViewById<TextView>(R.id.tv_morning_weather).text = viewState.today.tempMorning.roundToInt().toString() + "\u00B0"
                    findViewById<TextView>(R.id.tv_morning_feels_like_temp).text = viewState.today.feelsLikeMorning.roundToInt().toString() + "\u00B0"

                    // Evening weather
                    findViewById<TextView>(R.id.tv_evening_weather).text = viewState.today.tempEvening.roundToInt().toString() + "\u00B0"
                    findViewById<TextView>(R.id.tv_evening_feels_like_temp).text = viewState.today.feelsLikeEvening.roundToInt().toString() + "\u00B0"

                    // Night weather
                    findViewById<TextView>(R.id.tv_night_weather).text = viewState.today.tempNight.roundToInt().toString() + "\u00B0"
                    findViewById<TextView>(R.id.tv_night_feels_like_temp).text = viewState.today.feelsLikeNight.roundToInt().toString() + "\u00B0"

                    setupHourlyRecycler(viewState.hourlyWeatherData)
                }

                is WeatherViewModel.ViewState.Error -> {
                    Toast.makeText(this, viewState.throwable?.message ?: "", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupHourlyRecycler(hourlyWeatherData: List<HourlyWeatherData>) {
        val hourlyRecycler = findViewById<RecyclerView>(R.id.rv_hourly_weather)
        hourlyRecycler.adapter = HourlyWeatherAdapter(hourlyWeatherData)
        hourlyRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }
}