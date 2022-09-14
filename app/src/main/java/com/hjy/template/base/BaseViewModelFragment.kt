package com.hjy.template.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.hjy.template.event.BaseEvent
import com.hjy.template.utils.toastLong
import com.hjy.template.utils.toastShort
import com.maning.mndialoglibrary.MProgressDialog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

abstract class BaseViewModelFragment<VM : BaseViewModel, VB : ViewBinding> : BaseFragment() {

    private var _viewBinding: VB? = null

    lateinit var mFragmentViewModel: VM
    val mBinding get() = _viewBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFragmentViewModel =
            ViewModelProvider(if (isShareViewModelWithActivity()) requireActivity() else this).get(
                getViewModelClass()
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = getViewBinding()
        return _viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //如果与 Activity 公用一个 ViewModel，避免重复
        if (!isShareViewModelWithActivity()) {
            subscribeCommEvent()
        }
        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    /**
     * 与该 Fragment 绑定的 ViewModel class.
     */
    abstract fun getViewModelClass(): Class<VM>

    abstract fun getViewBinding(): VB

    override fun getLayoutId(): Int? = null

    /**
     * 是否与宿主 Activity 共享同一 ViewModel，默认为 false 即不共享
     *
     * @return true 表示与宿主 Activity 共享同一 ViewModel
     */
    open fun isShareViewModelWithActivity() = false

    /**
     * 订阅页面内通用事件
     */
    private fun subscribeCommEvent() {
        launchAtLeast(Lifecycle.State.CREATED) {
            mFragmentViewModel.pageEventBus.subscribeEvent<BaseEvent.ToastEvent> {
                if (it.isLong) {
                    activity?.toastLong(it.message)
                } else {
                    activity?.toastShort(it.message)
                }
            }
        }
        launchAtLeast(Lifecycle.State.CREATED) {
            mFragmentViewModel.pageEventBus.subscribeEvent<BaseEvent.ShowLoadingDialogEvent> {
                if (it.showOrHide) {
                    showLoadingDialog(it.message ?: "")
                } else {
                    dismissLoadingDialog()
                }
            }
        }
        launchAtLeast(Lifecycle.State.CREATED) {
            mFragmentViewModel.pageEventBus.subscribeEvent<BaseEvent.ClosePageEvent> {
                requireActivity().finish()
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
            mFragmentViewModel.pageEventBus.subscribeEvent(false, dispatcher, onEvent)
        }
    }

    fun showLoadingDialog(msg: CharSequence) {
        MProgressDialog.showProgress(requireActivity(), msg)
    }

    fun dismissLoadingDialog() {
        MProgressDialog.dismissProgress()
    }

}