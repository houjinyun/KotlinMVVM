package com.hjy.template.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hjy.template.base.BaseViewModel
import com.hjy.template.utils.SettingUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(app: Application): BaseViewModel(app) {

    // > 0 则显示， = 0 则不显示
    private val _mineShowRedDotFlow = MutableStateFlow(false)
    val mineShowRedDotFlow = _mineShowRedDotFlow.asStateFlow()

    /**
     * 计算各种未读消息数，然后在 tab 上显示对应的红点或未读消息数
     */
    fun calcUnreadCount() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val isShowBadge = SettingUtil.isShowBottomTabBadge()
                if (isShowBadge) {

                } else {
                    _mineShowRedDotFlow.value = false
                }
            }
        }
    }

}