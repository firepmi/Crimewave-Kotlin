package com.crime.wave

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.crime.wave.crimeRadar.CrimeRadarFragment
import com.crime.wave.menu.ColorRatingScaleActivity
import com.crime.wave.menu.LocationActivity
import com.crime.wave.menu.PdfViewerActivity
import com.crime.wave.news.NewsCardAdapter
import com.crime.wave.news.NewsCardAdapter.ItemClickListener
import com.crime.wave.news.NewsItem
import com.crime.wave.selectContent.SelectContentFragment
import com.crime.wave.utils.LocationUpdaterService
import com.crime.wave.utils.MPreferenceManager
import com.crime.wave.wave.WaveWordsFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main.*
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder
import java.util.*

class MainActivity : AppCompatActivity(), ItemClickListener,NavigationView.OnNavigationItemSelectedListener {
    private var data: Array<NewsItem> = arrayOf()
    var crimeRadarFragment = CrimeRadarFragment()
    private var waveWordsFragment = WaveWordsFragment()
    private var fineLocationPermissionRequest = 1
    private var tabString = arrayOf(
        "custom news",
        "US News",
        "business",
        "entertainment",
//        "general",
        "health",
        "science",
        "sports",
        "technology",
        "politics"
//        "world news"
    )
    private var newLayoutType = 0
    private var page = 0
    private var isLoading = false
    private var cityName = ""
    companion object {
        var instance: MainActivity? = null
            private set
    }
    var newsAdapter: NewsCardAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        instance = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        val selectFragment = SelectContentFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.selectContentLayout, selectFragment).commitAllowingStateLoss()

        supportFragmentManager.beginTransaction()
            .replace(R.id.contentLayout, crimeRadarFragment).commitAllowingStateLoss()

        newsAdapter = NewsCardAdapter(this@MainActivity, data, this@MainActivity)
        newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = newsAdapter
        }
        if (data.isNotEmpty()) {
            emptyCard.visibility = View.GONE
        }
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), fineLocationPermissionRequest
            )
        }
        else {
            val intent = Intent(this, LocationUpdaterService::class.java)
            startService(intent)
        }

        for (tab in tabString) {
            tabLayout.addTab(tabLayout.newTab().setText(tab))
        }

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                emptyCard.visibility = View.VISIBLE
                page = 0
                data = arrayOf()
                isLoading = false
                newsAdapter!!.setData(data, newLayoutType)
                getNews(tab.position)
            }
        })
        val vg = tabLayout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildCount = vgTab.childCount
            for (i in 0 until tabChildCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.typeface = App.instance?.nexaBoldFont
                }
            }
        }
        txtLoading.typeface = App.instance!!.nexaBoldFont
        page = 0
        data = arrayOf()
        isLoading = false
        getNews(0)

        ivFullScreen.setOnClickListener {onNewsFullScreenClicked()}
        ivSettings.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.END);
        }

        nav_view.setNavigationItemSelectedListener(this)

    }

    private fun onNewsFullScreenClicked(){
        if(llMap?.visibility == View.GONE) {
            llMap?.visibility = View.VISIBLE

            newLayoutType = 0
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // In landscape
                guidelineMain.setGuidelinePercent(0.5f)
            } else {
                var params = rlNews.layoutParams
                val scale = resources.displayMetrics.density
                params.height = (250 * scale).toInt()
                rlNews.layoutParams = params
                newsRecyclerView.layoutManager = LinearLayoutManager(
                    this@MainActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
//                newsAdapter!!.setData(data,newLayoutType)
                newsAdapter = NewsCardAdapter(
                    this@MainActivity,
                    data,
                    this@MainActivity,
                    newLayoutType
                )
                newsRecyclerView.adapter = newsAdapter
            }
        }
        else {
            llMap?.visibility = View.GONE
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // In landscape
                guidelineMain.setGuidelinePercent(1f)
            } else {
                // In portrait
                rlNews.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
                newLayoutType = 1
                newsRecyclerView.layoutManager = LinearLayoutManager(
                    this@MainActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                newsAdapter!!.setData(data, newLayoutType)
                newsAdapter = NewsCardAdapter(
                    this@MainActivity,
                    data,
                    this@MainActivity,
                    newLayoutType
                )
                newsRecyclerView.adapter = newsAdapter
                if (data.isNotEmpty()) {
                    emptyCard.visibility = View.GONE
                }
            }
        }
    }
    fun onMapFullScreen(){
        if(rlNews.visibility == View.GONE){
            rlNews.visibility = View.VISIBLE
            selectContentLayout.visibility = View.VISIBLE
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                guidelineMain.setGuidelinePercent(0.5f)
                guidelineMap.setGuidelinePercent(0.4f)
            }
        }
        else {
            rlNews.visibility = View.GONE
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                guidelineMain.setGuidelinePercent(0f)
                guidelineMap.setGuidelinePercent(0f)
                selectContentLayout.visibility = View.GONE
            }
        }
    }
    fun onSwipeTop(): Boolean {
        Log.d("Main", "Swipe Top")
        return if(llMap?.visibility != View.GONE)
            false
        else {
            onNewsFullScreenClicked()
            true
        }
    }

    fun onSwipeBottom(): Boolean {
        Log.d("Main", "Swipe Bottom")
        return if(llMap?.visibility == View.GONE)
            false
        else {
            onNewsFullScreenClicked()
            true
        }
    }

    fun onUpdateLocation(){
        val latitude = MPreferenceManager.readDoubleInformation(this, "lat")
        val longitude = MPreferenceManager.readDoubleInformation(this, "lon")
        if(latitude == 0.0 || longitude == 0.0) {
            return
        }
        else {
            try {
                val geoCoder = Geocoder(this, Locale.getDefault())
                val addresses =
                    geoCoder.getFromLocation(latitude, longitude, 1)
                cityName = addresses[0].locality
                tabLayout.getTabAt(0)?.text = "$cityName News"
            }
            catch (e: IOException) {
                Log.e("city name", "Service not Available")
            }
        }
    }
    override fun onItemClick(position: Int) { //select news item
        Log.d("new click", "position $position")
        val i = Intent(this, WebViewActivity::class.java)
        i.putExtra("webLink", data[position].url)
        startActivity(i)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onLoadMore() {
        getNews(tabLayout.selectedTabPosition)
    }

    private fun getNews(index: Int) {
        if (isLoading) return
        isLoading = true
        var p = page + 1
        val queue = Volley.newRequestQueue(this)
        var link: String = App.instance!!.newsUrl + "page=$p&country=us"
        when (index) {
            0 -> { //Local News
                if (cityName != "") {
                    tabLayout.getTabAt(0)?.text = "$cityName News"
                    val cityParam: String = URLEncoder.encode(cityName, "utf-8")
                    link =
                        "https://newsapi.org/v2/everything?page=$p&apiKey=73373c784bd24a679fafc03522618936&q=$cityParam"
                }
            }
            1 -> { //US News
                link = App.instance!!.newsUrl + "page=$p&country=us"
            }
            in 2..7 -> {
                link = App.instance!!.newsUrl + "page=$p&country=us&category=" + tabString[index]
            }
            7 -> {  //Politics
                link =
                    "https://newsapi.org/v2/everything?page=$p&apiKey=73373c784bd24a679fafc03522618936&language=en&q=" + tabString[index]
            }
            8 -> {
                link =
                    "https://newsapi.org/v2/top-headlines?page=$p&apiKey=73373c784bd24a679fafc03522618936&language=en"
            }
        }

        Log.d("news link", link)
        val stringRequest =  object : StringRequest(
            Method.GET, link, { response ->
                val resultObject = JSONObject(response)
                val dataArray = resultObject.getJSONArray("articles")
                if (dataArray.length() > 0) {
                    var refresh = false
                    if (data.isEmpty()) {
                        refresh = true
                    }
                    for (i in 0 until dataArray.length()) {
                        if (dataArray.getJSONObject(i)
                                .getString("title") != "" && dataArray.getJSONObject(
                                i
                            ).getString("description") != "null"
                        ) {
                            val itemTitle = dataArray.getJSONObject(i).getString("title")
                            val itemDescription =
                                dataArray.getJSONObject(i).getString("description")
                            val itemImage = dataArray.getJSONObject(i).getString("urlToImage")
                            val itemUrl = dataArray.getJSONObject(i).getString("url")
                            var ok = true
                            for (dt in data) {
                                if (dt.title == itemTitle || dt.description == itemDescription || dt.image == itemImage || dt.url == itemUrl) {
                                    ok = false
                                    break
                                }
                            }
                            if (ok) {
                                data += NewsItem(
                                    dataArray.getJSONObject(i).getString("title"),
                                    dataArray.getJSONObject(i).getString("description"),
                                    dataArray.getJSONObject(i).getString("urlToImage"),
                                    dataArray.getJSONObject(i).getString("url"),
                                    dataArray.getJSONObject(i).getString("publishedAt")
                                )
                            }
                        }
                    }

                    page++
                    isLoading = false
                    if (refresh) {
                        newsAdapter = NewsCardAdapter(
                            this@MainActivity,
                            data,
                            this@MainActivity,
                            newLayoutType
                        )
                        newsRecyclerView.adapter = newsAdapter
                    } else {
                        newsAdapter!!.setData(data, newLayoutType)
                    }
                    if (data.isNotEmpty()) {
                        emptyCard.visibility = View.GONE
                    }
                }
            },
            {
                it?.printStackTrace()
                isLoading = false
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "PostmanRuntime/7.26.8"
                return headers
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        queue.add(stringRequest)
    }
    fun onSelectContent(index: Int) {
        when {
            index < 8 -> {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.contentLayout, crimeRadarFragment).commitAllowingStateLoss()
                supportFragmentManager.executePendingTransactions()
                crimeRadarFragment.selectedCategory(index)
            }
            else -> {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.contentLayout, waveWordsFragment).commitAllowingStateLoss()
                supportFragmentManager.executePendingTransactions()
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == fineLocationPermissionRequest) {
            startService(Intent(this, LocationUpdaterService::class.java))
            crimeRadarFragment.getDataLocation()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navLocation -> {
                val i = Intent(this, LocationActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.navShootingHistory -> {
                val urlString = App.instance!!.gunMapUrl
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage("com.android.chrome")
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    // Chrome browser presumably not installed and open Kindle Browser
                    intent.setPackage(null)
                    startActivity(intent)
                }
            }
            R.id.navStore -> {
                val urlString = App.instance!!.storeUrl
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage("com.android.chrome")
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    // Chrome browser presumably not installed and open Kindle Browser
                    intent.setPackage(null)
                    startActivity(intent)
                }
            }
            R.id.navColorRatingScale -> {
                val i = Intent(this, ColorRatingScaleActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.navPrivacyPolicy -> {
                val i = Intent(this, PdfViewerActivity::class.java)
                i.putExtra("name", "privacypolicy.pdf")
                startActivity(i)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.navTermsOfService -> {
                val i = Intent(this, PdfViewerActivity::class.java)
                i.putExtra("name", "termsofservice.pdf")
                startActivity(i)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.END)
        return true
    }
}
