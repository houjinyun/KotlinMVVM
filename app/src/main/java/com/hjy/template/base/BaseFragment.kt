package com.hjy.template.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 所有 Fragment 都必须继承自该类或者其子类，处理一些通用业务逻辑
 */
abstract class BaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getLayoutId()?.let {
            return inflater.inflate(it, null, false)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * Fragment 对应 layout 文件 id
     *
     * @return layout 文件id，如果采用 ViewBinding 方案则直接返回 null，重写 onCreateView 方法即可
     */
    abstract fun getLayoutId(): Int?

    abstract fun init()

    /**
     * repeatOnLifeCycle
     */
    fun launchAtLeast(state: Lifecycle.State, block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(state) {
                block()
            }
        }
    }
}