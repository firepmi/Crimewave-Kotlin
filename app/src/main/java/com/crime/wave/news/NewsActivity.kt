package com.crime.wave.news

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.crime.wave.App
import com.crime.wave.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_news.*

/**
 * Created by Mobile World on 4/17/2020.
 */
class NewsActivity : AppCompatActivity() {
    var tabString = arrayOf(
        "US News",
        "business",
        "entertainment",
        "general",
        "health",
        "science",
        "sports",
        "technology"
    )
//    var adView: AdView? = null
//
//    fun doUpdateUI() {
//        toolbar_top!!.setBackgroundColor(
//            Color.parseColor(
//                Global.titleColors.get(
//                    Global.crime_level
//                )
//            )
//        )
//        if (Global.crime_level === 2) {
//            tvTitle!!.setTextColor(Color.BLACK)
//        } else {
//            tvTitle!!.setTextColor(Color.WHITE)
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val window: Window = getWindow()
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.statusBarColor = Color.parseColor(Global.titleColors.get(Global.crime_level))
//        }
//        tabLayout!!.setBackgroundColor(Color.parseColor(Global.titleColors.get(Global.crime_level)))
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

//        adView = AdView(this)
//        adView.setLayoutParams(params)
//        adView.setAdSize(AdSize.BANNER)
//        adView.setAdUnitId(resources.getString(R.string.banner_ads))
//        val adRequest: AdRequest = Builder().build()
//        adView.loadAd(adRequest)
//        rootView!!.addView(adView)
        viewPager!!.adapter = SectionPagerAdapter(supportFragmentManager)
        tabLayout!!.setupWithViewPager(viewPager)
        tabLayout!!.tabMode = TabLayout.MODE_SCROLLABLE
        toolbar_top!!.setBackgroundColor(
            Color.parseColor(
                App.instance?.titleColors!![App.instance!!.crimeLevel]
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor(App.instance!!.titleColors[App.instance!!.crimeLevel])
        }
        tabLayout!!.setBackgroundColor(Color.parseColor(App.instance!!.titleColors[App.instance!!.crimeLevel]))
        if (App.instance!!.crimeLevel == 2) {
            tvTitle!!.setTextColor(Color.BLACK)
            tabLayout!!.setTabTextColors(
                Color.parseColor("#666666"),
                Color.BLACK
            )
        } else {
            tvTitle!!.setTextColor(Color.WHITE)
            tabLayout!!.setTabTextColors(
                Color.parseColor("#CCCCCC"),
                Color.WHITE
            )
        }

        ivBack.setOnClickListener {
            finish()
        }
    }

    inner class SectionPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            return NewsFragment(position)
        }

        override fun getCount(): Int {
            return tabString.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabString[position]
        }
    }

}
