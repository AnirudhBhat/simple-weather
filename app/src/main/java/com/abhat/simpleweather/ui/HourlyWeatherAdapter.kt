package com.abhat.simpleweather.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abhat.simpleweather.R
import com.abhat.simpleweather.data.model.WeatherData
import java.text.SimpleDateFormat
import java.util.*

class HourlyWeatherAdapter(private val hourlyWeatherData: List<WeatherData>): RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly_weather_data, parent, false)
        return HourlyWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return hourlyWeatherData.size
    }

    inner class HourlyWeatherViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind() {
            itemView.findViewById<TextView>(R.id.tv_temp_hourly).text = String.format("%.1f", hourlyWeatherData[adapterPosition].temp) + "\u00B0"
            itemView.findViewById<TextView>(R.id.tv_hour).text = getFormattedDate(Date(hourlyWeatherData[adapterPosition].date * 1000))
            itemView.findViewById<TextView>(R.id.tv_status).text =hourlyWeatherData[adapterPosition].weatherMeta[0].weatherDescription
        }
    }

    private fun getFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("haa")
        return dateFormat.format(date).toString().replace("AM", "am").replace("PM", "pm")
    }
}