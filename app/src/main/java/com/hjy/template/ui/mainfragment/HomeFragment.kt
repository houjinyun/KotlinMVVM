package com.hjy.template.ui.mainfragment

import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.launcher.ARouter
import com.drake.brv.BindingAdapter
import com.hjy.template.R
import com.hjy.template.base.BaseViewModelFragment
import com.hjy.template.databinding.FragmentHomeBinding
import com.hjy.template.event.*
import com.hjy.template.global.Constants
import com.hjy.template.global.UserInfoManager
import com.hjy.template.ui.adapter.buildCommonArticleListAdapter
import com.hjy.template.viewmodel.mainvm.HomeViewModel


class HomeFragment: BaseViewModelFragment<HomeViewModel, FragmentHomeBinding>() {

    private lateinit var mListAdapter: BindingAdapter

    override fun getViewModelClass(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater, null, false)

    override fun init() {
        mBinding.header.setRightMenuIcon(R.mipmap.ic_search)
        mBinding.header.setRightMenuClickListener {
            ARouter.getInstance().build("/test/search").navigation(requireActivity())
        }

        initArticleList()

        subscribeEventAtLeast<BaseEvent.LoginEvent> {
            mBinding.refreshLayout.autoRefresh()
        }

        subscribeEventAtLeast<BaseEvent.LogoutEvent> {
            mBinding.refreshLayout.autoRefresh()
        }
        //粘性事件
        subscribeEventAtLeast<ShowBannerEvent>(Lifecycle.State.RESUMED, true) {
            mBinding.recyclerView.layoutManager?.scrollToPosition(0)
            if (it.isShow) {
                //重新刷新一下数据
                mBinding.refreshLayout.autoRefresh()
            } else {
                //隐藏 banner
                mListAdapter.clearHeader()
                mBinding.refreshLayout.autoRefresh()
            }
            //粘性事件，收到之后立马清除掉
            clearStickyEvent<ShowBannerEvent>()
        }
        subscribeEventAtLeast<ShowTopArticleEvent>(Lifecycle.State.RESUMED, true) {
            mBinding.recyclerView.layoutManager?.scrollToPosition(0)
            mBinding.refreshLayout.autoRefresh()
            //粘性事件，收到之后立马清除掉
            clearStickyEvent<ShowTopArticleEvent>()
        }
    }

    private fun initArticleList() {
        mListAdapter = buildCommonArticleListAdapter(mBinding.recyclerView, this) { id, data ->
            when(id) {
                R.id.item -> {
                    ARouter.getInstance().build("/test/webview")
                        .withString("url", data.link)
                        .withString("title", data.title)
                        .navigation(requireActivity())
                }
                R.id.iv_like -> {
                    if (UserInfoManager.isLogin()) {
                        mFragmentViewModel.collectOrCancelCollectArticle(data) {
                            mListAdapter.notifyDataSetChanged()
                        }
                    } else {
                        ARouter.getInstance().build("/test/login").navigation(requireActivity())
                    }
                }
            }
        }

        mBinding.refreshLayout.setOnRefreshListener {
            //下拉刷新中...
            mFragmentViewModel.getBannerList()
            mFragmentViewModel.getTopArticlesAndArticleList(true)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            //加载更多中...
            mFragmentViewModel.getTopArticlesAndArticleList(false)
        }

        mBinding.refreshLayout.setEnableLoadMoreWhenContentNotFull(false)

        //顶部 banner
        launchAtLeast(Lifecycle.State.RESUMED) {
            mFragmentViewModel.bannerListFlow.collect {
                if (!it.data.isNullOrEmpty()) {
                    mListAdapter.clearHeader(false)
                    mListAdapter.addHeader(it.data)
                } else {
                    mListAdapter.clearHeader(false)
                }
            }
        }

        //设置数据
        launchAtLeast(Lifecycle.State.RESUMED) {
            mFragmentViewModel.articleListFlow.collect {
                mListAdapter.models = it.data
            }
        }

        //下来刷新结束
        subscribeInternalPageEvent<PullDownRefreshEvent> {
            mBinding.refreshLayout.finishRefresh(0, it.isSuccess, false)
        }
        //加载更多
        subscribeInternalPageEvent<LoadMoreRefreshEvent> {
            mBinding.refreshLayout.finishLoadMore(30, it.isSuccess, it.noMoreData)
        }

        //获取文章列表数据
        mBinding.stateLayout.onRefresh {
            mFragmentViewModel.getBannerList()
            mFragmentViewModel.getTopArticlesAndArticleList(true)
        }

        launchAtLeast(Lifecycle.State.RESUMED) {
            mFragmentViewModel.loadingStateFlow.collect {
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