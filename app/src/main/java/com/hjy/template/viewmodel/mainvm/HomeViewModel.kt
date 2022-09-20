package com.hjy.template.viewmodel.mainvm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hjy.template.base.BaseViewModel
import com.hjy.template.bean.Article
import com.hjy.template.bean.Banner
import com.hjy.template.event.LoadMoreRefreshEvent
import com.hjy.template.event.PullDownRefreshEvent
import com.hjy.template.global.Constants
import com.hjy.template.global.Resource
import com.hjy.template.global.UserInfoManager
import com.hjy.template.repository.ArticleRepository
import com.hjy.template.utils.SettingUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(app: Application): BaseViewModel(app) {

    private val articleRepository = ArticleRepository()

    private val _bannerListFlow = MutableStateFlow<Resource<List<Banner>>>(Resource(null))
    val bannerListFlow = _bannerListFlow.asStateFlow()

    private val _articleListFlow = MutableStateFlow<Resource<List<Article>?>>(Resource(null))
    val articleListFlow = _articleListFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow(Resource(Constants.STATE_LOADING))
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    private val totalArticleList = mutableListOf<Article>()
    private var nextPageNum: Int = 0

    /**
     * 获取 banner 信息
     */
    fun getBannerList() {
        if (!SettingUtil.isShowBanner()) {
            _bannerListFlow.value = Resource(null)
            return
        }
        viewModelScope.launch {
            launchApiRequestFlow {
                articleRepository.getBannerList()
            }.catch {
            }.collect {
                _bannerListFlow.value = Resource(it)
            }
        }
    }

    /**
     * 获取置顶的文字以及普通文章列表
     *
     * @param isRefresh 是否下拉刷新
     */
    fun getTopArticlesAndArticleList(isRefresh: Boolean) {
        if (isRefresh) {
            viewModelScope.launch {
                launchApiRequestFlow(false) {
                    val isShowTopArticle = SettingUtil.isShowTopArticle()
                    var topArticleFlow = if (isShowTopArticle)
                        articleRepository.getTopArticles() else flow { emit(mutableListOf()) }
                    var articleListFlow = articleRepository.getArticleList(0)
                    topArticleFlow.zip(articleListFlow) { l1, l2 ->
                        l1.addAll(l2.datas)
                        l1
                    }
                }.catch {
                    postInternalPageEvent(PullDownRefreshEvent(false))
                    if (totalArticleList.isEmpty()) {
                        //如果本身没有显示任何数据，则会显示一个加载失败的布局
                        _loadingStateFlow.value = Resource(Constants.STATE_LOAD_ERROR)
                    } else {
                        //如果本事有列表数据了，下拉刷新失败，则 toast 错误信息
                        processCommonException(it)
                    }
                }.collect {
                    totalArticleList.clear()
                    totalArticleList.addAll(it)
                    if (totalArticleList.isEmpty()) {
                        _loadingStateFlow.value = Resource(Constants.STATE_EMPTY)
                    } else {
                        _loadingStateFlow.value = Resource(Constants.STATE_LOAD_SUCCESS)
                    }
                    _articleListFlow.value = Resource(totalArticleList)
                    nextPageNum = 1
                    postInternalPageEvent(PullDownRefreshEvent(true))
                }
            }
        } else {
            viewModelScope.launch {
                launchApiRequestFlow {
                    articleRepository.getArticleList(nextPageNum)
                }.catch {
                    postInternalPageEvent(LoadMoreRefreshEvent(false))
                }.collect {
                    totalArticleList.addAll(it.datas)
                    _articleListFlow.value = Resource(totalArticleList)
                    nextPageNum++
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
            launchApiRequestFlow {
                if (data.collect) articleRepository.cancelCollectArticle(data.id) else articleRepository.addCollectArticle(data.id)
            }.catch {
            }.onCompletion {
                hideLoading()
            }.collect {
                data.collect = !data.collect
                successCallback()
            }
        }
    }

}