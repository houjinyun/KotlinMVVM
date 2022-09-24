package com.hjy.template.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hjy.template.base.BaseViewModelActivity
import com.hjy.template.databinding.ActivityLoginBinding
import com.hjy.template.event.BaseEvent
import com.hjy.template.event.subscribeEvent
import com.hjy.template.viewmodel.AccountViewModel

@Route(path = "/test/login")
class LoginActivity: BaseViewModelActivity<AccountViewModel, ActivityLoginBinding>() {

    override fun isTranslucentBar() = true

    override fun getViewModelClass(): Class<AccountViewModel> = AccountViewModel::class.java

    override fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)

    override fun init() {
        mBinding.btnLogin.setOnClickListener {
            val name = mBinding.etPhone.text.toString().trim()
            val pwd = mBinding.etPwd.text.toString().trim()
            mActivityViewModel.doLogin(name, pwd)
        }

        mBinding.tvRegister.setOnClickListener {
            ARouter.getInstance().build("/test/register").navigation(this)
        }

        mBinding.etPhone.requestFocus()

        subscribeEvent<BaseEvent.LoginEvent> {
            finish()
        }
    }

}
