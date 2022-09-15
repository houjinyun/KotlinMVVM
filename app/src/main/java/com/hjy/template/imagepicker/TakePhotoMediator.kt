package com.hjy.template.imagepicker

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hjy.template.databinding.DialogBottomPhotoBinding

class TakePhotoMediator {

    companion object {
        private const val FRAGMENT_TAG = "InvisibleTakePhotoFragment"
    }

    private var activity: FragmentActivity? = null
    private var fragment: Fragment? = null

    private var takePhotoDialog: BottomSheetDialog? = null
    private var onTakePhotoCallback: ((photoPath: String) -> Unit)? = null
    private var onCancelCallback: (() -> Unit)? = null
    private var onErrorCallback: ((e: Throwable?) -> Unit)? = null

    private val fragmentManager: FragmentManager
        get() {
            return fragment?.childFragmentManager ?: activity!!.supportFragmentManager
        }

    private val invisibleFragment: InvisibleFragment
        get() {
            val existedFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
            return if (existedFragment != null) {
                existedFragment as InvisibleFragment
            } else {
                val invisibleFragment = InvisibleFragment()
                fragmentManager.beginTransaction()
                    .add(invisibleFragment, FRAGMENT_TAG)
                    .commitNowAllowingStateLoss()
                invisibleFragment
            }
        }

    constructor(activity: FragmentActivity) {
        this.activity = activity
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    takePhotoDialog?.dismiss()
                    takePhotoDialog = null
                }
            }
        })
    }

    constructor(fragment: Fragment) {
        this.fragment = fragment
        fragment.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    takePhotoDialog?.dismiss()
                    takePhotoDialog = null
                }
            }
        })
    }

    private fun build(activity: Activity): BottomSheetDialog {
        val dialog = BottomSheetDialog(activity)
        val binding = DialogBottomPhotoBinding.inflate(activity.layoutInflater)
        dialog.setContentView(binding.root)
        binding.tvCancle.setOnClickListener {
            dialog.dismiss()
        }
        binding.tvTakePhoto.setOnClickListener {
            dialog.dismiss()
            invisibleFragment.openCamera()
        }
        binding.tvFromAlbum.setOnClickListener {
            dialog.dismiss()
            invisibleFragment.openGallery()
        }
        invisibleFragment.setOutputListener { output ->
            if (output.isSuccess) {
                onTakePhotoCallback?.run {
                    invoke(output.filePath!!)
                }
            } else if (output.isCancel) {
                onCancelCallback?.run {
                    invoke()
                }
            } else {
                onErrorCallback?.run {
                    invoke(output.error)
                }
            }
        }
        return dialog
    }

    fun onTakePhoto(callback: (photoPath: String) -> Unit): TakePhotoMediator {
        onTakePhotoCallback = callback
        return this
    }

    fun onCancel(callback: () -> Unit): TakePhotoMediator {
        onCancelCallback = callback
        return this
    }

    fun onError(callback: (e: Throwable?) -> Unit): TakePhotoMediator {
        onErrorCallback = callback
        return this
    }

    /**
     * 不能在后台调用，否则不会弹窗显示
     */
    fun show(): BottomSheetDialog? {
        takePhotoDialog?.dismiss()
        val currState = fragment?.lifecycle?.currentState ?: activity?.lifecycle?.currentState
        if (currState?.isAtLeast(Lifecycle.State.RESUMED) == true) {
            val dialog = build(activity ?: fragment!!.requireActivity())
            dialog.show()
            takePhotoDialog = dialog
            return dialog
        }
        return null
    }

    class Output {

        companion object {
            fun success(filePath: String): Output {
                val result = Output()
                result.isSuccess = true
                result.filePath = filePath
                return result
            }

            fun cancel(): Output {
                val result = Output()
                result.isCancel = true
                return result
            }

            fun error(error: Throwable?): Output {
                val result = Output()
                result.isError = true
                result.error = error
                return result
            }

        }

        //是否成功
        var isSuccess: Boolean = false
        var filePath: String? = null
        //是否取消
        var isCancel: Boolean = false
        //是否报错
        var isError: Boolean = false
        var error: Throwable? = null
    }

}