package com.hjy.template.utils

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object WindowUtil {

    enum class Type {
        STATUS_BARS,        //状态栏
        NAVIGATION_BARS,    //导航栏
        CAPTION_BAR,
        SYSTEM_BARS,        //状态栏 + 导航栏 + CAPTION_BAR
    }

    private fun changeWindow(window: Window?, view: View, type: Type, isShow: Boolean) {
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            var types = when(type) {
                Type.STATUS_BARS -> WindowInsets.Type.statusBars()
                Type.NAVIGATION_BARS -> WindowInsets.Type.navigationBars()
                Type.CAPTION_BAR -> WindowInsets.Type.captionBar()
                Type.SYSTEM_BARS -> WindowInsets.Type.systemBars()
            }
            if (isShow) {
                window.insetsController?.show(types)
            } else {
                window.insetsController?.hide(types)
            }
        } else {
            var types = when(type) {
                Type.STATUS_BARS -> WindowInsetsCompat.Type.statusBars()
                Type.NAVIGATION_BARS -> WindowInsetsCompat.Type.navigationBars()
                Type.CAPTION_BAR -> WindowInsetsCompat.Type.captionBar()
                Type.SYSTEM_BARS -> WindowInsetsCompat.Type.systemBars()
            }
            ViewCompat.getWindowInsetsController(view)?.let {
                if (isShow) {
                    it.show(types)
                } else {
                    it.hide(types)
                }
            }
        }
    }

    /**
     * 控制导航栏的显示或隐藏
     *
     * @param window 如果不为空，在 targetSdk >= 30 时使用 android.view.WindowInsets 类来操作，否则使用 WindowInsetsCompat 类。
     * @param view
     * @param isShow true-显示, false-隐藏
     */
    fun showOrHideNavigationBars(window: Window?, view: View, isShow: Boolean) {
        changeWindow(window, view, Type.NAVIGATION_BARS, isShow)
    }

    /**
     * 控制状态栏的显示或隐藏
     *
     * @param window 如果不为空，在 targetSdk >= 30 时使用 android.view.WindowInsets 类来操作，否则使用 WindowInsetsCompat 类。
     * @param view
     * @param isShow true-显示, false-隐藏
     */
    fun showOrHideStatusBars(window: Window?, view: View, isShow: Boolean) {
        changeWindow(window, view, Type.STATUS_BARS, isShow)
    }

    /**
     *
     * @param window 如果不为空，在 targetSdk >= 30 时使用 android.view.WindowInsets 类来操作，否则使用 WindowInsetsCompat 类。
     * @param view
     * @param isShow true-显示, false-隐藏
     */
    fun showOrHideCaptionBar(window: Window?, view: View, isShow: Boolean) {
        changeWindow(window, view, Type.CAPTION_BAR, isShow)
    }

    /**
     * 控制 navigationBar + statusBar + captionBar 的显示或隐藏
     *
     * @param window 如果不为空，在 targetSdk >= 30 时使用 android.view.WindowInsets 类来操作，否则使用 WindowInsetsCompat 类。
     * @param view
     * @param isShow true-显示, false-隐藏
     */
    fun showOrHideSystemBars(window: Window?, view: View, isShow: Boolean) {
        changeWindow(window, view, Type.SYSTEM_BARS, isShow)
    }

    /**
     * 隐藏或者显示输入法
     *
     * @param window 如果不为空，在 targetSdk >= 30 时使用 android.view.WindowInsets 类来操作，否则使用 WindowInsetsCompat 类。
     * @param view
     * @param isShow
     */
    fun toggleSoftKeyboard(window: Window?, view: View, isShow: Boolean) {
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isShow) {
                window.insetsController?.show(WindowInsets.Type.ime())
            } else {
                window.insetsController?.hide(WindowInsets.Type.ime())
            }
        } else {
            ViewCompat.getWindowInsetsController(view)?.let {
                if (isShow) {
                    it.show(WindowInsetsCompat.Type.ime())
                } else {
                    it.hide(WindowInsetsCompat.Type.ime())
                }
            }
        }
    }

    /**
     * 改变状态栏字体颜色以适应浅色模式和深色模式
     *
     * @param window 如果不为空，在 targetSdk >= 30 时使用 android.view.WindowInsets 类来操作，否则使用 WindowInsetsCompat 类。
     * @param view
     * @param isLight true-浅色模式下， false-深色模式下
     */
    fun changeStatusBarFontColor(window: Window?, view: View, isLight: Boolean) {
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val state = if (isLight) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0
            window.insetsController?.setSystemBarsAppearance(state, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            ViewCompat.getWindowInsetsController(view)?.let {
                it.isAppearanceLightStatusBars = isLight
            }
        }
    }

    /**
     * 获取状态栏的高度
     */
    fun getStatusBarHeight(view: View): Int {
        var windowInsetsCompat = ViewCompat.getRootWindowInsets(view)
        var height = windowInsetsCompat?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
        if (height > 0) {
            return height
        }
        //上述方法获取不到，则尝试另外的方法
        val resourceId = view.resources.getIdentifier("status_bar_height", "dimen", "android")
        height = if (resourceId > 0) {
            view.resources.getDimensionPixelSize(resourceId)
        } else 0
        if (height > 0) {
            return height
        }
        //兜底高度
        return (30 * view.resources.displayMetrics.density).toInt()
    }

}