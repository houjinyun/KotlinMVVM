package com.hjy.template.event

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 应用内全局事件订阅扩展方法
 */

/**
 * 在 LifecycleOwner 中订阅事件
 *
 * @param isSticky 是否订阅粘性事件，默认为非粘性，注意只有发送粘性事件这里才能收到粘性事件，必须一一对应
 * @param dispatcher 订阅事件回调运行线程，默认运行在主线程
 * @param onEvent
 */
inline fun <reified T : BaseEvent> LifecycleOwner.subscribeEvent(
    isSticky: Boolean = false,
    dispatcher: CoroutineDispatcher? = null,
    noinline onEvent: (event: T) -> Unit
): Job {
    return lifecycleScope.launch {
        FlowEventBus.GLOBAL.subscribeEvent(T::class.java.name, isSticky, dispatcher, onEvent)
    }
}

/**
 * 在指定生命周期内订阅事件，注意在某些生命周期之外可能会收不到通知
 *
 * @param state 配合 repeatOnLifecycle 使用，支持生命周期的最小状态，默认为 CREATED
 * @param isSticky 是否订阅粘性事件，默认为非粘性，注意只有发送粘性事件这里才能收到粘性事件，必须一一对应
 * @param dispatcher 订阅事件回调运行线程，默认运行在主线程
 * @param onEvent
 */
inline fun <reified T : BaseEvent> LifecycleOwner.subscribeEventAtLeast(
    state: Lifecycle.State = Lifecycle.State.CREATED,
    isSticky: Boolean = false,
    dispatcher: CoroutineDispatcher? = null,
    noinline onEvent: (event: T) -> Unit
): Job {
    return lifecycleScope.launch {
        repeatOnLifecycle(state) {
            FlowEventBus.GLOBAL.subscribeEvent(T::class.java.name, isSticky, dispatcher, onEvent)
        }
    }
}

/**
 * 在 ViewModel 中调用订阅方法，由于 ViewModel 生命周期比较长，屏幕旋转时可能会导致多次订阅，故在同一个 ViewModel
 * 用 key 做区分，同一 key 的订阅事件，会自动取消之前的订阅
 */
val ViewModel.eventJobMap: MutableMap<String, Job> by lazy { mutableMapOf() }

/**
 * 在 ViewModel 中订阅事件
 *
 * @param key 一种类型的事件指定一个唯一的 key，为了避免横竖屏切换时导致多次重复订阅，默认为事件类名
 * @param isSticky 是否订阅粘性事件，注意只有发送粘性事件这里才能收到粘性事件，必须一一对应
 * @param dispatcher 订阅事件回调运行线程，默认运行在主线程
 * @param onEvent
 */
inline fun <reified T : BaseEvent> ViewModel.subscribeEvent(
    key: String = T::class.java.name,
    isSticky: Boolean = false,
    dispatcher: CoroutineDispatcher? = null,
    noinline onEvent: (event: T) -> Unit
): Job {
    eventJobMap[key]?.let {
        it.cancel()
    }
    var job = viewModelScope.launch {
        FlowEventBus.GLOBAL.subscribeEvent(T::class.java.name, isSticky, dispatcher, onEvent)
    }
    job.invokeOnCompletion {
        eventJobMap.remove(key)
    }
    eventJobMap[key] = job
    return job
}
