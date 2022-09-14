package com.hjy.template.api

import com.hjy.template.utils.SettingUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    //默认超时时间
    private const val DEFAULT_TIMEOUT = 30L

    /**
     * 默认的 baseUrl
     */
    private var defaultBaseUrl = "https://wanandroid.com/"

    /**
     * 根据 baseUrl 的不同来存储 retrofit 对象
     */
    private val retrofitMap = mutableMapOf<String, Retrofit>()

    /**
     * http 日志打印级别
     */
    private var httpLoggingLevel = HttpLoggingInterceptor.Level.BODY

    /**
     * 默认使用的 OkHttpClient
     */
    private val okHttpClient: OkHttpClient by lazy {
        buildOkHttpClient()
    }

    //用户登录之后的 token
    private var token: String? = null

    /**
     * 构建 OkHttpClient
     */
    private fun buildOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = httpLoggingLevel
        return builder.addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                //自定义拦截器，这里可为所有的请求都加上通用的请求头
                val builder = chain.request()
                    .newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                //根据实际需求来
//                    .addHeader("token", token ?: "")
                if (!token.isNullOrEmpty()) {
                    builder.addHeader("Cookie", token!!)
                }
                val request = builder.build()
                return@addInterceptor chain.proceed(request)
            }.addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                if (request.header("login") == "true") {
                    //从这里取 cookie
                    val list = response.headers("Set-Cookie")
                    val cookie = encodeCookie(list)
                    SettingUtil.setCookie(cookie)
                }
                return@addInterceptor response
            }
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 变更 baseUrl，需要在初始化时设置，否则不生效
     */
    fun changeBaseUrl(url: String) {
        defaultBaseUrl = url
        retrofitMap.clear()
    }

    fun updateToken(token: String?) {
        this.token = token
    }

    /**
     * 根据 baseUrl 获取对应的 retrofit 对象
     *
     * @param baseUrl 不传或位空则返回默认的 retrofit
     * @return baseUrl 对应的retrofit对象
     */
    fun getRetrofit(baseUrl: String? = null): Retrofit {
        var url = baseUrl ?: defaultBaseUrl
        var retrofit = retrofitMap[url]
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofitMap[url] = retrofit
        }
        return retrofit!!
    }

    fun encodeCookie(cookies: List<String>): String {
        val sb = StringBuilder()
        val set = HashSet<String>()
        cookies
            .map { cookie ->
                cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            }
            .forEach {
                it.filterNot { set.contains(it) }.forEach { set.add(it) }
            }
        val ite = set.iterator()
        while (ite.hasNext()) {
            val cookie = ite.next()
            sb.append(cookie).append(";")
        }
        val last = sb.lastIndexOf(";")
        if (sb.length - 1 == last) {
            sb.deleteCharAt(last)
        }
        return sb.toString()
    }

}