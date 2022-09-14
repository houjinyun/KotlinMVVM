package com.hjy.template.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hjy.template.base.BaseViewModel
import com.hjy.template.bean.CollectionArticle
import com.hjy.template.event.LoadMoreRefreshEvent
import com.hjy.template.event.PullDownRefreshEvent
import com.hjy.template.global.Constants
import com.hjy.template.global.Resource
import com.hjy.template.repository.ArticleRepository
import com.hjy.template.repository.MyCollectionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyCollectionViewModel(app: Application): BaseViewModel(app) {

    private val collectionRepository = MyCollectionRepository()
    private val articleRepository = ArticleRepository()

    private val _articleListFlow = MutableStateFlow<Resource<List<CollectionArticle>?>>(Resource(null))
    val articleListFlow = _articleListFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow(Resource(Constants.STATE_LOADING))
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    private val totalArticleList = mutableListOf<CollectionArticle>()
    private var nextPageNum: Int = 0

    /**
     * @param isRefresh 是否下拉刷新
     */
    fun getCollectionList(isRefresh: Boolean) {
        viewModelScope.launch {
            collectionRepository.getMyCollection(if (isRefresh) 0 else nextPageNum).catch {
                if (isRefresh) {
                    postInternalPageEvent(PullDownRefreshEvent(false))
                } else {
                    postInternalPageEvent(LoadMoreRefreshEvent(false))
                }
                if (totalArticleList.isEmpty()) {
                    //如果本身没有显示任何数据，则会显示一个加载失败的布局
                    _loadingStateFlow.value = Resource(Constants.STATE_LOAD_ERROR)
                } else {
                    //如果本事有列表数据了，下拉刷新失败，则 toast 错误信息
                    processCommonException(it)
                }
            }.collect {
                if (isRefresh) {
                    totalArticleList.clear()
                    nextPageNum = 1
                } else {
                    nextPageNum++
                }
                totalArticleList.addAll(it.datas)
                _articleListFlow.value = Resource(totalArticleList)

                if (totalArticleList.isEmpty()) {
                    _loadingStateFlow.value = Resource(Constants.STATE_EMPTY)
                } else {
                    _loadingStateFlow.value = Resource(Constants.STATE_LOAD_SUCCESS)
                }
                if (isRefresh) {
                    postInternalPageEvent(PullDownRefreshEvent(true))
                } else {
                    postInternalPageEvent(LoadMoreRefreshEvent(true, it.over))
                }
            }
        }
    }

    fun cancelCollect(data: CollectionArticle, successCallback: () -> Unit) {
        viewModelScope.launch {
            showLoading("数据加载中，请稍后...")
            articleRepository.cancelCollectArticle(data.id, data.originId).catch {
                processCommonException(it)
            }.onCompletion {
                hideLoading()
            }.collect {
                successCallback()
                delay(500)
                totalArticleList.remove(data)
                _articleListFlow.value = Resource(totalArticleList)
            }
        }
    }

}