package com.hjy.template.utils

import android.content.Context
import android.widget.Toast

/**
 * Toast 相关扩展方法
 */

fun Context.toastShort(msg: CharSequence) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.toastShort(msgResId: Int) {
    Toast.makeText(this, this.getString(msgResId), Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(msg: CharSequence) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.toastLong(msgResId: Int) {
    Toast.makeText(this, this.getString(msgResId), Toast.LENGTH_LONG).show()
}
