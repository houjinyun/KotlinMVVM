package com.hjy.template.api

import com.hjy.template.bean.*
import retrofit2.http.*

/**
 * 通过 ApiService.get() 即可获取到 ApiService 对象
 */
interface ApiService {

    companion object {
        private val apiService: ApiService by lazy {
            //采用默认的 baseUrl
            return@lazy RetrofitClient.getRetrofit().create(ApiService::class.java)
        }

        fun get(): ApiService = apiService
    }

    @GET("banner/json")
    suspend fun getBanners(): ResponseData<List<Banner>>

    /**
     * 获取首页置顶文章列表
     * eg: http://www.wanandroid.com/article/top/json
     */
    @GET("article/top/json")
    suspend fun getTopArticles(): ResponseData<MutableList<Article>>

    /**
     * 获取文章列表
     * eg: http://www.wanandroid.com/article/list/0/json
     *
     * @param pageNum 页码，从 0 开始
     */
    @GET("article/list/{pageNum}/json")
    suspend fun getArticles(@Path("pageNum") pageNum: Int): ResponseData<ArticleResponseBody>

    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String,
        @Header("login") flag: String = "true"
    ): ResponseData<LoginData>

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Header("login") flag: String = "true"
    ): ResponseData<LoginData>

    @GET("user/logout/json")
    suspend fun logout(): ResponseData<Any>

    /**
     * 收藏
     */
    @POST("lg/collect/{id}/json")
    suspend fun addCollectArticle(@Path("id") id: Int): ResponseData<Any>

    /**
     * 取消收藏
     */
    @POST("lg/uncollect_originId/{id}/json")
    suspend fun cancelCollectArticle(@Path("id") id: Int): ResponseData<Any>

    /**
     *  获取收藏列表
     *  http://www.wanandroid.com/lg/collect/list/0/json
     *  @param page
     */
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectList(@Path("page") page: Int): ResponseData<BaseListResponseBody<CollectionArticle>>


    @POST("lg/uncollect/{id}/json")
    @FormUrlEncoded
    suspend fun removeCollectArticle(
        @Path("id") id: Int,
        @Field("originId") originId: Int = -1
    ): ResponseData<Any>

    /**
     * 获取知识体系
     * http://www.wanandroid.com/tree/json
     */
    @GET("tree/json")
    suspend fun getKnowledgeTree(): ResponseData<List<KnowledgeTreeBody>>

    /**
     * 知识体系下的文章
     * http://www.wanandroid.com/article/list/0/json?cid=168
     * @param page
     * @param cid
     */
    @GET("article/list/{page}/json")
    suspend fun getKnowledgeList(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): ResponseData<ArticleResponseBody>

    /**
     * 问答
     * pageId,拼接在链接上，例如上面的1
     */
    @GET("wenda/list/{page}/json")
    suspend fun getQuestionList(@Path("page") page: Int): ResponseData<ArticleResponseBody>

}