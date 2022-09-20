package com.hjy.template.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hjy.template.event.BaseEvent
import com.hjy.template.event.FlowEventBus
import com.hjy.template.event.postEvent
import com.hjy.template.exception.BusinessException
import com.hjy.template.exception.NetworkConnException
import com.hjy.template.exception.TokenInvalidException
import com.hjy.template.repository.AccountRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

/**
 * 所有 ViewModel 都必须继承该类或其子类，统一使用包含 Application 参数的 ViewModel
 */
open class BaseViewModel(app: Application) : AndroidViewModel(app) {

    /**
     * 用于 ViewModel 与 Activity 或 Fragment 之间的事件通知，例如 toast、弹窗等
     */
    val pageEventBus: FlowEventBus by lazy { FlowEventBus() }

    /**
     * Toast
     */
    fun toastLong(msg: CharSequence) {
        pageEventBus.postEventInScope(
            viewModelScope,
            BaseEvent.ToastEvent(msg, true)
        )
    }

    /**
     * Toast
     */
    fun toastShort(msg: CharSequence) {
        pageEventBus.postEventInScope(
            viewModelScope,
            BaseEvent.ToastEvent(msg, false)
        )
    }

    /**
     * 显示加载对话框
     */
    fun showLoading(msg: CharSequence) {
        pageEventBus.postEventInScope(
            viewModelScope,
            BaseEvent.ShowLoadingDialogEvent(msg, true)
        )
    }

    /**
     * 隐藏加载对话框
     */
    fun hideLoading() {
        pageEventBus.postEventInScope(
            viewModelScope,
            BaseEvent.ShowLoadingDialogEvent(null, false)
        )
    }

    /**
     * 关闭当前页面
     */
    fun closeCurrPage() {
        pageEventBus.postEventInScope(
            viewModelScope,
            BaseEvent.ClosePageEvent("")
        )
    }

    /**
     * 仅仅用于 ViewModel 与绑定的 Activity 或 Fragment 通信
     */
    inline fun <reified T: BaseEvent> postInternalPageEvent(event: T) {
        pageEventBus.postEventInScope(
            viewModelScope,
            event
        )
    }

    /**
     * 处理常用的异常
     */
    fun processCommonException(e: Throwable) {
        when(e) {
            is CancellationException -> {
                //协程取消的异常，一般不处理
                e.printStackTrace()
            }
            is NetworkConnException -> {
                toastShort("网络连接失败，请重试")
            }
            is TokenInvalidException -> {

            }
            is BusinessException -> {
                toastShort(e.message ?: "")
            }
        }
    }

    /**
     * 构造一个 API 请求的 flow
     *
     * @param showError 是否自动弹出错误信息，默认为 true，注意不管怎样错误都会继续传递下去，
     *                  很多时候你还需要在发生错误时处理一些额外的业务逻辑
     * @param apiRequest
     */
    suspend fun <T> launchApiRequestFlow(
        showError: Boolean = true,
        apiRequest: suspend () -> Flow<T>
    ): Flow<T> {
        return apiRequest()
            .catch {
                //如果是 token 过期
                if (it is TokenInvalidException) {
                    //清除用户登录信息
                    AccountRepository().logout()
                    //发送 token 过期事件
                    postEvent(BaseEvent.TokenInvalidEvent(null))
                }
                if (showError) {
                    processCommonException(it)
                }
                throw it
            }.flowOn(Dispatchers.IO)
    }

}