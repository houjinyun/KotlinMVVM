package com.hjy.template.ui.activity

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.setup
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.hjy.template.R
import com.hjy.template.base.BaseViewModelActivity
import com.hjy.template.bean.HotSearchBean
import com.hjy.template.databinding.ActivitySearchBinding
import com.hjy.template.event.LoadMoreRefreshEvent
import com.hjy.template.global.Constants
import com.hjy.template.global.UserInfoManager
import com.hjy.template.ui.adapter.buildCommonArticleListAdapter
import com.hjy.template.utils.WindowUtil
import com.hjy.template.viewmodel.SearchViewModel

@Route(path = "/test/search")
class SearchActivity:BaseViewModelActivity<SearchViewModel, ActivitySearchBinding>() {

    private lateinit var mListAdapter: BindingAdapter

    override fun isTranslucentBar(): Boolean {
        return true
    }

    override fun getViewModelClass(): Class<SearchViewModel> = SearchViewModel::class.java

    override fun getViewBinding(): ActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)

    override fun init() {
        mBinding.ivBack.setOnClickListener {
            finish()
        }
        mBinding.tvSearch.setOnClickListener {
            val key = mBinding.etSearch.text.toString().trim()
            if (key.isNullOrEmpty()) {
                return@setOnClickListener
            }
            mBinding.hotsearch.visibility = View.GONE
            WindowUtil.toggleSoftKeyboard(window, it, false)
            mActivityViewModel.searchByKeyword(mBinding.etSearch.text.toString().trim())
        }
        mBinding.etSearch.requestFocus()
        WindowUtil.toggleSoftKeyboard(window, mBinding.etSearch, true)
        mBinding.etSearch.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when(actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val key = mBinding.etSearch.text.toString().trim()
                    if (key.isNullOrEmpty()) {
                        return@setOnEditorActionListener false
                    }
                    WindowUtil.toggleSoftKeyboard(window, v, false)
                    mBinding.hotsearch.visibility = View.GONE
                    mActivityViewModel.searchByKeyword(key)
                    true
                }
                else -> false
            }
        }

        initHotSearch()
        initArticleList()
    }

    private fun initHotSearch() {
        val flexLayoutManager = FlexboxLayoutManager(this)
        flexLayoutManager.flexWrap = FlexWrap.WRAP
        flexLayoutManager.flexDirection = FlexDirection.ROW
        mBinding.hotsearch.layoutManager = flexLayoutManager
        val adapter = mBinding.hotsearch.setup {
            addType<HotSearchBean>(R.layout.list_item_category_second)
            onBind {
                val data = getModel<HotSearchBean>()
                findView<TextView>(R.id.tv_category).text = data.name
            }
            onClick(R.id.tv_category) {
                val data = getModel<HotSearchBean>()
                mBinding.hotsearch.visibility = View.GONE
                mActivityViewModel.searchByKeyword(data.name)
            }
        }
        launchAtLeast(Lifecycle.State.RESUMED) {
            mActivityViewModel.hotSearchFlow.collect {
                adapter.models = it.data
            }
        }
    }

    private fun initArticleList() {
        mListAdapter = buildCommonArticleListAdapter(mBinding.recyclerView, this, null) { id, data ->
            when(id) {
                R.id.item -> {
                    ARouter.getInstance().build("/test/webview")
                        .withString("url", data.link)
                        .withString("title", data.title)
                        .navigation(this)
                }
                R.id.iv_like -> {
                    if (UserInfoManager.isLogin()) {
                        mActivityViewModel.collectOrCancelCollectArticle(data) {
                            mListAdapter.notifyDataSetChanged()
                        }
                    } else {
                        ARouter.getInstance().build("/test/login").navigation(this)
                    }
                }
            }
        }

        mBinding.refreshLayout.setEnableRefresh(false)
        mBinding.refreshLayout.setOnLoadMoreListener {
            mActivityViewModel.loadMore()
        }

        //设置数据
        launchAtLeast(Lifecycle.State.RESUMED) {
            mActivityViewModel.articleListFlow.collect {
                mListAdapter.models = it.data
            }
        }

        //加载更多
        subscribeInternalPageEvent<LoadMoreRefreshEvent> {
            mBinding.refreshLayout.finishLoadMore(30, it.isSuccess, it.noMoreData)
        }

        mBinding.stateLayout.onRefresh {
            mActivityViewModel.searchByKeyword(mBinding.etSearch.text.toString().trim())
        }

        launchAtLeast(Lifecycle.State.RESUMED) {
            mActivityViewModel.loadingStateFlow.collect {
                when (it.data) {
                    Constants.STATE_LOADING -> {
                        mBinding.stateLayout.showLoading()
                    }
                    Constants.STATE_LOAD_SUCCESS -> {
                        mBinding.stateLayout.showContent()
                    }
                    Constants.STATE_LOAD_ERROR -> {
                        mBinding.stateLayout.showError()
                    }
                    Constants.STATE_EMPTY -> {
                        mBinding.stateLayout.showEmpty()
                    }
                }
            }
        }
    }

}