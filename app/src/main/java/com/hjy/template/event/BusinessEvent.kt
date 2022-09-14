package com.hjy.template.event


/**
 * 是否显示tab页上的 badge
 */
data class ShowTabBadgeEvent(val isShow: Boolean): BaseEvent("showbadge")

//下拉刷新成功或者失败事件通知
data class PullDownRefreshEvent(val isSuccess: Boolean): BaseEvent("pulldown")

//加载更多成功或者失败事件通知
data class LoadMoreRefreshEvent(val isSuccess: Boolean, val noMoreData: Boolean = false): BaseEvent("loadmore")

data class ShowBannerEvent(val isShow: Boolean): BaseEvent("showbanner")
data class ShowTopArticleEvent(val isShow: Boolean): BaseEvent("showtoparticle")