package com.hjy.template.repository

import com.hjy.template.api.ApiService
import com.hjy.template.api.RetrofitClient
import com.hjy.template.bean.LoginData
import com.hjy.template.global.UserInfo
import com.hjy.template.global.UserInfoManager
import com.hjy.template.utils.SettingUtil
import kotlinx.coroutines.flow.Flow

class AccountRepository: BaseRepository() {

    suspend fun doRegister(name: String, pwd: String, pwdRepeat: String): Flow<LoginData> = buildCommApiRequestFlow {
        ApiService.get().register(name, pwd, pwdRepeat)
    }

    suspend fun doLogin(name: String, pwd: String): Flow<LoginData> = buildCommApiRequestFlow {
        ApiService.get().login(name, pwd)
    }

    suspend fun logout(): Flow<Any> = buildCommApiRequestFlow {
        ApiService.get().logout()
    }

    fun saveLoginInfo(data: LoginData) {
        UserInfoManager.updateName(data.username)
        UserInfoManager.updateUserId(data.id.toString())
        UserInfoManager.setToken(SettingUtil.getCookie())
        RetrofitClient.updateToken(UserInfoManager.getToken() ?: "")
        SettingUtil.saveUserInfo(data)
        SettingUtil.setLogin(true)
    }

    fun clearUserInfo() {
        RetrofitClient.updateToken(null)
        SettingUtil.clearUserInfo()
        SettingUtil.setCookie("")
        SettingUtil.setLogin(false)
        UserInfoManager.setToken(null)
        UserInfoManager.updateUserInfo(UserInfo())
    }

}