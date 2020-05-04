package com.crime.wave

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
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
import com.crime.wave.crimeRadar.ShootingHistoryFragment
import com.crime.wave.menu.LocationActivity
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
    private var crimeRadarFragment = CrimeRadarFragment()
    private var waveWordsFragment = WaveWordsFragment()
    private var fineLocationPermissionRequest = 1
    private var tabString = arrayOf(
        "US News",
        "business",
        "entertainment",
        "general",
        "health",
        "science",
        "sports",
        "technology"
    )
    private var newLayoutType = 0
    companion object {
        var instance: MainActivity? = null
            private set
    }
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

        newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = NewsCardAdapter(this@MainActivity, data,  this@MainActivity)
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
        getNews(0)

        ivFullScreen.setOnClickListener {onFullScreenClicked()}
        ivSettings.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.END);
        }

        nav_view.setNavigationItemSelectedListener(this)
    }
    private fun onFullScreenClicked(){
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
                newsRecyclerView.adapter =
                    NewsCardAdapter(this@MainActivity, data, this@MainActivity, newLayoutType)
            }
        }
    }
    fun onShootingMapFullScreen(){
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
            selectContentLayout.visibility = View.GONE
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                guidelineMain.setGuidelinePercent(0f)
                guidelineMap.setGuidelinePercent(0f)
            }
        }
    }
    fun onSwipeTop(): Boolean {
        Log.d("Main", "Swipe Top")
        return if(llMap?.visibility != View.GONE)
            false
        else {
            onFullScreenClicked()
            true
        }
    }

    fun onSwipeBottom(): Boolean {
        Log.d("Main", "Swipe Bottom")
        return if(llMap?.visibility == View.GONE)
            false
        else {
            onFullScreenClicked()
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

    private fun getNews(index: Int) {

        val queue = Volley.newRequestQueue(this)
        var link: String = App.instance!!.newsUrl + "page=1&country=us"

//        if(index == tabString.size - 1 ) {
//            val i = Intent(this, NewsActivity::class.java)
//            startActivity(i)
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//            return
//        }
//        else
        if(index != 0) {
            link = App.instance!!.newsUrl + "country=us&category=" + tabString[index]
        }

        val stringRequest = StringRequest(Request.Method.GET, link, Response.Listener { response ->
            val resultObject = JSONObject(response)
            val dataArray = resultObject.getJSONArray("articles")
            if (dataArray.length() > 0) {
                data = arrayOf()
                for (i in 0 until dataArray.length()) {
                    data += NewsItem(
                        dataArray.getJSONObject(i).getString("title"),
                        dataArray.getJSONObject(i).getString("description"),
                        dataArray.getJSONObject(i).getString("urlToImage"),
                        dataArray.getJSONObject(i).getString("url"),
                        dataArray.getJSONObject(i).getString("publishedAt")
                    )
                }

                newsRecyclerView.adapter =
                    NewsCardAdapter(this@MainActivity, data, this@MainActivity, newLayoutType)
                if (data.isNotEmpty()) {
                    emptyCard.visibility = View.GONE
                }
            }
        },
        Response.ErrorListener {
            it?.printStackTrace()
        })
        queue.add(stringRequest)
    }
    fun onSelectContent(index:Int) {
        when {
            index == 4 -> {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.contentLayout,
                        ShootingHistoryFragment()).commitAllowingStateLoss()
                supportFragmentManager.executePendingTransactions()
            }
            index <= 8 -> {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.contentLayout, crimeRadarFragment).commitAllowingStateLoss()
                supportFragmentManager.executePendingTransactions()
                if (index < 4 ) crimeRadarFragment.selectedCategory(index)
                else crimeRadarFragment.selectedCategory(index-1)
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
            R.id.navColorSystem-> {

            }
            R.id.navTermsOfService-> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.END)
        return true
    }
}
