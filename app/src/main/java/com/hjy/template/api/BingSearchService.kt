package com.hjy.template.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface BingSearchService {

    companion object {
        private val apiService: BingSearchService by lazy {
            //采用自定义的 baseUrl
            return@lazy RetrofitClient.getRetrofit("https://cn.bing.com/").create(BingSearchService::class.java)
        }

        fun get(): BingSearchService = apiService
    }

    @GET("search")
    suspend fun searchByKeyword(@Query("q") keyword: String): ResponseBody

}