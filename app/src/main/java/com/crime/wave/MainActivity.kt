package com.crime.wave

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
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
import com.android.volley.Request
import com.android.volley.Response
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
import com.crime.wave.wave.WaveWordsFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity(), ItemClickListener,NavigationView.OnNavigationItemSelectedListener {
    private var data: Array<NewsItem> = arrayOf()
    var crimeRadarFragment = CrimeRadarFragment()
    private var waveWordsFragment = WaveWordsFragment()
    private var fineLocationPermissionRequest = 1
    private var tabString = arrayOf(
        "US News",
        "business",
        "entertainment",
//        "general",
        "health",
        "science",
        "sports",
        "technology",
        "politics",
        "world news"
    )
    private var newLayoutType = 0
    private var page = 0
    private var isLoading = false
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

        newsAdapter = NewsCardAdapter(this@MainActivity, data,  this@MainActivity)
        newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = newsAdapter
        }

        if (ActivityCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), fineLocationPermissionRequest)
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
                newsAdapter!!.setData(data,newLayoutType)
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
                newsRecyclerView.adapter =
                    NewsCardAdapter(this@MainActivity, data, this@MainActivity, newLayoutType)
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
                newsAdapter!!.setData(data,newLayoutType)
                newsRecyclerView.adapter =
                    NewsCardAdapter(this@MainActivity, data, this@MainActivity, newLayoutType)
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
            0 -> {
                link = App.instance!!.newsUrl + "page=$p&country=us"
            }
            in 1..6 -> {
                link = App.instance!!.newsUrl + "page=$p&country=us&category=" + tabString[index]
            }
            7 -> {
                link = "http://newsapi.org/v2/everything?page=$p&apiKey=73373c784bd24a679fafc03522618936&language=en&q=" + tabString[index]
            }
            8 -> {
                link = "http://newsapi.org/v2/top-headlines?page=$p&apiKey=73373c784bd24a679fafc03522618936&language=en"
            }
        }

        Log.d("news link", link)
        val stringRequest = StringRequest(Request.Method.GET, link, Response.Listener { response ->
            val resultObject = JSONObject(response)
            val dataArray = resultObject.getJSONArray("articles")
            if (dataArray.length() > 0) {
                for (i in 0 until dataArray.length()) {
                    if (dataArray.getJSONObject(i).getString("title") != "" && dataArray.getJSONObject(i).getString("description") != "null" ) {
                        data += NewsItem(
                            dataArray.getJSONObject(i).getString("title"),
                            dataArray.getJSONObject(i).getString("description"),
                            dataArray.getJSONObject(i).getString("urlToImage"),
                            dataArray.getJSONObject(i).getString("url"),
                            dataArray.getJSONObject(i).getString("publishedAt")
                        )
                        data = data.distinct().toTypedArray()
                    }
                }

                page++
                isLoading = false
//                newsRecyclerView.adapter =
//                    NewsCardAdapter(this@MainActivity, data, this@MainActivity, newLayoutType)
                newsAdapter!!.setData(data,newLayoutType)
                if (data.isNotEmpty()) {
                    emptyCard.visibility = View.GONE
                }
            }
        },
        Response.ErrorListener {
            it?.printStackTrace()
            isLoading = false
        })
        queue.add(stringRequest)
    }
    fun onSelectContent(index:Int) {
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
            R.id.navLocation-> {
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
            R.id.navColorRatingScale -> {
                val i = Intent(this, ColorRatingScaleActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.navPrivacyPolicy-> {
                val i = Intent(this, PdfViewerActivity::class.java)
                i.putExtra("name", "privacypolicy.pdf")
                startActivity(i)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.navTermsOfService-> {
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
