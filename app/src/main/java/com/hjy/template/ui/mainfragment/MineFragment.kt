package com.hjy.template.ui.mainfragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alibaba.android.arouter.launcher.ARouter
import com.hjy.template.base.BaseViewModelFragment
import com.hjy.template.databinding.FragmentMineBinding
import com.hjy.template.global.UserInfo
import com.hjy.template.global.UserInfoManager
import com.hjy.template.utils.LocationUtil
import com.hjy.template.utils.toastShort
import com.hjy.template.viewmodel.mainvm.MineViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MineFragment: BaseViewModelFragment<MineViewModel, FragmentMineBinding>() {

    override fun getViewModelClass(): Class<MineViewModel> = MineViewModel::class.java

    override fun getViewBinding(): FragmentMineBinding = FragmentMineBinding.inflate(layoutInflater, null, false)

    override fun init() {
        UserInfoManager.observeAtLeast(viewLifecycleOwner, Lifecycle.State.RESUMED) {
            if (UserInfoManager.isLogin()) {
                mBinding.tvUserInfo.text = UserInfoManager.getUserInfo().name
                mBinding.tvUserId.text = UserInfoManager.getUserInfo().userId
                mBinding.tvToLogin.visibility = View.INVISIBLE
            } else {
                mBinding.tvUserInfo.text = ""
                mBinding.tvUserId.text = ""
                mBinding.tvToLogin.visibility = View.VISIBLE
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
                .setPositiveButton("确定"
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
        }

    }


}