package com.hjy.template.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hjy.template.base.BaseViewModel
import com.hjy.template.event.ShowBannerEvent
import com.hjy.template.event.ShowTopArticleEvent
import com.hjy.template.event.postEvent
import com.hjy.template.utils.SettingUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingViewModel(app: Application): BaseViewModel(app) {

    private val _bannerSwitchFlow = MutableStateFlow(true)
    private val _topSwitchFlow = MutableStateFlow(true)

    val bannerSwitchFlow = _bannerSwitchFlow.asStateFlow()
    val topSwitchFlow = _topSwitchFlow.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _bannerSwitchFlow.value = SettingUtil.isShowBanner()
            _topSwitchFlow.value = SettingUtil.isShowTopArticle()
        }
    }

    fun updateBannerShowState(isShow: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val oldValue = SettingUtil.isShowBanner()
            if (oldValue != isShow) {
                SettingUtil.setShowBanner(isShow)
                //发送粘滞事件
                postEvent(ShowBannerEvent(isShow), true)
            }
        }
    }

    fun updateTopArticleShowState(isShow: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val oldValue = SettingUtil.isShowTopArticle()
            if (oldValue != isShow) {
                SettingUtil.setShowTopArticle(isShow)
                //发送粘滞事件
                postEvent(ShowTopArticleEvent(isShow), true)
            }
        }
    }

}