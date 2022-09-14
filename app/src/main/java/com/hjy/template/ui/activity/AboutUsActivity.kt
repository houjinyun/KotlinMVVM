package com.hjy.template.ui.activity

import androidx.appcompat.app.AlertDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hjy.template.base.BaseViewModel
import com.hjy.template.base.BaseViewModelActivity
import com.hjy.template.databinding.ActivityAboutUsBinding

@Route(path = "/test/aboutus")
class AboutUsActivity: BaseViewModelActivity<BaseViewModel, ActivityAboutUsBinding>() {

    override fun isTranslucentBar(): Boolean = true

    override fun getViewModelClass(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun getViewBinding(): ActivityAboutUsBinding = ActivityAboutUsBinding.inflate(layoutInflater)

    override fun init() {
        mBinding.tvAboutVersion.text = "Version ${packageManager.getPackageInfo(packageName, 0).versionName}"
        mBinding.layoutAboutSourcecode.setOnClickListener {
            ARouter.getInstance().build("/test/webview")
                .withString("url", "https://www.wanandroid.com/blog/show/2")
                .withString("title", "源码")
                .navigation(this)
        }
        mBinding.layoutAboutApiaddress.setOnClickListener {
            ARouter.getInstance().build("/test/webview")
                .withString("url", "https://www.wanandroid.com/blog/show/2")
                .withString("title", "API地址")
                .navigation(this)
        }
        mBinding.layoutAboutCopyright.setOnClickListener {
            AlertDialog.Builder(this).setTitle("版权声明")
                .setMessage("APP 里使用的 API 均来自玩Android 官网，仅用于学习交流，不可用于任何商业交流。")
                .setPositiveButton("确定") { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
    }

}