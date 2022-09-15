package com.hjy.template.ui.mainfragment

import android.Manifest
import android.view.View
import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hjy.template.R
import com.hjy.template.base.BaseViewModelFragment
import com.hjy.template.databinding.FragmentMineBinding
import com.hjy.template.imagepicker.TakePhotoHelper
import com.hjy.template.global.UserInfoManager
import com.hjy.template.utils.toastShort
import com.hjy.template.viewmodel.mainvm.MineViewModel
import com.permissionx.guolindev.PermissionX

class MineFragment : BaseViewModelFragment<MineViewModel, FragmentMineBinding>() {

    private var takePhotoDialog: BottomSheetDialog? = null

    override fun getViewModelClass(): Class<MineViewModel> = MineViewModel::class.java

    override fun getViewBinding(): FragmentMineBinding =
        FragmentMineBinding.inflate(layoutInflater, null, false)

    override fun init() {
        UserInfoManager.observeAtLeast(viewLifecycleOwner, Lifecycle.State.RESUMED) {
            if (UserInfoManager.isLogin()) {
                mBinding.tvUserInfo.text = UserInfoManager.getUserInfo().name
                mBinding.tvUserId.text = UserInfoManager.getUserInfo().userId
                mBinding.tvToLogin.visibility = View.INVISIBLE
                Glide.with(this)
                    .load(UserInfoManager.getUserInfo().avatar)
                    .placeholder(R.mipmap.icon_profile)
                    .into(mBinding.ivAvatar)
            } else {
                mBinding.tvUserInfo.text = ""
                mBinding.tvUserId.text = ""
                mBinding.tvToLogin.visibility = View.VISIBLE
                mBinding.ivAvatar.setImageResource(R.mipmap.icon_profile)
            }
        }
        mBinding.layoutCollect.setOnClickListener {
            if (!UserInfoManager.isLogin()) {
                ARouter.getInstance().build("/test/login").navigation(requireActivity())
                return@setOnClickListener
            }
            ARouter.getInstance().build("/test/mycollection").navigation(requireActivity())
        }
        mBinding.layoutSetting.setOnClickListener {
            ARouter.getInstance().build("/test/setting").navigation(requireActivity())
        }
        mBinding.layoutAbout.setOnClickListener {
            ARouter.getInstance().build("/test/aboutus").navigation(requireActivity())
        }
        mBinding.layoutLogout.setOnClickListener {
            if (!UserInfoManager.isLogin()) {
                requireActivity().toastShort("您还没有登录！")
                return@setOnClickListener
            }
            androidx.appcompat.app.AlertDialog.Builder(requireActivity())
                .setTitle("温馨提示")
                .setMessage("您确定要退出登录吗？")
                .setPositiveButton(
                    "确定"
                ) { dialog, _ ->
                    mFragmentViewModel.logout()
                    dialog.dismiss()
                }
                .setNegativeButton("取消") { _, _ -> }
                .create().show()
        }

        mBinding.layoutAvatar.setOnClickListener {
            if (!UserInfoManager.isLogin()) {
                ARouter.getInstance().build("/test/login").navigation(requireActivity())
                return@setOnClickListener
            }
            requestPermission()
        }

    }

    private fun requestPermission() {
        PermissionX.init(this)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "设置头像需要这些权限", "确定", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "您需要在应用设置里开启这些权限", "确定", "取消")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    showTakePhotoPicker()
                } else {
                    requireActivity().toastShort("缺少相关权限，请到设置里开启")
                }
            }
    }

    private fun showTakePhotoPicker() {
        if (takePhotoDialog != null) {
            takePhotoDialog?.show()
            return
        }
        takePhotoDialog = TakePhotoHelper.init(this)
            .onTakePhoto {
                mFragmentViewModel.updateAvatar(it)
            }
            .onCancel {
                requireActivity().toastShort("用户已取消")
            }
            .onError {
                requireActivity().toastShort("出现错误")
            }
            .show()
    }

}