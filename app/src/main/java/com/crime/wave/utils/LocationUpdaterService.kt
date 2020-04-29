package com.crime.wave.utils

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * Created by Mobile World on 4/13/2020.
 */
class LocationUpdaterService : Service() {
    var mLocationManager: LocationManager? = null
    var mLocationListener: LocationUpdaterListener? = null
    var isGPSEnabled = false
    var isNetworkEnabled = false
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        mLocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationListener = LocationUpdaterListener()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startListening()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("LocationChanged", "Destroyed")
        super.onDestroy()
    }

    private fun startListening() {
        Log.d("LocationChanged", "Started again")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isGPSEnabled = mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled =
                mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (isGPSEnabled) mLocationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10,
                50f,
                mLocationListener
            )
            if (isNetworkEnabled) mLocationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                10,
                50f,
                mLocationListener
            )
            if (!isGPSEnabled && !isNetworkEnabled) {
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                startActivity(intent);
            }
        }
        isRunning = true
    }

    private fun stopListening() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationManager!!.removeUpdates(mLocationListener)
        }
        isRunning = false
    }

    inner class LocationUpdaterListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            sendToServer(location)
        }

        override fun onProviderDisabled(provider: String) {
            stopListening()
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(
            provider: String,
            status: Int,
            extras: Bundle
        ) {
        }
    }

    private fun sendToServer(location: Location) {
        Log.d("TAG", "location changed");
        MPreferenceManager.saveDoubleInformation(
            applicationContext,
            "lat",
            location.latitude.toFloat()
        )
        MPreferenceManager.saveDoubleInformation(
            applicationContext,
            "lon",
            location.longitude.toFloat()
        )
        val intent = Intent("location_changed")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    companion object {
        var isRunning = false
    }
}
