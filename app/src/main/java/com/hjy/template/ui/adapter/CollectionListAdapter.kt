package com.hjy.template.ui.adapter

import android.app.Activity
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.divider
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hjy.template.R
import com.hjy.template.bean.Article
import com.hjy.template.bean.CollectionArticle

fun buildCollectionListAdapter(recyclerView: RecyclerView, activity: Activity, onClickListener: (id: Int, data: CollectionArticle, position: Int) -> Unit): BindingAdapter {
    return recyclerView.linear()
        .divider(R.drawable.horizontal_divider)
        .setup {
            addType<CollectionArticle>(R.layout.list_item_collection_article)
            onBind {
                val article = getModel<CollectionArticle>()
                val author = if (article.author.isNullOrEmpty()) "转载" else article.author
                findView<TextView>(R.id.tv_article_author).text = author
                findView<TextView>(R.id.tv_article_date).text = article.niceDate ?: ""
                findView<TextView>(R.id.tv_article_title).text = Html.fromHtml(article.title ?: "")
                val tvChapter = findView<TextView>(R.id.tv_article_chapterName)
                tvChapter.text = article.chapterName
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
                    Glide.with(activity).load(envelopePic).into(img)
                }
                tvChapter.layoutParams = layoutParams
            }
            onClick(R.id.iv_like) {
                onClickListener(R.id.iv_like, getModel(), modelPosition)
            }
            onClick(R.id.item) {
                onClickListener(R.id.item, getModel(), modelPosition)
            }
        }
}