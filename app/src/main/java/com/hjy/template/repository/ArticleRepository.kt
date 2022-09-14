package com.hjy.template.repository

import com.hjy.template.api.ApiService
import com.hjy.template.bean.*
import kotlinx.coroutines.flow.Flow

class ArticleRepository: BaseRepository() {

    suspend fun getBannerList(): Flow<List<Banner>> = buildCommApiRequestFlow {
        ApiService.get().getBanners()
    }

    suspend fun getTopArticles(): Flow<MutableList<Article>> = buildCommApiRequestFlow {
        val response = ApiService.get().getTopArticles()
        if (response.errorCode == 0) {
            for (article in response.data) {
                article.top = "1"
            }
        }
        response
    }

    suspend fun getArticleList(pageNum: Int): Flow<ArticleResponseBody> = buildCommApiRequestFlow {
        ApiService.get().getArticles(pageNum)
    }

    suspend fun addCollectArticle(id: Int): Flow<Any> = buildCommApiRequestFlow {
        ApiService.get().addCollectArticle(id)
    }

    suspend fun cancelCollectArticle(id: Int): Flow<Any> = buildCommApiRequestFlow {
        ApiService.get().cancelCollectArticle(id)
    }

    suspend fun getMyCollection(pageNum: Int): Flow<BaseListResponseBody<CollectionArticle>> = buildCommApiRequestFlow {
        ApiService.get().getCollectList(pageNum)
    }

    suspend fun cancelCollectArticle(id: Int, originId: Int): Flow<Any> = buildCommApiRequestFlow {
        ApiService.get().removeCollectArticle(id, originId)
    }

    suspend fun getQuestionList(pageNum: Int): Flow<ArticleResponseBody> = buildCommApiRequestFlow {
        ApiService.get().getQuestionList(pageNum)
    }

}