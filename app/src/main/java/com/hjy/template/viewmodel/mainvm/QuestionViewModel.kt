package com.hjy.template.viewmodel.mainvm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hjy.template.base.BaseViewModel
import com.hjy.template.bean.Article
import com.hjy.template.event.LoadMoreRefreshEvent
import com.hjy.template.event.PullDownRefreshEvent
import com.hjy.template.global.Constants
import com.hjy.template.global.Resource
import com.hjy.template.global.UserInfo
import com.hjy.template.global.UserInfoManager
import com.hjy.template.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class QuestionViewModel(app: Application): BaseViewModel(app) {

    private val articleRepository = ArticleRepository()

    private val _articleListFlow = MutableStateFlow<Resource<List<Article>?>>(Resource(null))
    val articleListFlow = _articleListFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow(Resource(Constants.STATE_LOADING))
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    private val totalArticleList = mutableListOf<Article>()
    private var nextPageNum: Int = 0

    fun getQuestionList(isRefresh: Boolean) {
        viewModelScope.launch {
            articleRepository.getQuestionList(if (isRefresh) 0 else nextPageNum).catch {
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

    fun collectOrCancelCollectArticle(data: Article, successCallback: () -> Unit) {
        if (!UserInfoManager.isLogin()) {
            toastShort("请先登录")
            return
        }
        viewModelScope.launch {
            showLoading("数据加载中，请稍后...")
            var flow = if (data.collect) articleRepository.cancelCollectArticle(data.id) else articleRepository.addCollectArticle(data.id)
            flow.catch {
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