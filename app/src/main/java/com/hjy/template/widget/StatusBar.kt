package com.hjy.template.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.hjy.template.utils.WindowUtil

/**
 * 在沉浸式状态栏模式下，Activity 内容会被状态栏盖住，使用 StatusBar 作为占位 View，占据状态栏的空间
 */
class StatusBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            WindowUtil.getStatusBarHeight(this)
        )
    }

}