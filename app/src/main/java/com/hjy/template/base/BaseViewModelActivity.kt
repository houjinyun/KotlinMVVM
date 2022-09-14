package com.hjy.template.base

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.hjy.template.event.BaseEvent
import com.hjy.template.utils.toastLong
import com.hjy.template.utils.toastShort
import com.maning.mndialoglibrary.MProgressDialog
import kotlinx.coroutines.CoroutineDispatcher

/**
 * 所有使用 ViewModel 的 Activity 需继承该类
 */
abstract class BaseViewModelActivity<VM : BaseViewModel, VB : ViewBinding> : BaseActivity() {

    lateinit var mActivityViewModel: VM
    lateinit var mBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(getViewModelClass())
        super.onCreate(savedInstanceState)
        subscribeCommEvent()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        MProgressDialog.dismissProgress()
    }

    override fun initContentView() {
        mBinding = getViewBinding()
        setContentView(mBinding.root)
    }

    override fun getLayoutId(): Int? = null

    override fun isTranslucentBar(): Boolean = false

    /**
     * 与该 Activity 绑定的 ViewModel class.
     */
    abstract fun getViewModelClass(): Class<VM>

    abstract fun getViewBinding(): VB

    /**
     * 订阅页面内通用事件: Toast、Loading弹窗
     */
    private fun subscribeCommEvent() {
        launchAtLeast(Lifecycle.State.CREATED) {
            //注意 subscribeEvent 是 suspend 函数，不要试图在一个协程里订阅多个事件，这会导致只有第一个订阅生效
            //这里会挂起协程，后面如果还有代码不会被执行
            mActivityViewModel.pageEventBus.subscribeEvent<BaseEvent.ToastEvent> {
                if (it.isLong) {
                    toastLong(it.message)
                } else {
                    toastShort(it.message)
                }
            }
        }
        launchAtLeast(Lifecycle.State.CREATED) {
            mActivityViewModel.pageEventBus.subscribeEvent<BaseEvent.ShowLoadingDialogEvent> {
                if (it.showOrHide) {
                    showLoadingDialog(it.message ?: "")
                } else {
                    dismissLoadingDialog()
                }
            }
        }
        launchAtLeast(Lifecycle.State.CREATED) {
            mActivityViewModel.pageEventBus.subscribeEvent<BaseEvent.ClosePageEvent> {
                finish()
            }
        }
    }

    /**
     * 订阅与之绑定的 ViewModel 发送的事件
     */
    inline fun <reified T : BaseEvent> subscribeInternalPageEvent(
        state: Lifecycle.State = Lifecycle.State.CREATED,
        dispatcher: CoroutineDispatcher? = null, noinline onEvent: (event: T) -> Unit
    ) {
        launchAtLeast(state) {
            mActivityViewModel.pageEventBus.subscribeEvent(false, dispatcher, onEvent)
        }
    }

    /**
     * 显示Loading弹窗
     */
    fun showLoadingDialog(msg: CharSequence) {
        MProgressDialog.showProgress(this, msg)
    }

    /**
     * 隐藏Loading弹窗
     */
    fun dismissLoadingDialog() {
        MProgressDialog.dismissProgress()
    }

}