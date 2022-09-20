package com.hjy.template.repository

import com.hjy.template.api.ApiService
import com.hjy.template.bean.*
import kotlinx.coroutines.flow.Flow

class SearchRepository: BaseRepository() {

    suspend fun searchByKey(pageNum: Int, key: String): Flow<ArticleResponseBody> = buildCommApiRequestFlow {
        ApiService.get().queryBySearchKey(pageNum, key)
    }

    suspend fun getHotSearch(): Flow<MutableList<HotSearchBean>> = buildCommApiRequestFlow {
        ApiService.get().getHotSearchData()
    }

}