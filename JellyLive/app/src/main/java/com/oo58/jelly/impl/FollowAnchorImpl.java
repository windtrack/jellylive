package com.oo58.jelly.impl;

/**
 * Desc: 关注 的监听
 * Created by sunjinfang on 2016/6/21 18:38.
 */
public interface FollowAnchorImpl {

    public void followSuccess(boolean add,String uid) ;
    public void followFailed(String tips) ;

}
