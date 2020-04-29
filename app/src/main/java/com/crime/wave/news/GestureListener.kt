package com.crime.wave.news

import android.view.GestureDetector
import android.view.MotionEvent
import com.crime.wave.MainActivity
import kotlin.math.abs

/**
 * Created by Mobile World on 4/24/2020.
 */
class GestureListener : GestureDetector.SimpleOnGestureListener() {
    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        var result = false
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (Math.abs(diffX) < Math.abs(diffY)) {
                if (abs(diffY) > SWIPE_THRESHOLD && abs(
                        velocityY
                    ) > SWIPE_VELOCITY_THRESHOLD
                ) {
                    result = if (diffY > 0) {
                        MainActivity.instance!!.onSwipeBottom()
                    } else {
                        MainActivity.instance!!.onSwipeTop()
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return result
    }

    companion object {
        private const val SWIPE_THRESHOLD = 50
        private const val SWIPE_VELOCITY_THRESHOLD = 50
    }
}