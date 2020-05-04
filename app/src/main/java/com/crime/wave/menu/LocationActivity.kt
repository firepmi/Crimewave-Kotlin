package com.crime.wave.menu

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.crime.wave.App
import com.crime.wave.R
import com.crime.wave.utils.MPreferenceManager
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import kotlinx.android.synthetic.main.activity_location.*
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
            val customlat = MPreferenceManager.readDoubleInformation(applicationContext, "customlat")
            val customlon = MPreferenceManager.readDoubleInformation(applicationContext, "customlon")
            if( customlat == 0.0 && customlon == 0.0) {
                onSelectCustomLocation()
            }
            refreshView()
        }
        selectCustomLocation.setOnClickListener {onSelectCustomLocation()}

        refreshView()
    }

    private fun refreshView(){
        val latitude = MPreferenceManager.readCurrentLocationInformation(applicationContext, "lat")
        val longitude = MPreferenceManager.readCurrentLocationInformation(applicationContext, "lon")
        if(latitude == 0.0 || longitude == 0.0) {
            txtCity1.text = "-"
        }
        else {
            val geoCoder = Geocoder(this, Locale.getDefault())
            val addresses =
                geoCoder.getFromLocation(latitude, longitude, 1)
            val cityName = addresses[0].locality
            val stateName = addresses[0].adminArea
            val countryName = addresses[0].countryName
            val zipCode = addresses[0].postalCode

            txtCity1.text = cityName
            if(zipCode != null ) txtState1.text = "$stateName $zipCode"
            else txtState1.text = stateName
            if(countryName != "United State" && countryName != "US") {
                if(zipCode != null ) txtState1.text = "$stateName $zipCode $countryName"
                else txtState1.text = "$stateName $countryName"
            }
        }
        val customLatitude = MPreferenceManager.readDoubleInformation(applicationContext, "customlat")
        val customLongitude = MPreferenceManager.readDoubleInformation(applicationContext, "customlon")
        if(customLatitude == 0.0 && customLongitude == 0.0) {
            txtCity2.text = "-"
            txtState2.text = ""
        }
        else {
            val geoCoder = Geocoder(this, Locale.getDefault())
            val addresses =
                geoCoder.getFromLocation(customLatitude, customLongitude, 1)
            if (addresses.size == 0) return
            val cityName = addresses[0].locality
            val stateName = addresses[0].adminArea
            val countryName = addresses[0].countryName
            val zipCode = addresses[0].postalCode

            txtCity2.text = cityName
            if(zipCode != null ) txtState2.text = "$stateName $zipCode"
            else txtState2.text = stateName
            if(countryName != "United State" && countryName != "US") {
                if(zipCode != null ) txtState2.text = "$stateName $zipCode $countryName"
                else txtState2.text = "$stateName $countryName"
            }
        }

        if(App.instance!!.isCustomLocation) {
            imgLocationArrow1.visibility = View.INVISIBLE
            imgLocationArrow2.visibility = View.VISIBLE
        }
        else {
            imgLocationArrow1.visibility = View.VISIBLE
            imgLocationArrow2.visibility = View.INVISIBLE
        }
    }
    private fun onSelectCustomLocation(){
        val locationPickerIntent = LocationPickerActivity.Builder()
            .withLocation(36.121439, -115.150098)
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
//                val address = data.getStringExtra(LOCATION_ADDRESS)
//                Log.d("ADDRESS****", address.toString())
//                val postalCode = data.getStringExtra(ZIPCODE)
//                Log.d("POSTALCODE****", postalCode.toString())
//                val fullAddress = data.getParcelableExtra<Address>(ADDRESS)
//                if (fullAddress != null) {
//                    Log.d("FULL ADDRESS****", fullAddress.toString())
//                }
                MPreferenceManager.saveDoubleInformation(
                    applicationContext,
                    "customlat",
                    latitude.toFloat()
                )
                MPreferenceManager.saveDoubleInformation(
                    applicationContext,
                    "customlon",
                    longitude.toFloat()
                )

                refreshView()
            }
        }
    }
}
