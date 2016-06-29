package com.oo58.jelly.entity.room;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.Gift;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.impl.FollowAnchorImpl;
import com.oo58.jelly.impl.RoomViewerImpl;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc: 房间里的操作和获取数据调用
 * Created by sunjinfang on 2016/6/17 16:17.
 */
public class RoomRequest {
    //关注
    public static final int HandlerCmd_Follow_Success = 40001;
    public static final int HandlerCmd_Follow_Fialed = 40002;

    //设置管理员
    public static final int HandlerCmd_Helper_Success = 40003;
    public static final int HandlerCmd_Helper_Fialed = 40004;

    //开始直播
    public static final int HandlerCmd_StartLiving_Success = 40005;
    public static final int HandlerCmd_StartLiving_Fialed = 40006;

    //结束直播
    public static final int HandlerCmd_EndLiving_Success = 40007;
    public static final int HandlerCmd_EndLiving_Fialed = 40008;

    //更新直播时间
    public static final int HandlerCmd_UpdateTime_Success = 40009;
    public static final int HandlerCmd_UpdateTime_Fialed = 400010;

    //禁言
    public static final int HandlerCmd_ShutUp_Success = 40011;
    public static final int HandlerCmd_ShutUp_Fialed = 40012;

    //所有观众
    public static final int HandlerCmd_GetAllViewer_Success = 40013;
    public static final int HandlerCmd_GetAllViewer_Fialed = 40014;

    //获取用户信息
    public static final int HandlerCmd_GetUserInfo_Success = 40015;
    public static final int HandlerCmd_GetUserInfo_Fialed = 40016;



    //举报
    public static final int HandlerCmd_Report_Success = 40019;
    public static final int HandlerCmd_Report_Fialed = 40020;

    /**
     * 关注主播
     * @param mContext
     * @param mHandler
     * @param followid
     * @param add
     * @param listener
     */
    public static void callFollowAnchor(final Context mContext, final Handler mHandler , final String followid, final boolean add, final FollowAnchorImpl listener){
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String uid = GlobalData.getUID(mContext) ;
                    String secerts = GlobalData.getUSecert(mContext) ;
                    String result ="" ;
                    if(add){
                        result = Util.addRpcEvent(RpcEvent.CallFollowAnchor,uid,secerts,followid);
                    }else{
                        result = Util.addRpcEvent(RpcEvent.CallCancelFollowAnchor,uid,secerts,followid);
                    }
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        mHandler.sendEmptyMessage(HandlerCmd_Follow_Success);
                    } else {
                        mHandler.sendEmptyMessage(HandlerCmd_Follow_Fialed);
                    }
                    if(listener!=null){
                        if (s == 1) {
                            listener.followSuccess(add,followid);
                        } else {
                            String tips = obj.getString("data") ;
                            listener.followFailed(tips);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(HandlerCmd_Follow_Fialed);

                    if(listener!=null){
//                        listener.followFailed(add,followid);
                    }

                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 添加或撤销管理员
     * @param mContext
     * @param mHandler
     * @param heperid
     * @param add 1设置或 2删除
     */
    public static void callHelper(final Context mContext, final Handler mHandler ,final String heperid, final int add){
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String uid = GlobalData.getUID(mContext) ;
                    String secerts = GlobalData.getUSecert(mContext) ;
                    String result = Util.addRpcEvent(RpcEvent.CallAddHelper,uid,secerts,uid,heperid,add);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
//                        JSONObject data = obj.getJSONObject("data");
                        mHandler.sendEmptyMessage(HandlerCmd_Helper_Success);
                    } else {
                        mHandler.sendEmptyMessage(HandlerCmd_Helper_Fialed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(HandlerCmd_Helper_Fialed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 开始直播
     * @param mContext
     * @param mHandler
     * @param title 传入一个话题
     */
    public static void callStartLiving(final Context mContext, final Handler mHandler,final String title ){
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String uid = GlobalData.getUID(mContext) ;
                    String secerts = GlobalData.getUSecert(mContext) ;
                    String result = Util.addRpcEvent(RpcEvent.CallStartLiving,uid,secerts,title);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
//                        JSONObject data = obj.getJSONObject("data");
                        mHandler.sendEmptyMessage(HandlerCmd_StartLiving_Success);
                    } else {
                        mHandler.sendEmptyMessage(HandlerCmd_StartLiving_Fialed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(HandlerCmd_StartLiving_Fialed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 结束直播
     * @param mContext
     * @param mHandler
     */
    public static void callEndLiving(final Context mContext, final Handler mHandler ){
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String uid = GlobalData.getUID(mContext) ;
                    String secerts = GlobalData.getUSecert(mContext) ;
                    String result = Util.addRpcEvent(RpcEvent.CallEndLiving,uid,secerts);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
//                        JSONObject data = obj.getJSONObject("data");
                        mHandler.sendEmptyMessage(HandlerCmd_EndLiving_Success);
                    } else {
                        mHandler.sendEmptyMessage(HandlerCmd_EndLiving_Fialed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(HandlerCmd_EndLiving_Fialed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }


    /**
     * 更新直播时间
     * @param mContext
     * @param mHandler
     */
    public static void callUpdateLivingTime(final Context mContext, final Handler mHandler ){
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String uid = GlobalData.getUID(mContext) ;
                    String secerts = GlobalData.getUSecert(mContext) ;
                    String result = Util.addRpcEvent(RpcEvent.CallUpdateIsLiving,uid,secerts);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
//                        JSONObject data = obj.getJSONObject("data");
                        mHandler.sendEmptyMessage(HandlerCmd_UpdateTime_Success);
                    } else {
                        mHandler.sendEmptyMessage(HandlerCmd_UpdateTime_Fialed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(HandlerCmd_UpdateTime_Fialed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }


    /**
     * 禁言
     * @param mContext
     * @param mHandler
     * @param tid 目标
     * @param type  1 禁言 2 取消禁言
     */
    public static void callShutUp(final Context mContext, final Handler mHandler,final String tid,final int type){
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String uid = GlobalData.getUID(mContext) ;
                    String secerts = GlobalData.getUSecert(mContext) ;
                    String result = Util.addRpcEvent(RpcEvent.CallShutUp,uid,secerts);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        mHandler.sendEmptyMessage(HandlerCmd_ShutUp_Success);
                    } else {
                        mHandler.sendEmptyMessage(HandlerCmd_ShutUp_Fialed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(HandlerCmd_ShutUp_Fialed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 获取礼物列表
     */
    public static void getGiftList(final Context mContext, final Handler mHandler){
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String uid = GlobalData.getUID(mContext) ;
                    String secerts = GlobalData.getUSecert(mContext) ;
                    String result = Util.addRpcEvent(RpcEvent.GetGiftList,uid,secerts);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONArray giftList = obj.getJSONArray("data");

                        List<Gift> Gs = new ArrayList<Gift>();
                        for(int i=0; i<giftList.length(); i++){
                            Gift giftitem = new Gift();
                            Gson gson = new Gson();
                            giftitem = gson.fromJson(giftList.get(i).toString(), Gift.class);
                            Gs.add(giftitem);
                        }
                        Message msg = new Message();
                        msg.obj = Gs;
                        mHandler.sendMessage(msg);
//                        mHandler.sendEmptyMessage(HandlerCmd_ShutUp_Success);
                    } else {
//                        mHandler.sendEmptyMessage(HandlerCmd_ShutUp_Fialed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    mHandler.sendEmptyMessage(HandlerCmd_ShutUp_Fialed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }







    /**
     * 获取所有观众
     * @param mContext
     * @param mHandler
     * @param roomid
     */
    public static void getRoomAllViewer(final Context mContext, final Handler mHandler, final String roomid, final RoomViewerImpl listener){
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String uid = GlobalData.getUID(mContext) ;
                    String secerts = GlobalData.getUSecert(mContext) ;
                    String result = Util.addRpcEvent(RpcEvent.GetRoomViewer,uid,secerts,roomid);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONArray giftList = obj.getJSONArray("data");

                        List<UserVo> List = new ArrayList<UserVo>();
                        for(int i=0; i<giftList.length(); i++){
                            UserVo uv = new UserVo();
                            JSONObject user = giftList.optJSONObject(i) ;
                            uv.setIcon(AppUrl.USER_LOGO_ROOT+user.getString("icon"));
                            uv.setUid(user.getString("id"));
                            uv.setViplev(user.getInt("vip_lv"));
                            uv.setCostlev(user.getInt("cost_level"));
                            uv.setReceivelev(user.getInt("received_level"));
                            uv.setName(user.getString("nickname"));
                            uv.setUserType(user.getString("user_type"));
                            List.add(uv);
                        }

                        if(listener!=null){
                            listener.sendAllView(List);
                        }
                        Message msg = new Message();
                        msg.obj = List;
                        msg.what = HandlerCmd_GetAllViewer_Success;
                        mHandler.sendMessage(msg);
//                        mHandler.sendEmptyMessage(HandlerCmd_GetAllViewer_Success);
                    } else {
                        mHandler.sendEmptyMessage(HandlerCmd_GetAllViewer_Fialed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(HandlerCmd_GetAllViewer_Fialed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 获取用户数据
     */
    public static  void getUserInfo(final Context context,final Handler handler,final String uid,final String roomid) {

        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    String openid = GlobalData.getUID(context);
                    String secret = GlobalData.getUSecert(context);
                    String result = Util.addRpcEvent(RpcEvent.GetUserInfo, openid, secret, uid,roomid);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONObject userInfo = obj.getJSONObject("data");




                        UserVo userVo = new UserVo();
                        userVo.setUid(userInfo.getString("id"));
                        userVo.setName(userInfo.getString("nickname"));
                        userVo.setIcon(AppUrl.USER_LOGO_ROOT+userInfo.getString("icon"));
                        userVo.setGender(userInfo.getInt("gender"));
                        userVo.setViplev(userInfo.getInt("vip_lv"));
                        userVo.setCostlev(userInfo.getInt("cost_level"));
                        userVo.setReceivelev(userInfo.getInt("received_level"));
                        if(userInfo.has("sign")){
                            userVo.setSign(userInfo.getString("sign"));
                        }

                        userVo.setCostBeans(userInfo.getInt("cost_beans"));
                        userVo.setReceivedBeans(userInfo.getInt("received_beans"));

                        userVo.setFansCount(userInfo.getInt("fans_num"));
                        userVo.setFollowCount(userInfo.getInt("followed_num"));

                        userVo.setFollow(userInfo.getInt("is_followed")==0?false:true);
                        userVo.setBlacked(userInfo.getInt("is_blacked")==0?false:true);
                        userVo.setHelper(userInfo.getInt("is_helper")==1?true:false);
                        userVo.setShoutUp(userInfo.getInt("is_shutuped")==1?true:false);

                        Message msg = new Message();
                        msg.what = HandlerCmd_GetUserInfo_Success;
                        msg.obj = userVo;
                        handler.sendMessage(msg) ;
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetUserInfo_Fialed);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetUserInfo_Fialed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 举报用户
     * @param context
     * @param handler
     * @param uid
     */
    public static  void callReportUser(final Context context,final Handler handler,final String uid) {
        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    String openid = GlobalData.getUID(context);
                    String secret = GlobalData.getUSecert(context);
                    String result = Util.addRpcEvent(RpcEvent.CallReport, openid, secret, uid);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {

                        Message msg = new Message();
                        msg.what = HandlerCmd_GetUserInfo_Success;
                        msg.obj = uid;
                        handler.sendMessage(msg) ;
                        handler.sendEmptyMessage(HandlerCmd_Report_Success);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_Report_Fialed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_Report_Fialed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

}
