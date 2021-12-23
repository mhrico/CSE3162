package bd.ac.ru.cse3162

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val LOCATION_PERMISSION_REQ_CODE = 1000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        bSearch.setOnClickListener {
            getCurrentLocation()
        }

    }
    private fun getCurrentLocation() {
        // checking location permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);

            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                latitude = location.latitude
                longitude = location.longitude


                var cityName = getCity(latitude,longitude)
                var countryName = getCountry(latitude,longitude)

                tv_latitude.text = "${latitude}"
                tv_longitude.text = "${longitude}"
                tv_cityCountry.text = "${cityName} , $countryName"



            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed on getting current location",
                    Toast.LENGTH_SHORT).show()
            }

            getWeather(latitude,longitude);

    }

    private fun getWeather(latitude : Double,longitude : Double)
    {

        val queue = Volley.newRequestQueue(this)

        var url = "https://api.weatherbit.io/v2.0/current?lat=${latitude}&lon=${longitude}&key=331ce978da5d446d953f327f3f948f5f&fbclid=IwAR0mz6Fwbo4r6BnRM7n_qXt1g8Dqmwjzv4v5cPES30M_XoTGMfQxj-yCUMA"


        try {
            val jsonRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    setValues(response)
                },
                Response.ErrorListener {
                    Toast.makeText(
                        this,
                        "Please turn on internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                })
            queue.add(jsonRequest)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "ERROR" + e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setValues(response: JSONObject) {
        tv_sunrise_time.text =  response.getJSONArray("data").getJSONObject(0).getString("sunrise")
        tv_sunset_time.text =  response.getJSONArray("data").getJSONObject(0).getString("sunset")

        var tmp = response.getJSONArray("data").getJSONObject(0).getString("temp")
        tv_celsius.text =tmp+"°C"
        var tmp1 = tmp.toDouble()
        var far = (tmp1 * 9/ 5) + 32;
        tv_fahrenheit.text =far.toString()+"°F"
        tv_humidity.text ="আর্দ্রতা :"+ response.getJSONArray("data").getJSONObject(0).getString("rh")+"%"
        tv_pressure.text ="বায়ু চাপ :"+ response.getJSONArray("data").getJSONObject(0).getString("pres")+"mb"
        tv_vision_range.text =  "দৃষ্টি সীমা  : "+response.getJSONArray("data").getJSONObject(0).getString("vis")+" Km/h"
        tv_time.text = response.getJSONArray("data").getJSONObject(0).getString("datetime")
        tv_status.text =  response.getJSONArray("data").getJSONObject(0).getJSONObject("weather").getString("description")

    }


    private fun getCity(lat: Double ,lng: Double):String{
        var geocoder = Geocoder(this)
        var  list = geocoder.getFromLocation(lat,lng,1)
        return list[0].locality
    }
    private fun getCountry(lat: Double ,lng: Double):String{
        var geocoder = Geocoder(this)
        var  list = geocoder.getFromLocation(lat,lng,1)
        return list[0].countryName
    }
}
//apikey : 331ce978da5d446d953f327f3f948f5f