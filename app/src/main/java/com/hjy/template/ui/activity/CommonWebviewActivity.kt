package com.hjy.template.ui.activity

import android.graphics.Color
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjy.template.base.BaseViewModelActivity
import com.hjy.template.databinding.ActivityCommonWebviewBinding
import com.hjy.template.viewmodel.CommonWebviewModel
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient

@Route(path = "/test/webview")
class CommonWebviewActivity: BaseViewModelActivity<CommonWebviewModel, ActivityCommonWebviewBinding>() {

    @Autowired @JvmField
    var url: String? = null

    @Autowired @JvmField
    var title: String? = null

    private lateinit var mAgentWeb: AgentWeb

    private var mWebViewClient = object : WebViewClient() {

    }

    private var mWebChromeClient = object : WebChromeClient() {

    }

    override fun isTranslucentBar(): Boolean = true

    override fun getViewModelClass(): Class<CommonWebviewModel> = CommonWebviewModel::class.java

    override fun getViewBinding(): ActivityCommonWebviewBinding = ActivityCommonWebviewBinding.inflate(layoutInflater)

    override fun init() {
        mBinding.header.setTitle(title ?: "")

        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(mBinding.llContent, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ))
            .useDefaultIndicator(Color.parseColor("#09bb07"), 2)
            .setWebViewClient(mWebViewClient)
            .setWebChromeClient(mWebChromeClient)
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
            .createAgentWeb()
            .ready()
            .go(url)
        mBinding.header.setBackIconClickListener {
            if (!mAgentWeb.back()) {
                finish()
            }
        }
    }

    override fun onResume() {
        mAgentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onPause() {
        mAgentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mAgentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }


}