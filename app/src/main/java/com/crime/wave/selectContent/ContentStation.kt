package com.crime.wave.selectContent

import com.crime.wave.R

/**
 * Created by Mobile World on 4/8/2020.
 */

class ContentStation private constructor() {
    fun getContents(): List<ContentItem> {
        return listOf(
            ContentItem("Robbery", R.drawable.robbery, ContentLevel.MINIMAL),
            ContentItem("Burglary", R.drawable.burglary, ContentLevel.LOW),
            ContentItem("Theft", R.drawable.thief, ContentLevel.MODERATE),
            ContentItem("Shooting", R.drawable.shooting, ContentLevel.ELEVATED),
            ContentItem("Shooting History", R.drawable.icon_shooting_history, ContentLevel.PARTLY_CLOUDY),
            ContentItem("Assault", R.drawable.assault, ContentLevel.HIGH),
            ContentItem("Vandalism", R.drawable.vandalism, ContentLevel.SEVERELY_HIGH),
            ContentItem("Arrest", R.drawable.arrest, ContentLevel.MOSTLY_CLOUDY),
            ContentItem("Misc", R.drawable.icon_misc, ContentLevel.CLOUDY),
            ContentItem("Wave", R.drawable.icon_wave, ContentLevel.PERIODIC_CLOUDS)
        )
    }

    companion object {
        fun get(): ContentStation {
            return ContentStation()
        }
    }
}
