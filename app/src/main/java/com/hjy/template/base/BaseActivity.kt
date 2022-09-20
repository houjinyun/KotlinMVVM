package com.hjy.template.base

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alibaba.android.arouter.launcher.ARouter
import com.hjy.template.utils.WindowUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 所有 Activity 都必须继承自该类或者其子类，处理一些通用业务逻辑
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isTranslucentBar()) {
            findViewById<View>(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.statusBarColor = Color.TRANSPARENT
        }
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this);
        if (application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            //在深色模式下
            changeStatusBarFontColor(false)
        } else {
            //在浅色模式下
            changeStatusBarFontColor(true)
        }
        initContentView()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        ARouter.getInstance().inject(this)
    }

    /**
     * 是否沉浸式状态栏
     */
    abstract fun isTranslucentBar(): Boolean

    /**
     * 如果采用 ViewBinding，可以重写该方法
     */
    open fun initContentView() {
        getLayoutId()?.let {
            setContentView(it)
        }
    }

    /**
     * Activity 对应 layout 文件 id
     *
     * @return layout 文件id，如果采用 ViewBinding 方案则直接返回 null 即可
     */
    abstract fun getLayoutId(): Int?

    /**
     * Activity 初始化入口
     */
    abstract fun init()

    /**
     * 改变状态栏字体颜色以适应浅色模式和深色模式
     *
     * @param isLight true-浅色模式下， false-深色模式下
     */
    fun changeStatusBarFontColor(isLight: Boolean) {
        window?.let {
            WindowUtil.changeStatusBarFontColor(it, findViewById(android.R.id.content), isLight)
        }
    }

    /**
     * repeatOnLifeCycle
     */
    fun launchAtLeast(state: Lifecycle.State = Lifecycle.State.CREATED, block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(state) {
                block()
            }
        }
    }
}