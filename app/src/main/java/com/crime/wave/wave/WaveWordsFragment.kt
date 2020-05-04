package com.crime.wave.wave

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.crime.wave.App
import com.crime.wave.R
import kotlinx.android.synthetic.main.fragment_wave.*
import kotlinx.android.synthetic.main.fragment_wave.view.*


/**
 * Created by Mobile World on 4/8/2020.
 */
class WaveWordsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_wave, container, false)

        var video = 0
        when (App.instance!!.crimeLevel) {
            0 -> video = R.raw.blue
            1 -> video = R.raw.green
            2 -> video = R.raw.yellow
            3 -> video = R.raw.orange
            4 -> video = R.raw.red
            5 -> video = R.raw.dark_red
        }
        view.videoView.setVideoURI(Uri.parse("android.resource://"+ requireActivity().packageName+"/"+video))
        view.videoView.setOnPreparedListener { mp ->
            videoView.start()
            mp!!.isLooping = true
        }
        view.txtWords.setOnClickListener {
            makeNewWord(view)
        }

        makeNewWord(view)

        return view
    }

    private fun makeNewWord(v:View){
        val fadeIn  = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator() //add this
        fadeIn.duration = 500

//        val fadeOut = AlphaAnimation(1f, 0f)
//        fadeOut.interpolator = AccelerateInterpolator() //and this
//        fadeOut.startOffset = 500
//        fadeOut.duration = 500

        val animation = AnimationSet(false) //change to false
        animation.addAnimation(fadeIn)
//        animation.addAnimation(fadeOut)

        v.txtWords.startAnimation(animation)
        if(App.instance!!.crimeLevel < 3)
            v.txtWords.setText(App.instance!!.niceQueue[(0 until App.instance!!.niceQueue.size).random()])
        else v.txtWords.setText(App.instance!!.deepQueue[(0 until App.instance!!.deepQueue.size).random()])
        v.txtWords.typeface = App.instance!!.nexaBoldFont
        v.txtWords.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
    }

}