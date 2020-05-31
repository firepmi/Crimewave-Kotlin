package com.crime.wave.menu

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.crime.wave.App
import com.crime.wave.MainActivity
import com.crime.wave.R
import com.crime.wave.utils.MPreferenceManager
import com.google.android.gms.ads.AdRequest
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import kotlinx.android.synthetic.main.activity_location.*
import java.io.IOException
import java.util.*


class LocationActivity : AppCompatActivity(){

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        rootView.setOnClickListener {
            finish()
        }

        cvCurrentLocation.setOnClickListener {
            App.instance!!.isCustomLocation = false
            refreshView()
        }
        cvCustomLocation.setOnClickListener {
            App.instance!!.isCustomLocation = true
            val customLat = MPreferenceManager.readLocationInformation(this@LocationActivity, "customlat")
            val customLon = MPreferenceManager.readLocationInformation(this@LocationActivity, "customlon")
            if( customLat == 0.0 && customLon == 0.0) {
                onSelectCustomLocation()
            }
            refreshView()
        }
        selectCustomLocation.setOnClickListener {onSelectCustomLocation()}

        refreshView()

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onStop() {
        super.onStop()
        MainActivity.instance!!.crimeRadarFragment.getDataLocation()
    }

    private fun refreshView(){
        if(App.instance!!.isCustomLocation) {
            imgLocationArrow1.visibility = View.INVISIBLE
            imgLocationArrow2.visibility = View.VISIBLE
        }
        else {
            imgLocationArrow1.visibility = View.VISIBLE
            imgLocationArrow2.visibility = View.INVISIBLE
        }
        val latitude = MPreferenceManager.readLocationInformation(this@LocationActivity, "lat")
        val longitude = MPreferenceManager.readLocationInformation(this@LocationActivity, "lon")
        if(latitude == 0.0 || longitude == 0.0) {
            txtCity1.text = "-"
            txtState1.text = ""
        }
        else {
            try {
                val geoCoder = Geocoder(this, Locale.getDefault())
                val addresses =
                    geoCoder.getFromLocation(latitude, longitude, 1)
                val cityName = addresses[0].locality
                val stateName = addresses[0].adminArea
                val countryName = addresses[0].countryName
                val zipCode = addresses[0].postalCode

                txtCity1.text = cityName
                if (zipCode != null) txtState1.text = "$stateName $zipCode"
                else txtState1.text = stateName
                if (countryName != "United State" && countryName != "US") {
                    if (zipCode != null) txtState1.text = "$stateName $zipCode $countryName"
                    else txtState1.text = "$stateName $countryName"
                }
            }
            catch (e: IOException) {
                Toast.makeText(this, "Service not Available, Try again later", LENGTH_LONG).show()
                finish()
            }
        }

        val customLatitude = MPreferenceManager.readLocationInformation(this@LocationActivity, "customlat")
        val customLongitude = MPreferenceManager.readLocationInformation(this@LocationActivity, "customlon")
        if(customLatitude == 0.0 && customLongitude == 0.0) {
            txtCity2.text = "-"
            txtState2.text = ""
        }
        else {
            try {
                val geoCoder = Geocoder(this, Locale.getDefault())
                val addresses =
                    geoCoder.getFromLocation(customLatitude, customLongitude, 1)
                if (addresses.size == 0) {
                    txtCity2.text = "-"
                    txtState2.text = ""
                    return
                }
                val cityName = addresses[0].locality
                val stateName = addresses[0].adminArea
                val countryName = addresses[0].countryName
                val zipCode = addresses[0].postalCode

                txtCity2.text = cityName
                if (zipCode != null) txtState2.text = "$stateName $zipCode"
                else txtState2.text = stateName
                if (countryName != "United State" && countryName != "US") {
                    if (zipCode != null) txtState2.text = "$stateName $zipCode $countryName"
                    else txtState2.text = "$stateName $countryName"
                }
            }
            catch (e: IOException) {
                Toast.makeText(this, "Service not Available, Try again later", LENGTH_LONG).show()
                finish()
            }
        }
    }
    private fun onSelectCustomLocation(){
        var latitude = MPreferenceManager.readLocationInformation(this@LocationActivity, "customlat")
        var longitude = MPreferenceManager.readLocationInformation(this@LocationActivity, "customlon")
        if(latitude == 0.0 && longitude == 0.0) {
            latitude = MPreferenceManager.readLocationInformation(this@LocationActivity, "lat")
            longitude = MPreferenceManager.readLocationInformation(this@LocationActivity, "lon")
        }
        if(latitude == 0.0 && longitude == 0.0) {
            latitude = 36.121439
            longitude = -115.150098
        }

        val locationPickerIntent = LocationPickerActivity.Builder()
            .withLocation(latitude, longitude)
            .withGeolocApiKey("AIzaSyBX-HDsCLh2zSa2U7auZOeT1q3PwJZhax8")
//                .withSearchZone("es_ES")
//                .withSearchZone(SearchZoneRect(LatLng(26.525467, -18.910366), LatLng(43.906271, 5.394197)))
            .withDefaultLocaleSearchZone()
            .shouldReturnOkOnBackPressed()
//                .withStreetHidden()
//                .withCityHidden()
//                .withZipCodeHidden()
//                .withSatelliteViewHidden()
            .withGooglePlacesEnabled()
            .withGoogleTimeZoneEnabled()
//                .withVoiceSearchHidden()
//                .withUnnamedRoadHidden()
            .build(applicationContext)

        startActivityForResult(locationPickerIntent, 100)
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK && data != null) {
//                if (requestCode == 1) {
                val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                Log.d("LATITUDE****", latitude.toString())
                val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                Log.d("LONGITUDE****", longitude.toString())
                MPreferenceManager.saveDoubleInformation(this@LocationActivity,"customlat",latitude.toFloat())
                MPreferenceManager.saveDoubleInformation(this@LocationActivity,"customlon",longitude.toFloat())

                App.instance!!.isCustomLocation = true
                refreshView()
            }
        }
    }
}
