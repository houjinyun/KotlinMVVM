package com.hjy.template.ui.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjy.template.R
import com.hjy.template.base.BaseViewModelActivity
import com.hjy.template.databinding.ActivityCommonWebviewBinding
import com.hjy.template.utils.ShareUtil
import com.hjy.template.viewmodel.CommonWebviewModel
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.lxj.xpopup.XPopup
import java.lang.Exception

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

        val icMore = getDrawable(R.mipmap.ic_points_more)
        icMore?.run {
            setTint(getColor(R.color.grey800))
            mBinding.header.setRightMenuIcon(this)
        }
        mBinding.header.setRightMenuClickListener {
            XPopup.Builder(this)
                .hasShadowBg(false)
                .animationDuration(200)
                .atView(it)
                .isDestroyOnDismiss(true)
                .offsetX((resources.displayMetrics.density * 16).toInt())
                .asAttachList(arrayOf("分享", "浏览器打开"), null) { position, _ ->
                    when(position) {
                        0 -> {
                            val text = "【玩Android】分享[${title}]:${url}"
                            val title = "分享[$title]"
                            ShareUtil.shareUrl(this, text, title)
                        }
                        1 -> {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                .show()
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