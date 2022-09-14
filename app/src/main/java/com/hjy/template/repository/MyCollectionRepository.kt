package com.hjy.template.repository

import com.hjy.template.api.ApiService
import com.hjy.template.bean.BaseListResponseBody
import com.hjy.template.bean.CollectionArticle
import kotlinx.coroutines.flow.Flow

class MyCollectionRepository: BaseRepository() {

    suspend fun getMyCollection(pageNum: Int): Flow<BaseListResponseBody<CollectionArticle>> = buildCommApiRequestFlow {
        ApiService.get().getCollectList(pageNum)
    }

}