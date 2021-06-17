package com.abhat.simpleweather.ui

import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.abhat.simpleweather.R
import com.abhat.simpleweather.data.model.WeatherData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class HourlyWeatherAdapter(private val hourlyWeatherData: List<HourlyWeatherData>): RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {

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
            val temp = itemView.findViewById<TextView>(R.id.tv_temp_hourly)
            temp.text = hourlyWeatherData[adapterPosition].temp.roundToInt().toString() + "\u00B0"
            itemView.findViewById<TextView>(R.id.tv_hour).text = getFormattedDate(Date(hourlyWeatherData[adapterPosition].date * 1000))
//            itemView.findViewById<TextView>(R.id.tv_status).text =hourlyWeatherData[adapterPosition].weatherMeta[0].weatherDescription
            itemView.findViewById<ImageView>(R.id.iv_status).setBackgroundDrawable(ContextCompat.getDrawable(itemView.context, hourlyWeatherData[adapterPosition].icon))
            val params = temp.layoutParams as ConstraintLayout.LayoutParams
            params.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                (hourlyWeatherData[adapterPosition].temp.roundToInt() * 5).toFloat(),
                Resources.getSystem().displayMetrics).toInt()
            temp.layoutParams = params
        }
    }

    private fun getFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("haa")
        return dateFormat.format(date).toString().replace("AM", "am").replace("PM", "pm")
    }
}