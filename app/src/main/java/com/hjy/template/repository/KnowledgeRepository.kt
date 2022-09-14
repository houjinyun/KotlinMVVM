package com.hjy.template.repository

import com.hjy.template.api.ApiService
import com.hjy.template.bean.ArticleResponseBody
import com.hjy.template.bean.KnowledgeTreeBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class KnowledgeRepository: BaseRepository() {

    suspend fun getKnowledgeTree(): Flow<List<KnowledgeTreeBody>> = buildCommApiRequestFlow {
        ApiService.get().getKnowledgeTree()
    }

    suspend fun getKnowledgeList(page: Int, cid: Int): Flow<ArticleResponseBody> = buildCommApiRequestFlow {
        ApiService.get().getKnowledgeList(page, cid)
    }

}