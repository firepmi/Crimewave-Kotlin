package com.crime.wave.crimeRadar

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.refactor.lib.colordialog.PromptDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.crime.wave.App
import com.crime.wave.MainActivity
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
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.activity_crime_radar.*
import kotlinx.android.synthetic.main.fragment_crime_radar.view.*
import org.apache.commons.lang3.StringUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by Mobile World on 4/12/2020.
 */
class CrimeRadarFragment : Fragment() {
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
    private var mapTypes = intArrayOf(
        GoogleMap.MAP_TYPE_NORMAL,
        GoogleMap.MAP_TYPE_SATELLITE,
        GoogleMap.MAP_TYPE_TERRAIN,
        GoogleMap.MAP_TYPE_HYBRID
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_crime_radar, container, false)
        activity?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(
                mMessageReceiver,
                IntentFilter("location_changed")
            )
        }

        view.mapView.onCreate(savedInstanceState)
        view.mapView.onResume()

        MapsInitializer.initialize(activity)

        view.mapView.getMapAsync { mMap ->
            // For showing a move to my location button
            mMap.isMyLocationEnabled = false
            val markerOptions = MarkerOptions()
            val latLng = LatLng(
                MPreferenceManager.readDoubleInformation(activity, "lat"),
                MPreferenceManager.readDoubleInformation(activity, "lon")
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
        view.ivFilter.setOnClickListener {
            mapType++
            map?.mapType = mapTypes[mapType % mapTypes.size]
        }
        view.ivFullScreen.setOnClickListener {
//            activity?.startActivity(Intent(activity, CrimeRadarActivity::class.java))
            MainActivity.instance!!.onMapFullScreen()
        }
        return view
    }
    @SuppressLint("SetTextI18n")
    fun selectedCategory(position:Int){
        if (tvTitle == null) return
        tvTitle.text = "${App.instance!!.levelTexts[App.instance!!.crimeLevel]} ${tabTitles[position]} Area"
        currentIndex = position
        try {
            crimeLevel = checkForLocation()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        setTitleBar()
    }
    @SuppressLint("SetTextI18n")
    private fun setTitleBar() {
        if (tvTitle == null) return
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(App.instance!!.titleColors[crimeLevel]), Color.TRANSPARENT)
        )
        gd.cornerRadius = 0f
        mainLayout?.background = gd
        tvTitle?.text = "${App.instance!!.levelTexts[crimeLevel]} ${tabTitles[currentIndex]} Area"
        tvTitle.typeface = App.instance!!.nexaBoldFont

        if (currentIndex == 7) {
            tvTitle.text = "${App.instance!!.levelTexts[crimeLevel]} Misc Crime Area"
        }
        if (crimeLevel == 2) {
            tvTitle?.setTextColor(Color.BLACK)
        } else {
            tvTitle?.setTextColor(Color.WHITE)
        }
    }
    fun getDataLocation() {
        if(activity == null) return
        val url =
            "http://api.spotcrime.com/crimes.json?lat=" + MPreferenceManager.readDoubleInformation(
                activity,
                "lat"
            ).toString() + "&lon=" + MPreferenceManager.readDoubleInformation(activity, "lon")
                .toString() + "&radius=0.01&key=" + App.instance!!.key + "&callback="

        val queue = Volley.newRequestQueue(activity)

        val stringRequest = StringRequest(
            Request.Method.GET, url, Response.Listener { response ->

                jsonCrimes = if (response == null) {
                    JSONArray(ArrayList<String?>())
                } else {
                    val res = JSONObject(response)
                    res.getJSONArray("crimes")
                }
                crimeLevel = checkForLocation()
                App.instance!!.crimeLevel = crimeLevel
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
                MPreferenceManager.readDoubleInformation(activity, "lat"),
                MPreferenceManager.readDoubleInformation(activity, "lon")
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
            MPreferenceManager.readDoubleInformation(activity, "lat"),
            MPreferenceManager.readDoubleInformation(activity, "lon")
        )
        markerOptions.position(latlng)
        markerOptions.title("My Location")
        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13.0f))
        markerOptions.anchor(0.5f, 0.5f)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.radar))
        map?.addMarker(markerOptions)
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

                @SuppressLint("InflateParams")
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
                    activity?.let { it1 -> ShowProgressDialog.showProgressDialog(it1, "") }

                    val queue = Volley.newRequestQueue(activity)
                    val link: String = snippet[1]

                    val stringRequest = StringRequest(
                        Request.Method.GET, link, Response.Listener { response ->
//                        val resultObject = JSONObject(response)
                            ShowProgressDialog.hideProgressDialog()
                            val content: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(
                                    StringUtils.substringBetween(
                                        response,
                                        "<dl class=\"dl-horizontal\">",
                                        "</dl>"
                                    ).replace("</dd>", "</dd><br>"),
                                    Html.FROM_HTML_MODE_COMPACT
                                )
                            } else {
                                Html.fromHtml(
                                    StringUtils.substringBetween(
                                        response,
                                        "<dl class=\"dl-horizontal\">",
                                        "</dl>"
                                    ).replace("</dd>", "</dd><br>")
                                )
                            }

                            PromptDialog(activity)
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

            val weekday = 2
            val weekday2 = 3
            var weekdayResult: Int
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
            var hourDifference: Int
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
        var avg =
            (timeAverage + distanceAverage + dateAverage + offset).toFloat() / 3
        if (timeAverage == 0f && distanceAverage == 0f && dateAverage == 0f ) {
            avg = 0f
        }
        ratings = avg.roundToInt()
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

        return highColor
    }


    override fun onResume() {
        view?.mapView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        view?.mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.let { LocalBroadcastManager.getInstance(it).unregisterReceiver(mMessageReceiver) }
        view?.mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        view?.mapView?.onLowMemory()
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