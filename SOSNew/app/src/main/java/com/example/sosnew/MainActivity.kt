package com.example.sosnew

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var send : Button
    private lateinit var delete : Button
    private lateinit var recycle : RecyclerView
    private lateinit var recycle2 : RecyclerView
    private lateinit var adapter: Adapter
    private lateinit var adapter1: Adapter2
    private lateinit var txtAddress : TextView
    //database
    private lateinit var db : MyDatabase
    //1
    private lateinit var sms : sms
    //2
    private val request = 111
    //3
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private val PLACE_PICKER_REQUEST = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        send.setOnClickListener {
            onClick()
        }
        delete.setOnClickListener {
            db.deleteAll()
            Toast.makeText(this,"Delete ", Toast.LENGTH_LONG).show()
            recycle2()
        }
    }

    override fun onResume() {
        super.onResume()
        isPermission()
    }

    private fun isPermission() {
        try {
            readContact()
            getLastLocation()

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS),
                PackageManager.PERMISSION_GRANTED
            )
        }
        catch (e : Exception) {
            Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
        }
    }

    private fun init() {
        initOfMap()
        send = findViewById(R.id.btn)
        delete = findViewById(R.id.delete)
        recycle = findViewById(R.id.recycle2)
        recycle2 = findViewById(R.id.recycle1)
        txtAddress = findViewById(R.id.txtAddress)
        db = MyDatabase(this)
        //db.query()
        sms = sms()
        recycle()
        recycle2()
        isPermission()
    }

    //get numberPhone for send sms
    fun recycle2 () {
        adapter1 = Adapter2(this, listTemp)
        recycle2.adapter = adapter1
        adapter1.notifyDataSetChanged()
        recycle2.setHasFixedSize(true)
    }

    //Location
    private fun initOfMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }
    //check uses permission
    private fun checkPermission() : Boolean {
        if(
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        )
            return true
        return false
    }
    //get uses permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),
            PLACE_PICKER_REQUEST
        )
    }
    //check Location service pf device enable
    private fun isLocationEnable() : Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun getNewLocation() {
        try {
            locationRequest = LocationRequest()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 0
            locationRequest.fastestInterval = 0
            locationRequest.numUpdates = 2
            //
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            //
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()
                //create val locationCallback
            )
        }
        catch (e : Exception) {
            Log.e("in getNewLocation: ",e.toString())
            Toast.makeText(this,e.message, Toast.LENGTH_LONG).show()
        }
    }
    private val locationCallback =
        object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val lastLocation = p0.lastLocation
                //set new Location
                val address = "Lat: ${lastLocation.latitude}, Long: ${lastLocation.longitude}\n" +
                        "Location: ${getLocaleName(lastLocation.latitude,lastLocation.longitude)}\n"
                txtAddress.text = address
            }
        }
    //get location
    fun getLocaleName(lat : Double, long : Double) : String{
        val address : String
        val geoCoder = Geocoder(this, Locale.getDefault())
        val location = geoCoder.getFromLocation(lat, long,1)
        address = location[0].getAddressLine(0)
        return address
    }

    private fun getLastLocation() {
        try {
            //check permission
            if (checkPermission()) {
                //check location service enable
                if (isLocationEnable()) {
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                        val location = task.result
                        if (location == null) {
                            //if location == null then will get the new user's location
                            getNewLocation()
                        } else {
                            val address = "Lat: ${location.latitude}, Long: ${location.longitude}\n" +
                                    "Location: ${getLocaleName(location.latitude,location.longitude)}\n"
                            txtAddress.text = address
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "You ever haven't allow enable your Location service",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                requestPermission()
            }
        }catch (e : Exception) {
            Log.e("in getLastLocation: ",e.toString())
            Toast.makeText(this,e.message, Toast.LENGTH_LONG).show()
        }
    }
    private fun recycle(){
        adapter = Adapter(this,listContact)
        recycle.adapter = adapter
        adapter.notifyDataSetChanged()
        recycle.setHasFixedSize(true)
    }
    private fun onClick() {
        val message = txtAddress.text.toString()
//        if(listTemp.isNotEmpty()) {
            sms.init(message, listTemp, this)
            Toast.makeText(this,"Send", Toast.LENGTH_LONG).show()
//        }
//        else
//            Toast.makeText(this,"You must add some numberPhone ", Toast.LENGTH_LONG).show()
    }

    //Read Contact List
    private fun readContact() {
        try {
            if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    Array(1) { Manifest.permission.READ_CONTACTS },
                    request
                )
            }
            else {
                queryListContact()
            }
        }
        catch (e : Exception) {
            Log.e("Error in one: ",e.toString())
            Toast.makeText(this,e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun queryListContact() {
        try {
            val contacts = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            listContact.clear()
            while (contacts!!.moveToNext()) {
                val name =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val numberPhone =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                listContact.add(DataContact(name, numberPhone))
            }
            //save on Database
            contacts.close()
        }
        catch (e : Exception){
            Log.e("Error in three: ",e.toString())
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Read Contact List
        if(requestCode == request && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            queryListContact()
        }
        // get Location
        if(requestCode == PLACE_PICKER_REQUEST) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("in onPermissionResult: ","not permission")
            }
        }
    }
}