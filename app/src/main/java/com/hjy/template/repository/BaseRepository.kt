package com.hjy.template.repository

import com.hjy.template.bean.ResponseData
import com.hjy.template.exception.BusinessException
import com.hjy.template.exception.NetworkConnException
import com.hjy.template.exception.TokenInvalidException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okio.IOException

open class BaseRepository {

    suspend fun <T> buildCommApiRequestFlow(apiRequest: suspend () -> ResponseData<T>): Flow<T> = flow {
        try {
            val response = apiRequest()
            if (response.errorCode == 0) {
                emit(response.data)
            } else if (response.errorCode == -1001){
                throw TokenInvalidException(response.errorMsg)
            } else {
                throw BusinessException(response.errorCode, response.errorMsg)
            }
        } catch (e: Exception) {
            if (e is IOException) {
                //连接出现异常
                throw NetworkConnException(e.message ?: "")
            } else {
                throw e
            }
        }
    }

}