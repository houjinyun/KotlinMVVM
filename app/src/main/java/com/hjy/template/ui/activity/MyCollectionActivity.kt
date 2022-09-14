package com.hjy.template.ui.activity

import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.drake.brv.BindingAdapter
import com.hjy.template.R
import com.hjy.template.base.BaseViewModelActivity
import com.hjy.template.databinding.ActivityMyCollectionBinding
import com.hjy.template.event.LoadMoreRefreshEvent
import com.hjy.template.event.PullDownRefreshEvent
import com.hjy.template.global.Constants
import com.hjy.template.ui.adapter.buildCollectionListAdapter
import com.hjy.template.viewmodel.MyCollectionViewModel

@Route(path = "/test/mycollection")
class MyCollectionActivity: BaseViewModelActivity<MyCollectionViewModel, ActivityMyCollectionBinding>() {

    private lateinit var mListAdapter: BindingAdapter

    override fun getViewModelClass(): Class<MyCollectionViewModel> = MyCollectionViewModel::class.java

    override fun getViewBinding(): ActivityMyCollectionBinding = ActivityMyCollectionBinding.inflate(layoutInflater)

    override fun isTranslucentBar(): Boolean = true

    override fun init() {
        initMyCollectionList()
    }

    private fun initMyCollectionList() {
        mListAdapter = buildCollectionListAdapter(mBinding.recyclerView, this) { id, data, pos ->
            when(id) {
                R.id.item -> {
                    ARouter.getInstance().build("/test/webview")
                        .withString("url", data.link)
                        .withString("title", data.title)
                        .navigation(this)
                }
                R.id.iv_like -> {
                    mActivityViewModel.cancelCollect(data) {
                        //执行删除动画
                        mListAdapter.notifyItemRemoved(pos)
                    }
                }
            }
        }
        mBinding.refreshLayout.setOnRefreshListener {
            //下拉刷新中...
            mActivityViewModel.getCollectionList(true)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            //加载更多中...
            mActivityViewModel.getCollectionList(false)
        }

        mBinding.refreshLayout.setEnableLoadMoreWhenContentNotFull(false)

        //设置数据
        launchAtLeast(Lifecycle.State.RESUMED) {
            mActivityViewModel.articleListFlow.collect {
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
            mActivityViewModel.getCollectionList(true)
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