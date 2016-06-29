package com.oo58.jelly.view.listview;

/**
 * @author zhongxf
 * @Description下拉刷新和加载更多的回调方法
 * @Date 2016/6/14.
 */
public interface OnRefreshListener {
    /**
     * 下拉刷新
     */
    void onDownPullRefresh();

    /**
     * 上拉加载
     */
    void onLoadingMore();
}

