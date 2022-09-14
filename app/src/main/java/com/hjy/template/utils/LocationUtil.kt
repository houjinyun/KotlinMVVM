package com.hjy.template.utils

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.os.Build

object LocationUtil {

    fun isGpsProviderEnabled(activity: Activity): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!locationManager.isLocationEnabled) {
                return false
            }
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

}