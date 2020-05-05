package com.crime.wave.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crime.wave.R
import kotlinx.android.synthetic.main.activity_pdf_viewer.*

class PdfViewerActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        btnBack.setOnClickListener {finish()}

        val name = intent.getStringExtra("name")

        pdfView.fromAsset(name).load()
    }

}
