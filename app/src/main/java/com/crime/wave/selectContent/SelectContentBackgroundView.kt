package com.crime.wave.selectContent

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.crime.wave.R

/**
 * Created by Mobile World on 4/8/2020.
 */
class SelectContentBackgroundView : LinearLayout {
    private var gradientPaint: Paint? = null
    private var currentGradient: IntArray = intArrayOf(0)
    private var evaluator: ArgbEvaluator? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private fun initGradient() {
        val centerX = width * 0.5f
        val gradient: Shader = LinearGradient(
            centerX, 0.0f, centerX, height.toFloat(),
            currentGradient, null,
            Shader.TileMode.MIRROR
        )
        gradientPaint!!.shader = gradient
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (currentGradient.size > 1) {
            initGradient()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), gradientPaint!!)
        super.onDraw(canvas)
    }

    fun setForecast(forecast: ContentItem) {
        val weather = forecast.contentLevel
        currentGradient = weatherToGradient(weather)
        if (width != 0 && height != 0) {
            initGradient()
        }
        invalidate()
    }

    fun onScroll(fraction: Float, oldF: ContentItem, newF: ContentItem) {
        currentGradient = mix(
            fraction,
            weatherToGradient(newF.contentLevel),
            weatherToGradient(oldF.contentLevel)
        )
        initGradient()
        invalidate()
    }

    private fun mix(fraction: Float, c1: IntArray, c2: IntArray): IntArray {
        return intArrayOf(
            (evaluator!!.evaluate(fraction, c1[0], c2[0]) as Int),
            (evaluator!!.evaluate(fraction, c1[1], c2[1]) as Int),
            (evaluator!!.evaluate(fraction, c1[2], c2[2]) as Int)
        )
    }

    private fun weatherToGradient(contentLevel: ContentLevel): IntArray {
        return when (contentLevel) {
            ContentLevel.PERIODIC_CLOUDS -> colors(R.array.gradientPeriodicClouds)
            ContentLevel.CLOUDY -> colors(R.array.gradientCloudy)
            ContentLevel.MOSTLY_CLOUDY -> colors(R.array.gradientMostlyCloudy)
            ContentLevel.PARTLY_CLOUDY -> colors(R.array.gradientPartlyCloudy)
            ContentLevel.CLEAR -> colors(R.array.gradientClear)

            ContentLevel.MINIMAL -> colors(R.array.gradientPeriodicClouds)
            ContentLevel.LOW -> colors(R.array.gradientLevelLow)
            ContentLevel.MODERATE -> colors(R.array.gradientLevelModerate)
            ContentLevel.ELEVATED -> colors(R.array.gradientLevelElevated)
            ContentLevel.HIGH -> colors(R.array.gradientLevelHigh)
            ContentLevel.SEVERELY_HIGH -> colors(R.array.gradientLevelSeverelyHigh)
        }
    }

    private fun colors(@ArrayRes res: Int): IntArray {
        return context.resources.getIntArray(res)
    }

    init {
        evaluator = ArgbEvaluator()
        gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        setWillNotDraw(false)
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        View.inflate(context, R.layout.view_select_content, this)
    }
}
