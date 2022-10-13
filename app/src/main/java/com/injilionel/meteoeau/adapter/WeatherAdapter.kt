package com.injilionel.meteoeau.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.injilionel.meteoeau.R
import com.injilionel.meteoeau.model.WeatherData

 class WeatherAdapter(var c:Context,
                     var weatherList:ArrayList<WeatherData>):
RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>()
{
    inner class WeatherViewHolder (v: View):RecyclerView.ViewHolder(v)
    {
        val tempoImg = v.findViewById<ImageView>(R.id.tempoImg)
        val tempo = v.findViewById<TextView>(R.id.tempo)
        val tempoInfo = v.findViewById<TextView>(R.id.tempoInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.weather_item, parent, false)
        return WeatherViewHolder(v)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        var newWeatherList = weatherList[position]
        holder.tempo.text = newWeatherList.tempo
        holder.tempoInfo.text = newWeatherList.tempoInfo
        holder.tempoImg.setImageResource(newWeatherList.img)
    }

    override fun getItemCount(): Int = weatherList.size

}