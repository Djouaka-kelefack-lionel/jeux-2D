 package com.injilionel.meteoeau

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.injilionel.meteoeau.adapter.WeatherAdapter
import com.injilionel.meteoeau.model.WeatherData
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList

 class MainActivity : AppCompatActivity() {

     var CITTA: String = ""
     var API: String = "6cde54b629dbe47098e2a97279e9f304 "
     private lateinit var fusedLocationClient: FusedLocationProviderClient

     private lateinit var weatherList: ArrayList<WeatherData>
     private lateinit var weatherAdapter: WeatherAdapter

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)

         fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


         obtainLocation()
         weatherBtn.setOnClickListener {
             var nome_citta = nome_citta.text.toString()
             if (!nome_citta.equals(" ")) {
                 CITTA = nome_citta
                 WeatherTask().execute()
             }
         }
     }

     @SuppressLint("MissingPermission")
     private fun obtainLocation() {
         fusedLocationClient.lastLocation
             .addOnSuccessListener { location :Location? ->
                var current_Nome_Citta = getNomeCitta(location?.latitude,location?.longitude)
                 CITTA = current_Nome_Citta
                 WeatherTask().execute()
             }
     }

     private fun getNomeCitta(longitude: Double?, latitude: Double?): String {
         var nomeCitta = "Not Found"

         val gcd :Geocoder = Geocoder(this@MainActivity, Locale.getDefault())
         try {
             val address = gcd.getFromLocation(latitude!!,longitude!!,10)
             for(adr in address){
                 if (adr != null){
                     val citta = adr.locality
                     if (citta != null && !citta.equals(" ")){
                         nomeCitta = citta
                     }
                 }
             }
         }
         catch (e:Exception){
             e.printStackTrace()
         }
         return nomeCitta
     }
     inner class WeatherTask() :AsyncTask<String,Void,String>()
     {
         override fun onPreExecute() {
             super.onPreExecute()
             loader.visibility = View.VISIBLE
             mainContainer.visibility = View.GONE
             errorText.visibility = View.GONE
         }

         override fun doInBackground(vararg p0: String?): String {
             var response: String?
             try {
                 response = URL("https://api.openweathermap.org/data/2.5/weather?q=${CITTA.lowercase()}&units=metric&appid=$API").readText(
                     Charsets.UTF_8)
             }
             catch (e:Exception){
                  response = null
             }
             return response !!
         }

         @RequiresApi(Build.VERSION_CODES.N)
         override fun onPostExecute(result: String?) {
             super.onPostExecute(result)
             try {
                 val jsonObj = JSONObject(result)
                 val main = jsonObj.getJSONObject("main")
                 val sys = jsonObj.getJSONObject("sys")
                 val wind = jsonObj.getJSONObject("wind")
                 val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                 val updatedAt :Long = jsonObj.getLong("dt")
                 val updatedAtText = "Update at:" + SimpleDateFormat("dd/MM/yy:mm a", Locale.getDefault())
                     .format(Date(updatedAt * 1000))
                 val temp = main.getString("Tempo") + "°C"
                 val tempMin = "Temp Min :" +main.getString("temp_min") + "°C"
                 val tempMax = "Temp Max :" +main.getString("temp_max") + "°C"
                 val pressione = main.getString("pressione")
                 val sunrise = main.getLong("sunrise")
                 val sunset = main.getLong("sunset")
                 val humidity = main.getString("humidità") + "°C"
                 val windSpeed = wind.getString("speed")
                 val weatherDescription = weather.getString("description")

                 val mindirizzo = jsonObj.getString("nome") + "," + sys.getString("country")
                 indirizzo.text = mindirizzo
                 aggiorna.text = updatedAtText
                 statuss.text = weatherDescription.capitalize()

                 temps.text = temp
                 temp_min.text = tempMin
                 temp_max.text = tempMax

                 weatherList = ArrayList()

                 weatherAdapter = WeatherAdapter(this@MainActivity, weatherList)
                 informazione_tempo.layoutManager = LinearLayoutManager(
                     this@MainActivity,LinearLayoutManager.HORIZONTAL,false)

                 informazione_tempo.adapter = weatherAdapter

                 weatherList.add(WeatherData(R.drawable.sunrise, "Sunrise", SimpleDateFormat("hh:mm a",Locale.getDefault())
                     .format(Date(sunrise*1000))))

                 weatherList.add(WeatherData(R.drawable.sunset, "Sunset", SimpleDateFormat("hh:mm a",Locale.getDefault())
                     .format(Date(sunset*1000))))

                 weatherList.add(WeatherData(R.drawable.wind, "Wind",windSpeed))
                 weatherList.add(WeatherData(R.drawable.pressure, "Pressione", pressione))
                 weatherList.add(WeatherData(R.drawable.humidity, "Umidità",humidity))

                 loader.visibility = View.GONE
                 mainContainer.visibility = View.VISIBLE
             }
             catch (e:Exception){
                 loader.visibility = View.GONE
                 errorText.visibility = View.VISIBLE
             }
         }

     }
 }

