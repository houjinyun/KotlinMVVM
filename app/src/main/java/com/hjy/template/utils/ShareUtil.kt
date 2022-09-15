package com.hjy.template.utils

import android.app.Activity
import android.content.Intent

object ShareUtil {

    fun shareUrl(activity: Activity, text: String, title: String) {
        val intent = Intent()
        intent.run {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        activity.startActivity(Intent.createChooser(intent, title))
    }

}