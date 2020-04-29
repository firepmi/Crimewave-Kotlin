package com.crime.wave.utils

import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.crime.wave.R

/**
 * Created by Mobile World on 4/10/2020.
 */
object ShowProgressDialog {
    var pDialog: ProgressDialog? = null
    fun showProgressDialog(context: Context, title: String?) {
        val v: View
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = inflater.inflate(R.layout.activity_load, null)
        pDialog = ProgressDialog(context, R.style.MyTheme)
        pDialog!!.isIndeterminate = true
        pDialog!!.setCancelable(false)
        pDialog!!.show()
        pDialog!!.setContentView(v)
    }

    fun hideProgressDialog() {
        if (pDialog == null) return
        if (pDialog!!.isShowing) pDialog!!.dismiss()
    }
}