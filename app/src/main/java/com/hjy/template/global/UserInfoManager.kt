package com.hjy.template.global

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


/**
 * 全局用户信息管理，采用单例模式
 */
object UserInfoManager {

    //用户信息
    private var userInfo: UserInfo = UserInfo()
    private var token: String? = null
    //采用 StateFlow，具备 LiveData 的功能，此外还支持背压
    private val userInfoStateFlow = MutableStateFlow(Resource(userInfo))

    fun getToken(): String? = token

    fun setToken(t: String?) {
        token = t
    }

    fun isLogin(): Boolean = !token.isNullOrEmpty()

    fun getUserInfo(): UserInfo = userInfo

    fun updateUserInfo(userInfo: UserInfo) {
        this.userInfo = userInfo
        userInfoStateFlow.value = Resource(userInfo)
    }

    fun updateName(name: String?) {
        userInfo.name = name
        userInfoStateFlow.value = Resource(userInfo)
    }
    fun updateAge(age: Int?) {
        userInfo.age = age
        userInfoStateFlow.value = Resource(userInfo)
    }
    fun updateGender(gender: Int?) {
        userInfo.gender = gender
        userInfoStateFlow.value = Resource(userInfo)
    }
    fun updateAddress(address: String?) {
        userInfo.address = address
        userInfoStateFlow.value = Resource(userInfo)
    }
    fun updateUserId(userId: String?) {
        userInfo.userId = userId
        userInfoStateFlow.value = Resource(userInfo)
    }
    fun updateAvatar(path: String?) {
        userInfo.avatar = path
        userInfoStateFlow.value = Resource(userInfo)
    }

    /**
     * 监听用户信息的变化
     */
    fun observe(owner: LifecycleOwner, observer: Observer<UserInfo>) {
        owner.lifecycleScope.launch {
            userInfoStateFlow.collect {
                observer.onChanged(it.data)
            }
        }
    }

    /**
     * 至少在某个状态下开始监听用户信息的变化
     */
    fun observeAtLeast(owner: LifecycleOwner, state: Lifecycle.State, observer: Observer<UserInfo>) {
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(state) {
                userInfoStateFlow.collect {
                    observer.onChanged(it.data)
                }
            }
        }
    }

    /**
     * 在某个 CoroutineScope 中监听
     *
     * @param scope
     * @param observer
     *
     * @return Job
     */
    fun observeInScope(scope: CoroutineScope, observer: Observer<UserInfo>): Job {
        return scope.launch {
            userInfoStateFlow.collect {
                observer.onChanged(it.data)
            }
        }
    }
}

class UserInfo {

    var name: String? = null
    var age: Int? = 0
    var gender: Int? = null
    var address: String? = null
    var userId: String? = null
    var avatar: String? = null

}