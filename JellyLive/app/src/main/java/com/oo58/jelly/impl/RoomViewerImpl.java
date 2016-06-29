package com.oo58.jelly.impl;

import com.oo58.jelly.entity.UserVo;

import java.util.List;

/**
 * Desc: 获取观众接口
 * Created by sunjinfang on 2016/6/21 17:18.
 */
public interface RoomViewerImpl {
    public void sendAllView(List<UserVo> list) ;
}
