package com.hjy.template.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.hjy.template.base.BaseViewModelActivity
import com.hjy.template.databinding.ActivityRegisterBinding
import com.hjy.template.event.BaseEvent
import com.hjy.template.event.subscribeEvent
import com.hjy.template.viewmodel.AccountViewModel

@Route(path = "/test/register")
class RegisterActivity: BaseViewModelActivity<AccountViewModel, ActivityRegisterBinding>() {

    override fun isTranslucentBar() = true

    override fun getViewModelClass(): Class<AccountViewModel> = AccountViewModel::class.java

    override fun getViewBinding(): ActivityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)

    override fun init() {
        mBinding.btnLogin.setOnClickListener {
            val name = mBinding.etPhone.text.toString().trim()
            val pwd = mBinding.etPwd.text.toString().trim()
            val pwdRepeat = mBinding.etPwdRepeat.text.toString().trim()
            mActivityViewModel.doRegister(name, pwd, pwdRepeat)
        }

        subscribeEvent<BaseEvent.LoginEvent> {
            finish()
        }
    }

}
