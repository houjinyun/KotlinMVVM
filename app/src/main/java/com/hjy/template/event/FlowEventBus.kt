package com.hjy.template.event

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * 采用 SharedFlow 实现的本地 EventBus
 */
class FlowEventBus {

    companion object {
        //默认全局的 EventBus，用于应用间通信
        val GLOBAL: FlowEventBus = FlowEventBus()
    }

    //正常事件
    private val eventFlowMap: ConcurrentHashMap<String, MutableSharedFlow<BaseEvent>> by lazy {
        ConcurrentHashMap<String, MutableSharedFlow<BaseEvent>>()
    }

    //粘性事件
    private val stickyEventFlowMap: ConcurrentHashMap<String, MutableSharedFlow<BaseEvent>> by lazy {
        ConcurrentHashMap<String, MutableSharedFlow<BaseEvent>>()
    }

    /**
     * 根据事件名获取对应的 SharedFlow
     *
     * @param eventType 事件类型名称
     * @param isSticky 是否粘性
     */
    private fun getEventFlow(eventType: String, isSticky: Boolean): MutableSharedFlow<BaseEvent> {
        return if (isSticky) {
            stickyEventFlowMap[eventType]
        } else {
            eventFlowMap[eventType]
        } ?: MutableSharedFlow<BaseEvent>(
            if (isSticky) 1 else 0,
            Int.MAX_VALUE
        ).also {
            if (isSticky) {
                stickyEventFlowMap[eventType] = it
            } else {
                eventFlowMap[eventType] = it
            }
        }
    }

    /**
     * 发送事件
     *
     * @param eventType 事件类型名称
     * @param event
     * @param isSticky 是否粘性
     */
    suspend fun postEvent(eventType: String, event: BaseEvent, isSticky: Boolean = false) {
        getEventFlow(eventType, isSticky).emit(event)
    }

    suspend inline fun <reified T : BaseEvent> postEvent(
        event: T,
        isSticky: Boolean = false
    ) {
        postEvent(T::class.java.name, event, isSticky)
    }

    inline fun <reified T : BaseEvent> postEventInScope(
        scope: CoroutineScope,
        event: T,
        isSticky: Boolean = false
    ) {
        scope.launch {
            postEvent(T::class.java.name, event, isSticky)
        }
    }

    /**
     * 订阅事件
     *
     * @param eventType 事件类型名称
     * @param isSticky 是否订阅粘性事件，必须发送的也是粘性事件，订阅者才能收到粘性事件
     * @param dispatcher 事件接收回调线程调度器，可设置来切换线程
     * @param onEvent 事件通知回调
     *
     */
    suspend fun <T : BaseEvent> subscribeEvent(
        eventType: String,
        isSticky: Boolean = false,
        dispatcher: CoroutineDispatcher? = null,
        onEvent: (event: T) -> Unit
    ) {
        getEventFlow(eventType, isSticky).collect {
            if (dispatcher == null) {
                invokeOnEvent(it, onEvent)
            } else {
                withContext(dispatcher) {
                    invokeOnEvent(it, onEvent)
                }
            }
        }
    }

    suspend inline fun <reified T : BaseEvent> subscribeEvent(
        isSticky: Boolean = false,
        dispatcher: CoroutineDispatcher? = null,
        noinline onEvent: (event: T) -> Unit
    ) {
        subscribeEvent(T::class.java.name, isSticky, dispatcher, onEvent)
    }

    inline fun <reified T : BaseEvent> subscribeEventInScope(
        scope: CoroutineScope, isSticky: Boolean = false, dispatcher: CoroutineDispatcher? = null,
        noinline onEvent: (event: T) -> Unit
    ) {
        scope.launch {
            subscribeEvent(T::class.java.name, isSticky, dispatcher, onEvent)
        }
    }

    private fun <T : BaseEvent> invokeOnEvent(event: BaseEvent, onEvent: (event: T) -> Unit) {
        try {
            onEvent(event as T)
        } catch (e: ClassCastException) {
            Log.e("EventBus", "Class cast error on receive event: $event")
        } catch (e: Exception) {
            Log.e("EventBus", "Error on receive event: $event")
        }
    }

    fun removeStickEvent(eventType: String) {
        stickyEventFlowMap.remove(eventType)
    }

    fun clearStickEvent(eventType: String) {
        stickyEventFlowMap[eventType]?.resetReplayCache()
    }

}