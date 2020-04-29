package com.crime.wave.news

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.crime.wave.MainActivity


/**
 * Created by Mobile World on 4/24/2020.
 */
class OnSwipeTouchListener : OnTouchListener {
    private val gestureDetector =
        GestureDetector(MainActivity.instance, GestureListener())

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}