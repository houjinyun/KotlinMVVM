package com.hjy.template.ui.adapter

import android.app.Activity
import android.content.Context
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.divider
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hjy.template.R
import com.hjy.template.bean.Article
import com.hjy.template.bean.Banner
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.indicator.CircleIndicator

fun buildCommonArticleListAdapter(recyclerView: RecyclerView, fragment: Fragment, onClickListener: (id: Int, data: Article) -> Unit): BindingAdapter {
    return buildCommonArticleListAdapter(recyclerView, null, fragment, onClickListener)
}


fun buildCommonArticleListAdapter(recyclerView: RecyclerView, activity: AppCompatActivity?, fragment: Fragment?, onClickListener: (id: Int, data: Article) -> Unit): BindingAdapter {
    return recyclerView.linear()
        .divider(R.drawable.horizontal_divider)
        .setup {
            val bannerAdapter: ImageAdapter by lazy { ImageAdapter(recyclerView.context) }
            var bannerData: List<Banner>? = null

            addType<Article>(R.layout.list_item_home_article)
            addType<List<Banner>>(R.layout.layout_banner)
            onCreate {
                when(it) {
                    R.layout.layout_banner -> {
                        val bannerView = itemView as com.youth.banner.Banner<Banner, ImageAdapter>
                        bannerView.addBannerLifecycleObserver(activity ?: fragment)
                            .setIndicator(CircleIndicator(recyclerView.context))
                            .setAdapter(bannerAdapter)
                            .setIndicatorNormalColorRes(R.color.grey600)
                            .setIndicatorSelectedColorRes(R.color.grey900)
                        bannerAdapter.setOnBannerListener { data, position ->
                            ARouter.getInstance().build("/test/webview")
                                .withString("url", data.url)
                                .withString("title", data.title)
                                .navigation(activity ?: fragment!!.requireActivity())
                        }
                    }
                }
            }
            onBind {
                when(itemViewType) {
                    R.layout.layout_banner -> {
                        val bannerView = itemView as com.youth.banner.Banner<Banner, ImageAdapter>
                        val currBannerList = getModel<List<Banner>>()
                        if (currBannerList != bannerData) {
                            bannerData = currBannerList
                            bannerView.setDatas(getModel<List<Banner>>())
                        }
                    }
                    R.layout.list_item_home_article -> {
                        val article = getModel<Article>()
                        findView<TextView>(R.id.tv_article_top).visibility = if (article.top == "1") View.VISIBLE else View.GONE
                        findView<TextView>(R.id.tv_article_fresh).visibility = if (article.fresh) View.VISIBLE else View.GONE
                        val author = if (article.author.isNullOrEmpty()) article.shareUser else article.author
                        findView<TextView>(R.id.tv_article_author).text = author ?: ""
                        findView<TextView>(R.id.tv_article_date).text = article.niceDate ?: ""
                        findView<TextView>(R.id.tv_article_title).text = Html.fromHtml(article.title ?: "")
                        val chapterName = when {
                            !article.superChapterName.isNullOrEmpty() and !article.chapterName.isNullOrEmpty() ->
                                "${article.superChapterName} - ${article.chapterName}"
                            !article.superChapterName.isNullOrEmpty() -> "${article.superChapterName}"
                            !article.chapterName.isNullOrEmpty() -> "${article.chapterName}"
                            else -> ""
                        }
                        val tvChapter = findView<TextView>(R.id.tv_article_chapterName)
                        tvChapter.text = chapterName
                        val envelopePic = article.envelopePic
                        val img = findView<ImageView>(R.id.iv_article_thumbnail)
                        val layoutParams = tvChapter.layoutParams as ConstraintLayout.LayoutParams
                        if (envelopePic.isNullOrEmpty()) {
                            img.visibility = View.GONE
                            layoutParams.bottomToBottom = -1
                            layoutParams.topToBottom = R.id.tv_article_title
                        } else {
                            img.visibility = View.VISIBLE
                            layoutParams.bottomToBottom = R.id.iv_article_thumbnail
                            layoutParams.topToBottom = -1
                            if (activity != null) {
                                Glide.with(activity).load(envelopePic).into(img)
                            } else {
                                Glide.with(fragment!!).load(envelopePic).into(img)
                            }
                        }
                        tvChapter.layoutParams = layoutParams
                        val tvTag = findView<TextView>(R.id.tv_article_tag)
                        if (article.tags != null && article.tags.size > 0) {
                            tvTag.visibility = View.VISIBLE
                            tvTag.text = article.tags[0].name
                        } else {
                            tvTag.visibility = View.GONE
                        }
                        findView<ImageView>(R.id.iv_like).setImageResource(
                            if (article.collect) R.mipmap.ic_like else R.mipmap.ic_not_like
                        )
                    }
                }
            }
            onClick(R.id.iv_like) {
                onClickListener(R.id.iv_like, getModel())
            }
            onClick(R.id.item) {
                onClickListener(R.id.item, getModel())
            }
        }
}

class ImageAdapter(val context: Context): BannerAdapter<Banner, ImageAdapter.BannerViewHolder>(null) {

    override fun onBindView(
        holder: BannerViewHolder?,
        data: com.hjy.template.bean.Banner?,
        position: Int,
        size: Int
    ) {
        if (holder != null && data != null) {
            Glide.with(context).load(data.imagePath).into(holder.view)
        }
    }

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerViewHolder {
        val imageView = ImageView(context)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return BannerViewHolder(imageView)
    }

    class BannerViewHolder(var view: ImageView) : RecyclerView.ViewHolder(view)
}