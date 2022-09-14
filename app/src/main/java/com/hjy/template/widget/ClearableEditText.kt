package com.hjy.template.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import com.hjy.template.R

class ClearableEditText constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs) {

    companion object {
        var CLICK_EXPAND = 20
    }

    private var clearDrawable: Drawable
    private val clearRect = Rect()
    private val clickRect = Rect()

    init {
        var typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearableEditText)
        var drawable = typedArray.getDrawable(R.styleable.ClearableEditText_clear_drawable)
        clearDrawable = drawable ?: resources.getDrawable(R.mipmap.ic_clear, context.theme)
        typedArray.recycle()
        setPadding(paddingLeft, paddingTop, paddingRight + clearDrawable.intrinsicWidth, paddingBottom)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        clearRect.set(
            width - paddingRight,
            (height - clearDrawable.intrinsicHeight) / 2,
            width - paddingRight + clearDrawable.intrinsicWidth,
            (height + clearDrawable.intrinsicHeight) / 2
        )
        clickRect.set(
            clearRect.left - CLICK_EXPAND,
            clearRect.top - CLICK_EXPAND,
            clearRect.right + CLICK_EXPAND,
            clearRect.bottom + CLICK_EXPAND
        )
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (!text.isNullOrEmpty() && canvas != null) {
            clearDrawable.setBounds(clearRect.left, clearRect.top + scrollY,
                clearRect.right, clearRect.bottom + scrollY)
            clearDrawable.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            if (!text.isNullOrEmpty()
                && event.x >= clickRect.left && event.x <= clickRect.right
                && event.y >= clickRect.top && event.y <= clickRect.bottom) {
                text = null
                return true
            }
        }
        return super.onTouchEvent(event)
    }

}