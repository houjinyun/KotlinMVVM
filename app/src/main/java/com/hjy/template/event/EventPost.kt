package com.hjy.template.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 应用内全局事件发送扩展方法
 */

/**
 * 发送通知事件
 *
 * @param event 事件
 * @param isSticky 是否粘性事件
 *
 * @return Job
 */
inline fun <reified T : BaseEvent> LifecycleOwner.postEvent(
    event: T,
    isSticky: Boolean = false
): Job {
    return lifecycleScope.launch {
        FlowEventBus.GLOBAL.postEvent(T::class.java.name, event, isSticky)
    }
}

/**
 * 发送通知事件
 *
 * @param event 事件
 * @param isSticky 是否粘性事件
 *
 * @return Job
 */
inline fun <reified T : BaseEvent> ViewModel.postEvent(event: T, isSticky: Boolean = false): Job {
    return viewModelScope.launch {
        FlowEventBus.GLOBAL.postEvent(T::class.java.name, event, isSticky)
    }
}

/**
 * 发送通知事件
 * @param scope 协程域
 * @param event 事件
 * @param isSticky 是否粘性事件
 *
 * @return Job
 */
inline fun <reified T : BaseEvent> postEvent(
    scope: CoroutineScope,
    event: T,
    isSticky: Boolean = false
): Job {
    return scope.launch {
        FlowEventBus.GLOBAL.postEvent(T::class.java.name, event, isSticky)
    }
}

inline fun <reified T : BaseEvent> removeStickEvent() {
    FlowEventBus.GLOBAL.removeStickEvent(T::class.java.name)
}

inline fun <reified T : BaseEvent> clearStickyEvent() {
    FlowEventBus.GLOBAL.clearStickEvent(T::class.java.name)
}
