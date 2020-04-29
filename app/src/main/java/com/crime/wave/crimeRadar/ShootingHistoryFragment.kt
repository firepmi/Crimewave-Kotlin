package com.crime.wave.crimeRadar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
        view.webView.settings.pluginState = WebSettings.PluginState.ON
        view.webView.webViewClient = CustomWebViewClient()
        view.webView.webChromeClient = WebChromeClient()
        view.webView.loadUrl(App.instance!!.gunMapUrl)

        view.ivFullScreen.setOnClickListener {
            MainActivity.instance!!.onShootingMapFullScreen()
        }
        return view
    }
    private class CustomWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            return false
        }

    }
}