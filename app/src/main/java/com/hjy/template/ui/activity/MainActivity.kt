package com.hjy.template.ui.activity

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.ashokvarma.bottomnavigation.ShapeBadgeItem
import com.ashokvarma.bottomnavigation.TextBadgeItem
import com.hjy.template.R
import com.hjy.template.base.BaseViewModelActivity
import com.hjy.template.databinding.ActivityMainBinding
import com.hjy.template.event.ShowTabBadgeEvent
import com.hjy.template.event.subscribeEvent
import com.hjy.template.ui.mainfragment.HomeFragment
import com.hjy.template.ui.mainfragment.MineFragment
import com.hjy.template.ui.mainfragment.KnowledgeTreeFragment
import com.hjy.template.ui.mainfragment.QuestionFragment
import com.hjy.template.viewmodel.MainViewModel

@Route(path = "/test/main")
class MainActivity : BaseViewModelActivity<MainViewModel, ActivityMainBinding>() {

    @Autowired
    @JvmField
    var tabIndex: Int? = 0

    private lateinit var mAdapter: MainStateAdapter
    private lateinit var mMineBadge: ShapeBadgeItem
//    private lateinit var mCartBadge: TextBadgeItem

    override fun isTranslucentBar(): Boolean = true

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.getIntExtra("tabIndex", -1) != -1)
            mBinding.bottomNavigationBar.selectTab(tabIndex ?: 0)
    }

    override fun init() {
        initViewPage()
        initBottomTabBar()
        initData()
    }

    private fun initViewPage() {
        mAdapter = MainStateAdapter(supportFragmentManager)
        //????????????????????? ViewPager2??????????????? ViewPager ???????????? RecyclerView?????????????????????????????????
        mBinding.viewpagerContent.adapter = mAdapter
        mBinding.viewpagerContent.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                mBinding.bottomNavigationBar.selectTab(position, false)
            }

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
        })
    }

    private fun initBottomTabBar() {
        val tabTitles = resources.getStringArray(R.array.main_tab_array)
//        mCartBadge = TextBadgeItem().setBorderWidth(1)
        mMineBadge = ShapeBadgeItem().setShape(ShapeBadgeItem.SHAPE_OVAL).setSizeInDp(this, 5, 5)
        mBinding.bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED)
            .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
            .addItem(BottomNavigationItem(R.mipmap.ic_tab_home_normal, tabTitles[0]))
            .addItem(BottomNavigationItem(R.mipmap.ic_tab_product_normal, tabTitles[1]))
            .addItem(BottomNavigationItem(R.mipmap.ic_tab_cart_normal, tabTitles[2]))
            .addItem(BottomNavigationItem(R.mipmap.ic_tab_mine_normal, tabTitles[3]).setBadgeItem(
                mMineBadge
            ))
            .setInActiveColor(R.color.tab_inactive_color)
            .setActiveColor(R.color.tab_active_color)
            .setBarBackgroundColor(R.color.tab_bar_background_color)
            .setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
                override fun onTabReselected(position: Int) {
                    mBinding.viewpagerContent.setCurrentItem(position, false)
                }

                override fun onTabSelected(position: Int) {
                    mBinding.viewpagerContent.setCurrentItem(position, false)
                }

                override fun onTabUnselected(position: Int) {
                }
            })
            .initialise()
        mBinding.bottomNavigationBar.selectTab(tabIndex ?: 0)
        mBinding.viewpagerContent.offscreenPageLimit = 4
        mBinding.viewpagerContent.currentItem = mBinding.bottomNavigationBar.currentSelectedPosition
    }

    private fun initData() {
        //?????? tab ??????????????????
        mActivityViewModel.calcUnreadCount()

        //?????????????????????????????? badge
        subscribeEvent<ShowTabBadgeEvent> {
            //???????????????????????? tab ?????????????????????????????????????????????
            mActivityViewModel.calcUnreadCount()
        }
        launchAtLeast(Lifecycle.State.RESUMED) {
            mActivityViewModel.mineShowRedDotFlow.collect {
                if (it) {
                    mMineBadge.show()
                } else {
                    mMineBadge.hide()
                }
            }
        }
    }

}

class MainStateAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager,
    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = listOf(
        HomeFragment(),
        KnowledgeTreeFragment(),
        QuestionFragment(),
        MineFragment()
    )

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
}