package com.crime.wave.crimeRadar

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.fromHtml
import android.text.Spanned
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.refactor.lib.colordialog.PromptDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.crime.wave.App
import com.crime.wave.R
import com.crime.wave.utils.MPreferenceManager
import com.crime.wave.utils.ShowProgressDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.activity_crime_radar.*
import org.apache.commons.lang3.StringUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by Mobile World on 4/10/2020.
 */

class CrimeRadarActivity : AppCompatActivity() {
    private var map: GoogleMap? = null
    var tabTitles = arrayOf(
        "Robbery",
        "Burglary",
        "Theft",
        "Shooting",
        "Assault",
        "Vandalism",
        "Arrest",
        "Other"
    )

    //    int currentIndex = Global.crimeType;
    var currentIndex = 0
    private var jsonCrimes = JSONArray()
    private var currentCrimeArray = JSONArray()
    var crimeLevel = App.instance!!.crimeLevel
    private var mapType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_radar)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter("location_changed")
        )
        viewPager.adapter = SectionPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onTabSelected(tab: TabLayout.Tab) {
                tvTitle.text = "${App.instance!!.levelTexts[App.instance!!.crimeLevel]} ${tabTitles[tab.position]} Area"
                currentIndex = tab.position
                try {
                    crimeLevel = checkForLocation()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                setTitleBar()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        for (i in 0..7) {
            tabLayout.getTabAt(i)!!.setIcon(R.drawable.red_dot)
        }
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        MapsInitializer.initialize(this)

        mapView.getMapAsync { mMap ->
            // For showing a move to my location button
            if (ActivityCompat.checkSelfPermission(
                    this@CrimeRadarActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@CrimeRadarActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@getMapAsync
            }

            mMap.isMyLocationEnabled = false
            val markerOptions = MarkerOptions()
            val latLng = LatLng(
                MPreferenceManager.readDoubleInformation(this, "lat"),
                MPreferenceManager.readDoubleInformation(this, "lon")
            )
            markerOptions.position(latLng)
            markerOptions.title("My Location")
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f))
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot))
            mMap.addMarker(markerOptions)
            getDataLocation()
            setTitleBar()
            map = mMap
        }
        ivFilter.setOnClickListener {
            mapType++
            map!!.mapType = mapTypes[mapType % mapTypes.size]
        }
        ivBack.setOnClickListener {
            finish()
        }
    }

    private var mapTypes = intArrayOf(
        GoogleMap.MAP_TYPE_NORMAL,
        GoogleMap.MAP_TYPE_SATELLITE,
        GoogleMap.MAP_TYPE_TERRAIN,
        GoogleMap.MAP_TYPE_HYBRID
    )

    @SuppressLint("SetTextI18n")
    private fun setTitleBar() {
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(App.instance!!.titleColors[crimeLevel]), Color.TRANSPARENT)
        )
        gd.cornerRadius = 0f
        mainLayout!!.background = gd
        tvTitle.text = "${App.instance!!.levelTexts[crimeLevel]} ${tabTitles[currentIndex]} Area"
        tabLayout.setBackgroundColor(
            Color.parseColor(
                App.instance!!.titleColors[crimeLevel]
            )
        )
        if (currentIndex == 7) {
            tvTitle.text = "${App.instance!!.levelTexts[crimeLevel]} Misc Crime Area"
        }
        if (crimeLevel == 2) {
            tvTitle!!.setTextColor(Color.BLACK)
            tabLayout.setTabTextColors(
                Color.parseColor("#666666"),
                Color.BLACK
            )
        } else {
            tvTitle!!.setTextColor(Color.WHITE)
            tabLayout.setTabTextColors(
                Color.parseColor("#CCCCCC"),
                Color.WHITE
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor(
                App.instance!!.titleColors[crimeLevel]
            )
        }
    }

    private fun getDataLocation() {
        val url =
            "http://api.spotcrime.com/crimes.json?lat=" + MPreferenceManager.readDoubleInformation(
                this,
                "lat"
            ).toString() + "&lon=" + MPreferenceManager.readDoubleInformation(this, "lon")
                .toString() + "&radius=0.01&key=" + App.instance!!.key + "&callback="

        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url, Response.Listener { response ->

                jsonCrimes = if (response == null) {
                    JSONArray(ArrayList<String?>())
                } else {
                    val res = JSONObject(response)
                    res.getJSONArray("crimes")
                }
                crimeLevel = checkForLocation()
                setTitleBar()

        },
            Response.ErrorListener {
                it?.printStackTrace()
            })
        queue.add(stringRequest)

    }

    private fun checkForLocation(): Int {
        if (map == null) return 0
        map!!.clear()
        if (jsonCrimes.length() > 0) {
            currentCrimeArray = JSONArray(ArrayList<String?>())
            for (i in 0 until jsonCrimes.length()) {
                if (jsonCrimes.getJSONObject(i)
                        .getString("type") == tabTitles[currentIndex]
                ) {
                    currentCrimeArray.put(jsonCrimes.getJSONObject(i))
                }
            }
        } else {
            val markerOptions = MarkerOptions()
            val latlng = LatLng(
                MPreferenceManager.readDoubleInformation(this, "lat"),
                MPreferenceManager.readDoubleInformation(this, "lon")
            )
            markerOptions.position(latlng)
            markerOptions.title("My Location")
            map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13.0f))
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot))
            map!!.addMarker(markerOptions)
            return crimeLevel
        }
        var highColor = 0
        val markerOptions = MarkerOptions()
        val latlng = LatLng(
            MPreferenceManager.readDoubleInformation(this, "lat"),
            MPreferenceManager.readDoubleInformation(this, "lon")
        )
        markerOptions.position(latlng)
        markerOptions.title("My Location")
        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13.0f))
        markerOptions.anchor(0.5f, 0.5f)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.radar))
        map!!.addMarker(markerOptions)
        var ratings: Int
        val distance = ArrayList<String>()
        val dateArray = ArrayList<String>()
        val time = ArrayList<String>()
        val offsetList = doubleArrayOf(0.5, 0.5, 0.5, 1.5, 1.0, 0.2, 0.5, 0.2)
        val offset = offsetList[currentIndex]
        for (i in 0 until currentCrimeArray.length()) {
            val obj = currentCrimeArray.getJSONObject(i)
            val markerOptions1 = MarkerOptions()
            val latlng1 = LatLng(obj.getDouble("lat"), obj.getDouble("lon"))
            markerOptions1.position(latlng1)
            markerOptions1.title("Crime: " + obj.getString("type"))
            markerOptions1.snippet(
                obj.getString("date") + " @ " + obj.getString("address") + "@@@" + obj.getString(
                    "link"
                )
            )
            map!!.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                override fun getInfoWindow(arg0: Marker?): View? {
                    return null
                }

                override fun getInfoContents(marker: Marker): View {
                    if (marker.title != "My Location") {
                        val snippet: Array<String> = marker.snippet.split("@@@").toTypedArray()
                        val myContentView: View = layoutInflater.inflate(
                            R.layout.marker_info, null
                        )
                        val tvTitle = myContentView
                            .findViewById<View>(R.id.title) as TextView
                        tvTitle.text = marker.title
                        val tvSnippet = myContentView
                            .findViewById<View>(R.id.snippet) as TextView
                        tvSnippet.text = snippet[0]
                        return myContentView
                    } else {
                        val myContentView: View = layoutInflater.inflate(
                            R.layout.marker_info, null
                        )
                        val tvTitle = myContentView
                            .findViewById<View>(R.id.title) as TextView
                        tvTitle.text = marker.title
                        val tvSnippet = myContentView
                            .findViewById<View>(R.id.snippet) as TextView
                        tvSnippet.visibility = View.GONE
                        return myContentView
                    }
                }
            })
            map!!.setOnInfoWindowClickListener {
                if (!it.title.equals("My Location")) {
                    val snippet: Array<String> = it.snippet.split("@@@").toTypedArray()
                    ShowProgressDialog.showProgressDialog(this@CrimeRadarActivity, "")

                    val queue = Volley.newRequestQueue(this)
                    val link: String = snippet[1]

                    val stringRequest = StringRequest(Request.Method.GET, link, Response.Listener { response ->
//                        val resultObject = JSONObject(response)
                        ShowProgressDialog.hideProgressDialog()
                        val content: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            fromHtml(
                                StringUtils.substringBetween(
                                    response,
                                    "<dl class=\"dl-horizontal\">",
                                    "</dl>"
                                ).replace("</dd>", "</dd><br>"),
                                Html.FROM_HTML_MODE_COMPACT
                            )
                        } else {
                            fromHtml(
                                StringUtils.substringBetween(
                                    response,
                                    "<dl class=\"dl-horizontal\">",
                                    "</dl>"
                                ).replace("</dd>", "</dd><br>")
                            )
                        }

                        PromptDialog(this@CrimeRadarActivity)
                            .setDialogType(PromptDialog.DIALOG_TYPE_DEFAULT)
                            .setAnimationEnable(true)
                            .setTitleText("Crime Info")
                            .setContentText(content)
                            .setPositiveListener(
                                getString(R.string.dialog_ok)
                            ) { dialog -> dialog.dismiss() }
                            .setPositiveListener(
                                getString(R.string.view_source)
                            ) {
                                val browserIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(snippet[1])
                                )
                                startActivity(browserIntent)
                            }.show()
                    },
                        Response.ErrorListener { error ->
                            Log.d("crime volley", error.localizedMessage!!)
                            ShowProgressDialog.hideProgressDialog()
                        })
                    queue.add(stringRequest)

                }
            }
            markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot))
            map!!.addMarker(markerOptions1)
            val meters: Double = SphericalUtil.computeDistanceBetween(latlng, latlng1)
            when {
                meters >= 800 -> {
                    distance.add("0.0")
                }
                meters >= 600 -> {
                    distance.add("0.2")
                }
                meters >= 400 -> {
                    distance.add("0.4")
                }
                meters >= 200 -> {
                    distance.add("0.6")
                }
                meters >= 100 -> {
                    distance.add("0.8")
                }
                else -> {
                    distance.add("1.0")
                }
            }
            val dateStr = obj.getString("date")
            val weekday = 2
            val weekday2 = 3
            var weekdayResult = 0
            weekdayResult = weekday2 - weekday
            when {
                weekdayResult == 0 -> {
                    dateArray.add("1.0")
                }
                weekdayResult == 1 -> {
                    dateArray.add("0.8")
                }
                weekdayResult == 2 -> {
                    dateArray.add("0.6")
                }
                weekdayResult == 3 -> {
                    dateArray.add("0.4")
                }
                weekdayResult > 3 -> {
                    dateArray.add("0.2")
                }
            }
            val hour = 5
            val hour2 = 7
            var hourDifference = 0
            hourDifference = hour2 - hour
            when {
                hourDifference == 0 -> {
                    time.add("1.0")
                }
                hourDifference < 3 -> {
                    time.add("0.8")
                }
                hourDifference < 6 -> {
                    time.add("0.6")
                }
                hourDifference < 12 -> {
                    time.add("0.4")
                }
                else -> {
                    time.add("0.2")
                }
            }
        }
        var timeAverage = 0f
        for (i in time.indices) {
            val num = time[i].toFloat()
            timeAverage += num
        }
        var dateAverage = 0f
        for (i in dateArray.indices) {
            val num = dateArray[i].toFloat()
            dateAverage += num
        }
        var distanceAverage = 0f
        for (i in distance.indices) {
            val num = distance[i].toFloat()
            distanceAverage += num
        }
        val avg =
            (timeAverage + distanceAverage + dateAverage + offset).toFloat() / 3
        ratings = Math.round(avg)
        if (ratings > 5) {
            ratings = 5
        }
        if (ratings == 0) {
//            if highColor < 0 {
//                highColor = 0;
//            }
        } else if (ratings <= 1) {
            if (highColor < 1) {
                highColor = 1
            }
        } else if (ratings <= 2) {
            if (highColor < 2) {
                highColor = 2
            }
        } else if (ratings <= 3) {
            if (highColor < 3) {
                highColor = 3
            }
        } else if (ratings <= 4) {
            if (highColor < 4) {
                highColor = 4
            }
        } else if (ratings <= 5) {
            highColor = 5
        }

//
//        MarkerOptions markerOptions2 = new MarkerOptions();
//        LatLng latlng2 = new LatLng(MPreferenceManager.readDoubleInformation(this,"lat"), MPreferenceManager.readDoubleInformation(this,"lon"));
//        markerOptions2.position(latlng2);
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng2,13.0f));
//        markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.radar));
//
//        map.addMarker(markerOptions2);
        return highColor
    }

    inner class SectionPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            return Fragment()
        }

        override fun getCount(): Int {
            return tabTitles.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabTitles[position]
        }
    }

    public override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            getDataLocation()
        }
    }
}



