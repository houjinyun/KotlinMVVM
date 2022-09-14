package com.hjy.template.ui.mainfragment

import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.launcher.ARouter
import com.drake.brv.BindingAdapter
import com.hjy.template.R
import com.hjy.template.base.BaseViewModelFragment
import com.hjy.template.databinding.FragmentQuestionBinding
import com.hjy.template.event.LoadMoreRefreshEvent
import com.hjy.template.event.PullDownRefreshEvent
import com.hjy.template.global.Constants
import com.hjy.template.global.UserInfoManager
import com.hjy.template.ui.adapter.buildCommonArticleListAdapter
import com.hjy.template.viewmodel.mainvm.QuestionViewModel

class QuestionFragment: BaseViewModelFragment<QuestionViewModel, FragmentQuestionBinding>() {

    private lateinit var mListAdapter: BindingAdapter

    override fun getViewModelClass(): Class<QuestionViewModel> = QuestionViewModel::class.java

    override fun getViewBinding(): FragmentQuestionBinding = FragmentQuestionBinding.inflate(layoutInflater, null, false)

    override fun init() {
        initArticleList()
    }

    private fun initArticleList() {
        mListAdapter = buildCommonArticleListAdapter(mBinding.recyclerView, this) { id, data ->
            when(id) {
                R.id.item -> {
                    ARouter.getInstance().build("/test/webview")
                        .withString("url", data.link)
                        .withString("title", data.
                        title)
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
            mFragmentViewModel.getQuestionList(true)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            //加载更多中...
            mFragmentViewModel.getQuestionList(false)
        }

        mBinding.refreshLayout.setEnableLoadMoreWhenContentNotFull(false)

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

        mBinding.stateLayout.onRefresh {
            mFragmentViewModel.getQuestionList(true)
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