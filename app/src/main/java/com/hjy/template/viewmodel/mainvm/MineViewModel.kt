package com.hjy.template.viewmodel.mainvm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hjy.template.base.BaseViewModel
import com.hjy.template.event.BaseEvent
import com.hjy.template.event.postEvent
import com.hjy.template.repository.AccountRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MineViewModel(app: Application): BaseViewModel(app) {

    private val accountRepository = AccountRepository()

    fun logout() {
        viewModelScope.launch {
            showLoading("正在退出，请稍后...")
            accountRepository.logout()
                .catch {
                }.onCompletion {
                    hideLoading()
                    accountRepository.clearUserInfo()
                    postEvent(BaseEvent.LogoutEvent(""))
                }.collect {
                }
        }
    }

}