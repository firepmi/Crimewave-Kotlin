package com.crime.wave.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crime.wave.App
import com.crime.wave.R
import kotlinx.android.synthetic.main.activity_color_rating_scale.*
import kotlinx.android.synthetic.main.activity_pdf_viewer.*
import kotlinx.android.synthetic.main.activity_pdf_viewer.btnBack

class ColorRatingScaleActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_rating_scale)

        btnBack.setOnClickListener {finish()}

        tvTitle.typeface = App.instance!!.nexaBoldFont

        txtBold1.typeface = App.instance!!.nexaBoldFont
        txtBold2.typeface = App.instance!!.nexaBoldFont
        txtBold3.typeface = App.instance!!.nexaBoldFont
        txtBold4.typeface = App.instance!!.nexaBoldFont
        txtBold5.typeface = App.instance!!.nexaBoldFont
        txtBold6.typeface = App.instance!!.nexaBoldFont
        txtBold66.typeface = App.instance!!.nexaBoldFont

        txtNormal1.typeface = App.instance!!.nexaLightFont
        txtNormal2.typeface = App.instance!!.nexaLightFont
        txtNormal3.typeface = App.instance!!.nexaLightFont
        txtNormal33.typeface = App.instance!!.nexaLightFont
        txtNormal4.typeface = App.instance!!.nexaLightFont
        txtNormal44.typeface = App.instance!!.nexaLightFont
        txtNormal5.typeface = App.instance!!.nexaLightFont
        txtNormal6.typeface = App.instance!!.nexaLightFont
        txtNormal66.typeface = App.instance!!.nexaLightFont
    }

}
