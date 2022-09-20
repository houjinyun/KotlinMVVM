package com.hjy.template.bean

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class ResponseData<T>(
    var errorCode: Int,
    var errorMsg: String,
    var data: T
)

data class Banner(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)

data class Article(
    val apkLink: String?,
    val audit: Int,
    val author: String?,
    val chapterId: Int,
    val chapterName: String?,
    var collect: Boolean,
    val courseId: Int,
    val desc: String?,
    val envelopePic: String?,
    val fresh: Boolean,
    val id: Int,
    val link: String?,
    val niceDate: String?,
    val niceShareDate: String?,
    val origin: String?,
    val prefix: String?,
    val projectLink: String?,
    val publishTime: Long,
    val shareDate: String?,
    val shareUser: String?,
    val superChapterId: Int,
    val superChapterName: String?,
    val tags: MutableList<Tag>?,
    val title: String?,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int,
    var top: String?
)

data class Tag(
    val name: String?,
    val url: String?
)

//文章
data class ArticleResponseBody(
    val curPage: Int,
    var datas: MutableList<Article>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

data class LoginData(
    val chapterTops: MutableList<String>,
    val collectIds: MutableList<String>,
    val email: String,
    val icon: String,
    val id: Int,
    val password: String,
    val token: String,
    val type: Int,
    val username: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        mutableListOf<String>(),
        mutableListOf<String>(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(icon)
        parcel.writeInt(id)
        parcel.writeString(password)
        parcel.writeString(token)
        parcel.writeInt(type)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginData> {
        override fun createFromParcel(parcel: Parcel): LoginData {
            return LoginData(parcel)
        }

        override fun newArray(size: Int): Array<LoginData?> {
            return arrayOfNulls(size)
        }
    }

}

data class BaseListResponseBody<T>(
    val curPage: Int,
    val datas: MutableList<T>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

data class CollectionArticle(
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val id: Int,
    val link: String,
    val niceDate: String,
    val origin: String,
    val originId: Int,
    val publishTime: Long,
    val title: String,
    val userId: Int,
    val visible: Int,
    val zan: Int
)

data class KnowledgeTreeBody(
    val children: MutableList<Knowledge>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int,
    var selected: Boolean = false
): Serializable

data class Knowledge(
    val children: List<Any>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int
): Serializable

data class HotSearchBean(
    val id: Int,
    val link: String,
    val name: String,
    val order: Int,
    val visible: Int
)