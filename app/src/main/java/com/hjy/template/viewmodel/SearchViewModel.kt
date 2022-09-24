package com.hjy.template.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hjy.template.base.BaseViewModel
import com.hjy.template.bean.Article
import com.hjy.template.bean.HotSearchBean
import com.hjy.template.event.LoadMoreRefreshEvent
import com.hjy.template.global.Constants
import com.hjy.template.global.Resource
import com.hjy.template.global.UserInfoManager
import com.hjy.template.repository.ArticleRepository
import com.hjy.template.repository.DbRepository
import com.hjy.template.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(app: Application): BaseViewModel(app) {

    private val searchRepository = SearchRepository()
    private val articleRepository = ArticleRepository()
    private val dbRepository = DbRepository(app)

    private val _articleListFlow = MutableStateFlow<Resource<List<Article>?>>(Resource(null))
    val articleListFlow = _articleListFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow(Resource(Constants.STATE_LOAD_SUCCESS))
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    private val _hotSearchFlow = MutableStateFlow<Resource<List<HotSearchBean>>>(Resource(null))
    val hotSearchFlow = _hotSearchFlow.asStateFlow()

    private val totalArticleList = mutableListOf<Article>()
    private var keyword: String = ""
    private var nextPageNum: Int = 0

    private var searchJob: Job? = null

    init {
        //查询缓存，优先显示缓存
        viewModelScope.launch {
            dbRepository.getAllHotSearchListByFlow()
                .catch { it.printStackTrace() }
                .collect {
                    _hotSearchFlow.value = Resource(it)
                }
        }
        viewModelScope.launch {
            launchApiRequestFlow(false) {
                searchRepository.getHotSearch()
            }.catch {
                it.printStackTrace()
            }.map {
//                _hotSearchFlow.value = Resource(it)
                dbRepository.deleteAllHotSearchData()
                dbRepository.insertHotSearch(it)
            }.flowOn(Dispatchers.IO).catch { }.collect {}
        }
    }

    fun searchByKeyword(key: String) {
        if (key.isEmpty()) {
            _loadingStateFlow.value = Resource(Constants.STATE_LOAD_SUCCESS)
            totalArticleList.clear()
            _articleListFlow.value = Resource(totalArticleList)
            return
        }
        searchJob?.cancel()
        keyword = key
        searchJob = viewModelScope.launch {
            launchApiRequestFlow(false) {
                searchRepository.searchByKey(0, key)
            }.catch {
                _loadingStateFlow.value = Resource(Constants.STATE_LOAD_ERROR)
            }.collect {
                val list = it.datas
                totalArticleList.clear()
                totalArticleList.addAll(list)
                _articleListFlow.value = Resource(totalArticleList)
                if (totalArticleList.isEmpty()) {
                    _loadingStateFlow.value = Resource(Constants.STATE_EMPTY)
                } else {
                    _loadingStateFlow.value = Resource(Constants.STATE_LOAD_SUCCESS)
                }
                nextPageNum = 1
            }
        }
    }

    fun loadMore() {
        searchJob = viewModelScope.launch {
            launchApiRequestFlow {
                searchRepository.searchByKey(nextPageNum, keyword)
            }.catch {
                postInternalPageEvent(LoadMoreRefreshEvent(false))
            }.collect {
                val list = it.datas
                totalArticleList.addAll(list)
                _articleListFlow.value = Resource(totalArticleList)
                nextPageNum++
                postInternalPageEvent(LoadMoreRefreshEvent(true, it.over))
            }
        }
    }

    fun collectOrCancelCollectArticle(data: Article, successCallback: () -> Unit) {
        if (!UserInfoManager.isLogin()) {
            toastShort("请先登录")
            return
        }
        viewModelScope.launch {
            showLoading("数据加载中，请稍后...")
            launchApiRequestFlow {
                if (data.collect) articleRepository.cancelCollectArticle(data.id) else articleRepository.addCollectArticle(
                    data.id
                )
            }.catch {
                processCommonException(it)
            }.onCompletion {
                hideLoading()
            }.collect {
                data.collect = !data.collect
                successCallback()
            }
        }
    }

}