package com.hjy.template.viewmodel

import android.app.Application
import com.hjy.template.base.BaseViewModel
import com.hjy.template.event.BaseEvent
import com.hjy.template.event.postEvent
import com.hjy.template.event.subscribeEvent
import kotlinx.coroutines.Dispatchers

class SecondViewModel(app: Application): BaseViewModel(app) {

    fun subscribeLogoutEvent() {
        //屏幕旋转时，再次调用订阅方法，可以直接收到上次发出的事件
        subscribeEvent<BaseEvent.LogoutEvent>(isSticky = true, dispatcher = Dispatchers.IO) {
            println("[SecondViewModel]: 收到 LogoutEvent 通知事件-{${it.message}}, in io thread ${Thread.currentThread().name}")
        }
    }

    fun testPostEvent() {
        //发送普通事件
        postEvent(BaseEvent.LoginEvent("用户xxx登录"))
        //发送粘性事件
        postEvent(BaseEvent.LogoutEvent("用户xxx退出登录"), true)
    }

    fun testToastInfo() {
        toastLong("这是一个toast信息${System.currentTimeMillis()}")
    }

}