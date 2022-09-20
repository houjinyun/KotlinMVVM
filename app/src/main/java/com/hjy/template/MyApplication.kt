package com.hjy.template

import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.drake.statelayout.StateConfig
import com.hjy.template.api.RetrofitClient
import com.hjy.template.global.UserInfoManager
import com.hjy.template.utils.SettingUtil
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV

class MyApplication: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
        MMKV.initialize(this)

        SmartRefreshLayout.setDefaultRefreshHeaderCreator { _, _->
            ClassicsHeader(this).setFinishDuration(0)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { _, _ ->
            ClassicsFooter(this).setFinishDuration(0)
//            BallPulseFooter(this)
        }

        StateConfig.apply {
            emptyLayout = R.layout.layout_empty
            errorLayout = R.layout.layout_error
            loadingLayout = R.layout.layout_loading
            setRetryIds(R.id.iv)
        }

        //读取登录信息
        val isLogin = SettingUtil.isLogin()
        if (isLogin) {
            val token = SettingUtil.getCookie() ?: ""
            val user = SettingUtil.getUserInfo()
            val avatar = SettingUtil.getAvatar()
            user?.let {
                UserInfoManager.updateName(it.username)
                UserInfoManager.updateUserId(it.id.toString())
                UserInfoManager.updateAvatar(avatar)
            }
            UserInfoManager.setToken(token)
            RetrofitClient.updateToken(token)
        }


    }

}