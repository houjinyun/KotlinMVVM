package com.hjy.template.event

/**
 * EventBus 基础事件定义，所有自定义事件都必须继承该类
 */
open class BaseEvent(open val key: String) {

    /**
     * 用户登出 事件通知
     * @param message
     */
    data class LogoutEvent(val message: String) : BaseEvent("logout")

    /**
     * 用户登录 事件通知
     */
    data class LoginEvent(val message: String) : BaseEvent("login")

    /**
     * Toast 事件通知
     */
    data class ToastEvent(val message: CharSequence, val isLong: Boolean = false) :
        BaseEvent("toast")

    /**
     * 显示 Loading 弹窗 事件通知
     */
    data class ShowLoadingDialogEvent(val message: CharSequence?, val showOrHide: Boolean) : BaseEvent("loadingDialog")

    data class ClosePageEvent(val message: CharSequence?): BaseEvent("closepage")

    /**
     * token 过期的通知
     */
    data class TokenInvalidEvent(val message: String?): BaseEvent("tokeninvalid")
}


