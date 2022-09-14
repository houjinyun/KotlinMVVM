package com.hjy.template.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hjy.template.base.BaseViewModel
import com.hjy.template.event.BaseEvent
import com.hjy.template.event.postEvent
import com.hjy.template.repository.AccountRepository
import com.hjy.template.utils.SettingUtil
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class AccountViewModel(app: Application): BaseViewModel(app) {

    private val accountRepository = AccountRepository()

    fun doRegister(name: String, pwd: String, pwdRepeat: String) {
        if (name.isEmpty()) {
            toastShort("请输入用户名")
            return
        }
        if (pwd.isEmpty() || pwdRepeat.isEmpty()) {
            toastShort("请输入密码")
            return
        }
        if (pwd != pwdRepeat) {
            toastShort("两次输入的密码不一致")
            return
        }

        viewModelScope.launch {
            showLoading("正在注册，请稍后...")
            accountRepository.doRegister(name, pwd, pwdRepeat)
                .catch {
                    processCommonException(it)
                }.onCompletion {
                    hideLoading()
                }.collect {
                    accountRepository.saveLoginInfo(it)
                    postEvent(BaseEvent.LoginEvent("登录成功"))
                    toastShort("注册成功")
                }
        }

    }

    fun doLogin(name: String, pwd: String) {
        if (name.isEmpty()) {
            toastShort("请输入用户名")
            return
        }
        if (pwd.isEmpty()) {
            toastShort("请输入密码")
            return
        }
        viewModelScope.launch {
            showLoading("正在登录，请稍后...")
            accountRepository.doLogin(name, pwd)
                .catch {
                    processCommonException(it)
                }.onCompletion {
                    hideLoading()
                }.collect {
                    accountRepository.saveLoginInfo(it)
                    postEvent(BaseEvent.LoginEvent("登录成功"))
                    toastShort("登录成功")
                }
        }
    }

}