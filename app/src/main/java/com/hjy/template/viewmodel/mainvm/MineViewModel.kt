package com.hjy.template.viewmodel.mainvm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hjy.template.base.BaseViewModel
import com.hjy.template.event.BaseEvent
import com.hjy.template.event.postEvent
import com.hjy.template.global.UserInfoManager
import com.hjy.template.repository.AccountRepository
import com.hjy.template.utils.SettingUtil
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class MineViewModel(app: Application): BaseViewModel(app) {

    private val accountRepository = AccountRepository()

    fun logout() {
        viewModelScope.launch {
            showLoading("正在退出，请稍后...")
            launchApiRequestFlow(false) {
                accountRepository.logout()
            }.catch {
            }.onCompletion {
                hideLoading()
                accountRepository.clearUserInfo()
                postEvent(BaseEvent.LogoutEvent(""))
            }.collect {
            }
        }
    }

    fun updateAvatar(path: String) {
        viewModelScope.launch {
            SettingUtil.setAvatarUrl(path)
            UserInfoManager.updateAvatar(path)
        }
    }

}