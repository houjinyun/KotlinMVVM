package com.hjy.template.ui.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hjy.template.base.BaseViewModel
import com.hjy.template.base.BaseViewModelActivity
import com.hjy.template.bean.Knowledge
import com.hjy.template.bean.KnowledgeTreeBody
import com.hjy.template.databinding.ActivityKnowledgePageBinding
import com.hjy.template.ui.fragment.KnowledgePageListFragment

@Route(path = "/test/knowledgedetail")
class KnowledgePageActivity: BaseViewModelActivity<BaseViewModel, ActivityKnowledgePageBinding>() {

    @Autowired
    @JvmField
    var knowledge: KnowledgeTreeBody? = null

    @Autowired
    @JvmField
    var position: Int = 0

    private val viewPagerAdapter: KnowledgePageAdapter by lazy {
        KnowledgePageAdapter(this, knowledge?.children ?: listOf())
    }

    override fun isTranslucentBar(): Boolean = true

    override fun getViewModelClass(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun getViewBinding(): ActivityKnowledgePageBinding = ActivityKnowledgePageBinding.inflate(layoutInflater)

    override fun init() {
        mBinding.viewpagerContent.run {
            adapter = viewPagerAdapter
        }
        mBinding.tabLayout.run {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        mBinding.viewpagerContent.currentItem = it.position
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }
            })
        }
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewpagerContent) { tab, position ->
            tab.text = viewPagerAdapter.list[position].name
        }.attach()

        mBinding.viewpagerContent.currentItem = position
    }

    class KnowledgePageAdapter(activity: FragmentActivity, val list: List<Knowledge>): FragmentStateAdapter(activity) {

        private val fragments = mutableListOf<KnowledgePageListFragment>()

        init {
            list.forEach {
                fragments.add(KnowledgePageListFragment.newInstance(it.id))
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

    }

}