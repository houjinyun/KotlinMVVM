package com.hjy.template.widget

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.hjy.template.R

/**
 * 顶部导航栏，默认左边是返回按钮、中间是标题
 */
class Header(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val tvTitle: TextView
    private val ivBackIcon: ImageView
    private var rightMenuLayout: LinearLayout? = null
    private var ivRightMenu: ImageView? = null
    private val ivDivider: ImageView

    private var backIconOnClickListener: OnClickListener? = null
    private var rightMenuOnClickListener: OnClickListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        var typedArray = context.obtainStyledAttributes(attrs, R.styleable.HjyHeader)

        //中间标题
        var title = typedArray.getString(R.styleable.HjyHeader_headerTitle)
        var titleTextColor = typedArray.getColor(R.styleable.HjyHeader_headerTitleTextColor, Color.BLACK)
        var titleTextSize = typedArray.getDimension(
            R.styleable.HjyHeader_headerTitleTextSize,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                18f,
                context.resources.displayMetrics
            )
        )
        tvTitle = TextView(context, attrs).apply {
            id = R.id.header_title
            text = title
            setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize)
            setTextColor(titleTextColor)
            gravity = Gravity.CENTER
            setSingleLine()
            ellipsize = TextUtils.TruncateAt.END
            //限制最宽 200dp
            maxWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                200f,
                context.resources.displayMetrics
            ).toInt()
            ellipsize = TextUtils.TruncateAt.MARQUEE
            marqueeRepeatLimit = -1
            isFocusableInTouchMode = true
            isFocusable = true
            setOnClickListener(this@Header)
        }
        var titleLayoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_CONSTRAINT
        ).apply {
            leftToLeft = 0
            rightToRight = 0
            topToTop = 0
            bottomToBottom = 0
        }
        addView(tvTitle, titleLayoutParams)

        //左侧返回按钮
        val showBackIcon = typedArray.getBoolean(R.styleable.HjyHeader_headerShowBackIcon, true)
        val backIcon = typedArray.getDrawable(R.styleable.HjyHeader_headerBackIcon)
        ivBackIcon = LayoutInflater.from(context)
            .inflate(R.layout.header_layout_image, null, false) as ImageView
        ivBackIcon.apply {
            id = R.id.header_back_icon
            visibility = if (showBackIcon) View.VISIBLE else View.GONE
            if (backIcon == null) {
                setImageResource(R.drawable.vector_drawable_back_arrow_dark)
            } else {
                setImageDrawable(backIcon)
            }
            setOnClickListener(this@Header)
        }
        var backIconLayoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_CONSTRAINT
        ).apply {
            leftToLeft = 0
            topToTop = 0
            bottomToBottom = 0
        }
        addView(ivBackIcon, backIconLayoutParams)

        val rightMenuIcon = typedArray.getDrawable(R.styleable.HjyHeader_headerRightMenuIcon)
        if (rightMenuIcon != null) {
            setRightMenuIcon(rightMenuIcon)
        }

        val showDivider = typedArray.getBoolean(R.styleable.HjyHeader_headerShowDivider, true)
        ivDivider = ImageView(context)
        ivDivider.apply {
            setBackgroundColor(Color.LTGRAY)
            visibility = if (showDivider) View.VISIBLE else View.GONE
        }
        addView(ivDivider, LayoutParams(LayoutParams.MATCH_PARENT, 1).apply {
            leftToLeft = 0
            rightToRight = 0
            bottomToBottom = 0
        })

        typedArray.recycle()
    }

    private fun ensureRightMenuLayout() {
        if (rightMenuLayout != null)
            return
        rightMenuLayout = LinearLayout(context).apply {
            id = R.id.header_right_menu_content
            orientation = LinearLayout.HORIZONTAL
        }
        var rightMenuLayoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_CONSTRAINT
        ).apply {
            topToTop = 0
            bottomToBottom = 0
            rightToRight = 0
        }
        addView(rightMenuLayout, rightMenuLayoutParams)
    }

    private fun ensureRightMenuIcon() {
        if (ivRightMenu != null)
            return
        ivRightMenu = LayoutInflater.from(context)
            .inflate(R.layout.header_layout_image, null, false) as ImageView
        ivRightMenu?.apply {
            id = R.id.header_right_menu_icon
            setOnClickListener(this@Header)
        }
        var rightMenuImgLayoutParams = LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_PARENT
        )
        rightMenuLayout?.addView(ivRightMenu, rightMenuImgLayoutParams)
    }

    fun setTitle(title: CharSequence) {
        tvTitle.text = title
    }

    fun setTitle(textResId: Int) {
        tvTitle.setText(textResId)
    }

    fun setBackIcon(drawable: Drawable) {
        ivBackIcon.setImageDrawable(drawable)
    }

    fun setBackIcon(backIconResId: Int) {
        ivBackIcon.setImageResource(backIconResId)
    }

    fun setBackIcon(bmp: Bitmap) {
        ivBackIcon.setImageBitmap(bmp)
    }

    fun showBackIcon(isShow: Boolean) {
        ivBackIcon.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun setRightMenuIcon(drawable: Drawable) {
        ensureRightMenuLayout()
        ensureRightMenuIcon()
        ivRightMenu?.setImageDrawable(drawable)
    }

    fun setRightMenuIcon(backIconResId: Int) {
        ensureRightMenuLayout()
        ensureRightMenuIcon()
        ivRightMenu?.setImageResource(backIconResId)
    }

    fun setRightMenuIcon(bmp: Bitmap) {
        ensureRightMenuLayout()
        ensureRightMenuIcon()
        ivRightMenu?.setImageBitmap(bmp)
    }

    fun showDivider(isShow: Boolean) {
        ivDivider.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun setBackIconClickListener(listener: OnClickListener) {
        backIconOnClickListener = listener
    }

    fun setRightMenuClickListener(listener: OnClickListener) {
        rightMenuOnClickListener = listener
    }

    override fun onClick(v: View?) {
        v ?: return
        when(v.id) {
            R.id.header_back_icon -> {
                if (backIconOnClickListener != null) {
                    backIconOnClickListener!!.onClick(v)
                } else {
                    getActivityFromView()?.finish()
                }
            }
            R.id.header_title -> {

            }
            R.id.header_right_menu_icon -> {
                rightMenuOnClickListener?.onClick(v)
            }
        }
    }

    private fun getActivityFromView(): Activity? {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) {
                return ctx
            }
            ctx = ctx.baseContext
        }
        return null
    }

}