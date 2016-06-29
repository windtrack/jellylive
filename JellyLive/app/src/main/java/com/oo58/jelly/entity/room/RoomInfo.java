package com.oo58.jelly.entity.room;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Desc:
 * Created by sunjinfang on 2016/6/16 15:30.
 */
public class RoomInfo {

    public final static int HandlerCmd_GetRoomInfo_Success = 20001 ;
    public final static int HandlerCmd_GetRoomInfo_Failed = 20002 ;
    public final static int HandlerCmd_GetRoomInfo_Error = 20003 ;


    public int state ;//4直播
    public String roomName ;//房间名
    public String anchor_name ;//主播名称
    public String anchorid ;//房间id 和主播id 同一个
    public int onlineNum;//在线人数
    public int hotNum;//热度
    public int is_guard; //性别
    public int followNum; //关注
    public int fansNum; //粉丝数
    public String anchor_head_iconUrl;//头像地址
    public String anchorPostUrl ;
    public String chat_url; //聊天服务器
    public int port;
    public String live_url;//观看或推流地址

    public String roomid ;
    public boolean isFollow;//是否关注

    public String openkey ;
    public String timestamp;//时间戳
    public String secert;//秘钥
    public String openid;//
    public boolean isHelper;//是否是管理员
    public boolean isShutUp;//是否被禁言


    public void getRoomInfo(final Context mContext, final Handler mHandler,final String sroomid) {
        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    String uid = GlobalData.getUID(mContext) ;
                    String secerts = GlobalData.getUSecert(mContext) ;
                    String idd = "202842" ;
                    String result = Util.addRpcEvent(RpcEvent.GetRoomInfo,uid,secerts,idd);

                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONObject data = obj.getJSONObject("data");
                        JSONObject room = data.getJSONObject("room");
                        JSONObject anchor = data.getJSONObject("anchor");
                        JSONObject video_info = data.getJSONObject("video_info");
                        JSONObject auth = data.getJSONObject("auth");

                        // 房间信息
                        String room_id = room.getString("id") ;
                        String roomname = room.getString("name") ;
                        String roomposter_url = room.getString("poster_url") ;
                        int roomstatus = room.getInt("status") ;
                        int roomOnline = room.getInt("count") ; // 人数

                        //主播信息
                        String anchor_id = anchor.getString("id");
                        String anchorid_nickname = anchor.getString("nickname");
                        String anchorid_icon = AppUrl.USER_LOGO_ROOT+anchor.getString("icon");
                        int anchorid_gender = anchor.getInt("gender");
                        int anchorid_fans_num = anchor.getInt("fans_num");
                        int anchorid_is_followed = anchor.getInt("is_followed");
                        int anchorid_hot = anchor.getInt("coins"); //热度   主播收到的乐豆
                        int anchorid_helper = anchor.getInt("is_helper"); // 管理员  0不是   1是
                        boolean is_Follow = anchorid_helper==0?false:true ;
                        boolean userHelper = anchorid_is_followed==0?false:true ;

                        //视频信息
                        String video_livingStream = video_info.getString("live_url");
                        String chaturl  = video_info.getString("chat_url");
                        int chat_port = video_info.getInt("chat_port");

                        //其他
                        String auth_openkey = auth.getString("openkey") ;
                        String auth_timestamp = auth.getString("timestamp") ;
                        String auth_openid = auth.getString("openid") ;

                        state = roomstatus ;
                        roomName = roomname ;
                        anchor_name = anchorid_nickname ;
                        anchorid = anchor_id ;
                        onlineNum = roomOnline ;
                        hotNum = anchorid_hot ;
                        is_guard = anchorid_gender ;
                        fansNum = anchorid_fans_num ;
                        anchor_head_iconUrl = anchorid_icon ;
                        anchorPostUrl = roomposter_url ;
                        chat_url = chaturl ;
                        port = chat_port ;
                        live_url = video_livingStream ;
                        roomid = room_id ;
                        isFollow = is_Follow ;
                        openkey = auth_openkey ;
                        timestamp = auth_timestamp ;
                        openid = auth_openid ;
                        isHelper = userHelper ;



                        mHandler.sendEmptyMessage(HandlerCmd_GetRoomInfo_Success);
                    } else {

                        String tips = obj.getString("data");
                        Message msg = new Message() ;
                        msg.obj = tips ;
                        msg.what = HandlerCmd_GetRoomInfo_Failed ;
                        mHandler.sendMessage(msg) ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(HandlerCmd_GetRoomInfo_Error);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };

        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

}
