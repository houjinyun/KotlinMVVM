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
                requireActivity().toastShort("?????????????????????")
                return@setOnClickListener
            }
            androidx.appcompat.app.AlertDialog.Builder(requireActivity())
                .setTitle("????????????")
                .setMessage("??????????????????????????????")
                .setPositiveButton(
                    "??????"
                ) { dialog, _ ->
                    mFragmentViewModel.logout()
                    dialog.dismiss()
                }
                .setNegativeButton("??????") { _, _ -> }
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
            .permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "??????????????????????????????", "??????", "??????")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "?????????????????????????????????????????????", "??????", "??????")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    showTakePhotoPicker()
                } else {
                    requireActivity().toastShort("??????????????????????????????????????????")
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
                requireActivity().toastShort("???????????????")
            }
            .onError {
                requireActivity().toastShort("????????????")
            }
            .show()
    }

}