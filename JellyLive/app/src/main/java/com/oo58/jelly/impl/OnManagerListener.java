package com.oo58.jelly.impl;

import com.oo58.jelly.entity.UserVo;

/**
 * Desc: 管理面板监听
 * Created by sunjinfang on 2016/6/23 10:54.
 */
public interface OnManagerListener {
    public void onSetHelper(UserVo userVo);
    public void onShowHelperList(UserVo userVo);
    public void onShoutUp(UserVo userVo);
    public void onReport(UserVo userVo);

}
