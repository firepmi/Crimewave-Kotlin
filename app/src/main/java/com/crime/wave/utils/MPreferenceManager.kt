package com.crime.wave.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.crime.wave.App

/**
 * Created by Mobile World on 4/10/2020.
 */
class MPreferenceManager {
    val TOKEN = "token"
    val USER_ID = "id"
    val USER_FIRSTNAME = "firstname"
    val USER_LASTNAME = "lastname"
    val USER_EMAIL = "email"
    val USER_AVATAR = "avatar"
    val USER_MOBILE = "mobile"
    val IS_USER_LOGIN = "USER_LOGGEDIN"
    val TEMP_EVENT = "TEMP_EVENT_ID"


    val IS_STAFF_LOGIN = "STAFF_LOGGEDIN"
    val STAFF_ID = "STAFF_ID"
    val STAFF_FIRSTNAME = "STAFF_FIRSTNAME"
    val STAFF_LASTNAME = "STAFF_LASTNAME"
    val STAFF_AVATAR = "STAFF_AVATAR"
    val STAFF_EMAIL = "STAFF_EMAIL"
    val STAFF_PHONE = "STAFF_PHONE"


    val NEWS_NOTIFICATION = "news_notification"
    val LOCATION_NOTIFICATION = "location_notification"

    companion object {

        fun saveStringInformation(
            context: Context?,
            key: String?,
            value: String?
        ) {
            val sp =
                PreferenceManager.getDefaultSharedPreferences(context)
            val ed = sp.edit()
            ed.putString(key, value)
            ed.apply()
        }

        fun readStringInformation(
            context: Context?,
            key: String?
        ): String? {
            val sp =
                PreferenceManager.getDefaultSharedPreferences(context)
            return sp.getString(key, "")
        }

        fun readDoubleInformation(
            context: Context?,
            key: String?
        ): Double {
            if (context == null) {
                return 0.0;
            }
            ////TODO for test

            var k = key
            if(App.instance!!.isCustomLocation) {
                k = "custom$key"
            }
//            if (key == "lat") {
//                return 36.121439
//            }
//            else if(key == "lon") {
//                return -115.150098
//            }
            val sp =
                PreferenceManager.getDefaultSharedPreferences(context)
            return sp.getFloat(k, 0f).toDouble()
        }
        fun readLocationInformation(
            context: Context?,
            key: String?
        ): Double {
            if (context == null) {
                return 0.0;
            }
            val sp =
                PreferenceManager.getDefaultSharedPreferences(context)
            return sp.getFloat(key, 0f).toDouble()
        }
        fun saveBoolInformation(
            context: Context?,
            key: String?,
            value: Boolean
        ) {
            val sp =
                PreferenceManager.getDefaultSharedPreferences(context)
            val ed = sp.edit()
            ed.putBoolean(key, value)
            ed.apply()
        }

        fun saveDoubleInformation(
            context: Context?,
            key: String?,
            value: Float
        ) {
            val sp =
                PreferenceManager.getDefaultSharedPreferences(context)
            val ed = sp.edit()
            ed.putFloat(key, value)
            ed.apply()
        }

        fun readBoolInformation(
            context: Context?,
            key: String?
        ): Boolean {
            val sp =
                PreferenceManager.getDefaultSharedPreferences(context)
            return sp.getBoolean(key, false)
        }

        fun saveIntInformation(
            context: Context?,
            key: String?,
            value: Int
        ) {
            val sp =
                PreferenceManager.getDefaultSharedPreferences(context)
            val ed = sp.edit()
            ed.putInt(key, value)
            ed.apply()
        }

        fun readIntInformation(context: Context?, key: String?): Int {
            val sp =
                PreferenceManager.getDefaultSharedPreferences(context)
            return sp.getInt(key, 0)
        }
    }
}
