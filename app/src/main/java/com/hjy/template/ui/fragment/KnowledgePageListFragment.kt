package com.hjy.template.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.launcher.ARouter
import com.drake.brv.BindingAdapter
import com.hjy.template.R
import com.hjy.template.base.BaseViewModelFragment
import com.hjy.template.databinding.FragmentKnowledgePageListBinding
import com.hjy.template.event.LoadMoreRefreshEvent
import com.hjy.template.event.PullDownRefreshEvent
import com.hjy.template.global.Constants
import com.hjy.template.global.UserInfoManager
import com.hjy.template.ui.adapter.buildCommonArticleListAdapter
import com.hjy.template.viewmodel.KnowledgePageListViewModel

class KnowledgePageListFragment: BaseViewModelFragment<KnowledgePageListViewModel, FragmentKnowledgePageListBinding>() {

    private lateinit var mListAdapter: BindingAdapter

    companion object {
        fun newInstance(cid: Int): KnowledgePageListFragment {
            val fragment = KnowledgePageListFragment()
            var args = Bundle()
            args.putInt("cid", cid)
            fragment.arguments = args
            return fragment
        }
    }

    private var cid = 0

    override fun getViewModelClass(): Class<KnowledgePageListViewModel> = KnowledgePageListViewModel::class.java

    override fun getViewBinding(): FragmentKnowledgePageListBinding = FragmentKnowledgePageListBinding.inflate(layoutInflater)

    override fun init() {
        cid = arguments?.getInt("cid") ?: 0
        initKnowledgeList()
    }

    private fun initKnowledgeList() {
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
            mFragmentViewModel.getKnowledgeList(cid, true)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            //加载更多中...
            mFragmentViewModel.getKnowledgeList(cid, false)
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
            mFragmentViewModel.getKnowledgeList(cid, true)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("HJY: onViewCreated() cid = $cid")

    }

    override fun onStart() {
        super.onStart()
        println("HJY: onStart() cid = $cid")
    }

    override fun onResume() {
        super.onResume()
        println("HJY: onResume() cid = $cid")
    }

    override fun onPause() {
        super.onPause()
        println("HJY: onPause() cid = $cid")

    }

    override fun onStop() {
        super.onStop()
        println("HJY: onStop() cid = $cid")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("HJY: onDestroyView() cid = $cid")

    }

    override fun onDestroy() {
        super.onDestroy()
        println("HJY: onDestroy() cid = $cid")
    }

}