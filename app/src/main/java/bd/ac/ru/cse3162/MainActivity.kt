package bd.ac.ru.cse3162

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import bd.ac.ru.cse3162.databinding.ActivityMainBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var latt: String
    lateinit var long: String


    lateinit var mfusedlocation:FusedLocationProviderClient
    private var myRequestCode = 1010

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mfusedlocation = LocationServices.getFusedLocationProviderClient(this)
        binding.searchButton.setOnClickListener{doStuff(it)}

    }

    private fun doStuff(view: View){
        //var coordList: List<String>
        //coordList =
        getLastLocation()
        getJsonData()
    }

    private fun setValues(response: JSONObject) {
        binding.latitudeValue.text = response.getJSONArray("data").getJSONObject(0).getString("lat")
        binding.longitudeValue.text = response.getJSONArray("data").getJSONObject(0).getString("long")
    }

    private fun getJsonData() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val myKey = "00ad359085f24d918f2539a71417b869"
        val url = "https://api.weatherbit.io/v2.0/current?key=$myKey&lat=$latt&lon=$long"

        // Request a string response from the provided URL.
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                setValues(response)
            },
            Response.ErrorListener{ Toast.makeText(this, "Error fetching data", Toast.LENGTH_LONG).show()})

        // Add the request to the RequestQueue.
        queue.add(jsonRequest)
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        if(checkPermission()){
            if(locationEnable()){
                mfusedlocation.lastLocation.addOnCompleteListener{
                        task ->
                    var location: Location? = task.result
                    if(location == null){
                        newLocation()
                    }else{
                        latt = location.latitude.toString()
                        //var latitude = latt
                        long = location.longitude.toString()
                        //var longitude = long
                        //var coordList = listOf<String>(latitude, longitude)
                        Toast.makeText(this, "$latt", Toast.LENGTH_LONG).show()
                        Toast.makeText(this, "$long", Toast.LENGTH_LONG).show()
                        // return coordList
                    }
                }
            }else{
                Toast.makeText(this, "Please turn on your GPS location", Toast.LENGTH_LONG).show()
            }

        } else{
            requestPermission()
        }

    }

    @SuppressLint("MissingPermission")
    private fun newLocation() {
        var locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates =  1
        mfusedlocation = LocationServices.getFusedLocationProviderClient(this)
        mfusedlocation.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }

    private val locationCallback = object: LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation: Location =p0.lastLocation
        }
    }

    private fun locationEnable(): Boolean {
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            myRequestCode
        )
    }

    private fun checkPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == myRequestCode){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation()
            }
        }
    }
}