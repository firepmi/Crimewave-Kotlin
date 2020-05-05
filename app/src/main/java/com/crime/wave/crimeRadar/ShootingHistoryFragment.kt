package com.crime.wave.crimeRadar

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.crime.wave.App
import com.crime.wave.MainActivity
import com.crime.wave.R
import kotlinx.android.synthetic.main.fragment_gun_map.view.*


/**
 * Created by Mobile World on 4/8/2020.
 */
class ShootingHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_gun_map, container, false)

        view.webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        view.webView.settings.javaScriptEnabled = true
        view.webView.settings.loadWithOverviewMode = true
        view.webView.settings.useWideViewPort = true
        view.webView.settings.builtInZoomControls = true
        view.webView.settings.domStorageEnabled = true
        view.webView.settings.allowContentAccess = true
        view.webView.settings.allowUniversalAccessFromFileURLs = true
        view.webView.settings.allowFileAccess = true
        view.webView.settings.allowFileAccessFromFileURLs = true
        view.webView.settings.databaseEnabled = true
        view.webView.settings.blockNetworkLoads = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.webView.settings.safeBrowsingEnabled = false
        }
        view.webView.settings.setAppCacheEnabled(true)
        view.webView.settings.setGeolocationEnabled(true)
        view.webView.settings.loadsImagesAutomatically = true
        view.webView.settings.userAgentString = "Android WebView"

        view.webView.settings.pluginState = WebSettings.PluginState.ON
        view.webView.webViewClient = CustomWebViewClient()
        view.webView.webChromeClient = WebChromeClient()
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(view.webView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
        WebView.setWebContentsDebuggingEnabled(true)

        view.webView.loadUrl(App.instance!!.gunMapUrl)

        view.ivFullScreen.setOnClickListener {
            MainActivity.instance!!.onMapFullScreen()
        }
        return view
    }
    private class CustomWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            view.loadUrl(url)
            return true
        }
    }
}