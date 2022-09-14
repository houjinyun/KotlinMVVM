package com.hjy.template.utils

import com.hjy.template.bean.LoginData
import com.tencent.mmkv.MMKV

object SettingUtil {

    //通过微信的 MMKV 库来做一些通用设置
    private val mmkv = MMKV.defaultMMKV()

    /**
     * 是否 BottomTabBar 显示角标
     *
     * @return true 表示显示，false 表示不显示
     */
    fun isShowBottomTabBadge(): Boolean = mmkv.decodeBool("show_bottom_badge", true)

    /**
     * 设置是否显示底部 BottomTabBar 上的角标
     *
     * @param isShow
     */
    fun setShowBottomTabBadge(isShow: Boolean) = mmkv.encode("show_bottom_badge", isShow)

    fun setCookie(cookie: String): Boolean {
        return mmkv.encode("cookie", cookie)
    }

    fun getCookie(): String? {
        return mmkv.decodeString("cookie", null)
    }

    fun setLogin(flag: Boolean): Boolean {
        return mmkv.encode("isLogin", flag)
    }

    fun isLogin(): Boolean {
        return mmkv.decodeBool("isLogin", false)
    }

    fun saveUserInfo(user: LoginData) {
        mmkv.encode("userinfo", user)
    }

    fun clearUserInfo() {
        mmkv.remove("userinfo")
    }

    fun getUserInfo(): LoginData? {
        return mmkv.decodeParcelable("userinfo", LoginData::class.java)
    }

    fun isShowBanner(): Boolean {
        return mmkv.decodeBool("show_banner", true)
    }

    fun isShowTopArticle(): Boolean {
        return mmkv.decodeBool("show_top", true)
    }

    fun setShowBanner(isShow: Boolean) {
        mmkv.encode("show_banner", isShow)
    }

    fun setShowTopArticle(isShow: Boolean) {
        mmkv.encode("show_top", isShow)
    }

}