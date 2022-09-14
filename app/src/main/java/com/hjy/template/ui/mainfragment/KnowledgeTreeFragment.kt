package com.hjy.template.ui.mainfragment

import android.util.TypedValue
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.launcher.ARouter
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.hjy.template.R
import com.hjy.template.base.BaseViewModelFragment
import com.hjy.template.bean.Knowledge
import com.hjy.template.bean.KnowledgeTreeBody
import com.hjy.template.databinding.FragmentKnowledgeTreeBinding
import com.hjy.template.global.Constants
import com.hjy.template.viewmodel.mainvm.KnowledgeTreeViewModel

class KnowledgeTreeFragment: BaseViewModelFragment<KnowledgeTreeViewModel, FragmentKnowledgeTreeBinding>() {

    private lateinit var mFirstAdapter: BindingAdapter
    private lateinit var mSecondAdapter: BindingAdapter

    override fun getViewModelClass(): Class<KnowledgeTreeViewModel> = KnowledgeTreeViewModel::class.java

    override fun getViewBinding(): FragmentKnowledgeTreeBinding = FragmentKnowledgeTreeBinding.inflate(layoutInflater, null, false)

    override fun init() {
        mBinding.stateLayout.onRefresh {
            mFragmentViewModel.getKnowledgeTreeList()
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

        mFirstAdapter = mBinding.recyclerViewFirst.linear()
            .setup {
                addType<KnowledgeTreeBody>(R.layout.list_item_category_first)
                onBind {
                    val data = getModel<KnowledgeTreeBody>()
                    val tvName = findView<TextView>(R.id.tv_category)
                    tvName.text = data.name
                    if (data.selected) {
                        tvName.setTextColor(context.getColor(R.color.grey900))
                    } else {
                        tvName.setTextColor(context.getColor(R.color.grey500))
                    }
                }
                onClick(R.id.tv_category) {
                    mFragmentViewModel.selectCategory(getModel())
                }
            }

        val flexLayoutManager = FlexboxLayoutManager(requireActivity())
        flexLayoutManager.flexWrap = FlexWrap.WRAP
        flexLayoutManager.flexDirection = FlexDirection.ROW
        mBinding.recyclerViewSecond.layoutManager = flexLayoutManager
        mSecondAdapter = mBinding.recyclerViewSecond.setup {
            addType<Knowledge>(R.layout.list_item_category_second)
            onBind {
                val data = getModel<Knowledge>()
                findView<TextView>(R.id.tv_category).text = data.name
            }
            onClick(R.id.tv_category) {
                val knowledgeTreeBody = mFragmentViewModel.getSelectedKnowledge()
                knowledgeTreeBody?.let {
                    ARouter.getInstance().build("/test/knowledgedetail")
                        .withSerializable("knowledge", it)
                        .withInt("position", modelPosition)
                        .navigation(requireActivity())
                }
            }
        }

        launchAtLeast(Lifecycle.State.RESUMED) {
            mFragmentViewModel.treeListFlow.collect {
                mFirstAdapter.models = it.data
            }
        }
        launchAtLeast(Lifecycle.State.RESUMED) {
            mFragmentViewModel.secondListFlow.collect {
                mSecondAdapter.models = it.data
            }
        }
    }

}