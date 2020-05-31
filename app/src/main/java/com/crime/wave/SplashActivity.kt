package com.crime.wave

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    private val handler = Handler()
    private val r: Runnable = Runnable {
        this@SplashActivity.startActivity(
            Intent(
                this@SplashActivity,
                MainActivity::class.java
            )
        )
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        img_bg.setOnClickListener {
            this@SplashActivity.startActivity(
                Intent(
                    this@SplashActivity,
                    MainActivity::class.java
                )
            )
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }

        txtLogo.typeface = App.instance!!.nexaBoldFont
        handler.postDelayed(r, 3000)

        MobileAds.initialize(this) {}
    }
}
