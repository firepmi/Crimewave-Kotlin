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
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
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


class LocationActivity : AppCompatActivity(), PurchasesUpdatedListener {
    private lateinit var billingClient: BillingClient
    private val skuList = listOf("custom_location")
    @SuppressLint("SetTextI18n")
    var isPurchaseAvailable = false
    lateinit var skuDetails:SkuDetails
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
            onCustomLocation()
        }
        selectCustomLocation.setOnClickListener {onSelectCustomLocation()}

        refreshView()

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        setupBillingClient()
    }
    private fun onCustomLocation(){
        val customLat = MPreferenceManager.readLocationInformation(this@LocationActivity, "customlat")
        val customLon = MPreferenceManager.readLocationInformation(this@LocationActivity, "customlon")
        if( customLat == 0.0 && customLon == 0.0) {
            onSelectCustomLocation()
        }
        else {
            App.instance!!.isCustomLocation = true
            refreshView()
        }
    }
    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == OK) {
                    // The BillingClient is setup successfully
                    loadAllSKUs()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.

            }
        })
    }
    private fun loadAllSKUs() = if (billingClient.isReady) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == OK && skuDetailsList!!.isNotEmpty()) {
                for (skuDetails in skuDetailsList!!) {
                    //this will return both the SKUs from Google Play Console
                    if (skuDetails.sku == "custom_location"){
                        this.skuDetails = skuDetails
                        this.isPurchaseAvailable = true
                    }
                }
            }
        }
    } else {
        println("Billing Client not ready")
    }
    private fun onSelectCustomLocation(){
        if(isPurchaseAvailable) {
            onPurchaseCustomLocation(skuDetails)
        }
        else {
            Toast.makeText(this,"This service is not available for now.", LENGTH_SHORT).show();
        }
    }
    private fun onPurchaseCustomLocation(skuDetails:SkuDetails){
        val billingFlowParams = BillingFlowParams
            .newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        billingClient.launchBillingFlow(this, billingFlowParams)
    }
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
//        onSelectLocationDialog()
        if (billingResult?.responseCode == OK && purchases != null) {
            for (purchase in purchases) {
                acknowledgePurchase(purchase.purchaseToken)
            }
        } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Toast.makeText(this, "Purchase Canceled by user", LENGTH_LONG).show()
        } else if (billingResult?.responseCode == ITEM_ALREADY_OWNED) {
            onSelectLocationDialog()
        }
        else {
            // Handle any other error codes.
            Toast.makeText(this, "Purchase failed with error code ${billingResult?.responseCode}", LENGTH_LONG).show()
        }
    }
    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            if (responseCode == OK || responseCode == ITEM_ALREADY_OWNED) {
                onSelectLocationDialog()
            }
            else {
                Toast.makeText(this, debugMessage, LENGTH_LONG).show()
            }
        }
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

    private fun onSelectLocationDialog(){
        App.instance!!.isCustomLocation = true
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
