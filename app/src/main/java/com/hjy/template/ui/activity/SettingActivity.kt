package com.hjy.template.ui.activity

import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjy.template.base.BaseViewModelActivity
import com.hjy.template.databinding.ActivitySettingBinding
import com.hjy.template.viewmodel.SettingViewModel

@Route(path = "/test/setting")
class SettingActivity: BaseViewModelActivity<SettingViewModel, ActivitySettingBinding>() {

    override fun isTranslucentBar(): Boolean = true

    override fun getViewModelClass(): Class<SettingViewModel> = SettingViewModel::class.java

    override fun getViewBinding(): ActivitySettingBinding = ActivitySettingBinding.inflate(layoutInflater)

    override fun init() {
        launchAtLeast(Lifecycle.State.RESUMED) {
            mActivityViewModel.bannerSwitchFlow.collect {
                mBinding.switchSettingBanner.isChecked = it
            }
        }
        launchAtLeast(Lifecycle.State.RESUMED) {
            mActivityViewModel.topSwitchFlow.collect {
                mBinding.switchSettingTop.isChecked = it
            }
        }

        mBinding.switchSettingBanner.setOnCheckedChangeListener { _, isChecked ->
            mActivityViewModel.updateBannerShowState(isChecked)
        }
        mBinding.switchSettingTop.setOnCheckedChangeListener { _, isChecked ->
            mActivityViewModel.updateTopArticleShowState(isChecked)
        }
    }

}