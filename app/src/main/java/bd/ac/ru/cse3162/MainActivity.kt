package bd.ac.ru.cse3162

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

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