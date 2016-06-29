package com.oo58.jelly.util;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import com.oo58.jelly.R;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.entity.room.RoomRequest;
import com.oo58.jelly.impl.FollowAnchorImpl;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.UIHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Desc: 一个关注按钮的监听
 * Created by sunjinfang on 2016/6/22 11:16.
 */
public class FollowListener implements View.OnClickListener {
    private static final int HandlerCmd_Follow_Success = 10007;


    /**
     * 关注按钮状态文字的适配
     */
    private static final String[][] ButtonStr = {
            {"已关注","+关注"},
            {"已关注","关注"},
            {"已关注","关注"},
    } ;
    private static final int[][]  ButtonTextColor= {
            {R.color.black2c,R.color.yellow_btn_normal},
            {R.color.yellow_btn_normal,R.color.yellow_btn_normal},
            {R.color.black2c,R.color.black2c},
    } ;
    private static final int[][] ButtonBackgroundColor = {
            {R.drawable.search_follow_bt_cancel,R.drawable.search_follow_btn},
            {R.color.transparent,R.color.transparent},
            {R.drawable.fllow_btn,R.drawable.fllow_btn},

    } ;

    private Context context;
    private UserVo user;
    private List<UserVo> list;
    private Button button;
    private int strType ;

    private String userid ;

    public FollowListener(Context context, UserVo user, List<UserVo> list, Button button,int strType) {
        this.context = context;
        this.user = user;
        this.list = list;
        this.button = button;
        this.strType = strType;
        callButtonChange(context,button,user.isFollow(),strType) ;
    }
    

    /**
     * 设置按钮状态 文字
     * @param context
     * @param button
     * @param isFollow
     * @param strtype
     */
    private  void callButtonChange(Context context,Button button, boolean isFollow,int strtype){
        if(isFollow){
            button.setText(ButtonStr[strtype][0]);
            button.setTextColor(context.getResources().getColor(ButtonTextColor[strtype][0]));
            button.setBackgroundResource(ButtonBackgroundColor[strtype][0]);

        }else{
            button.setText(ButtonStr[strtype][1]);
            button.setTextColor(context.getResources().getColor(ButtonTextColor[strtype][1]));
            button.setBackgroundResource(ButtonBackgroundColor[strtype][1]);
        }
    }

    @Override
    public void onClick(View v) {

        if(GlobalData.getUID(context).equals(user.getUid())){
            ToastManager.makeToast(context,"不能关注自己");
            return ;
        }


        RpcRequest.OnRpcRequestListener listener = new RpcRequest.OnRpcRequestListener() {
            @Override
            public void onSuccess(Message msg) {
//                roomInfo.isFollow = !roomInfo.isFollow;
//                checkFollowBt();
//                ToastManager.makeToast(mContext, roomInfo.isFollow ? "关注成功" : "取消关注");
                user.setFollow(!user.isFollow());
                callFollowSucces(button,user.isFollow(),user.getUid());
                callButtonChange(context, button, user.isFollow(),strType);
            }

            @Override
            public void onFailed(Message msg) {
                String reslut = (String)msg.obj ;
                try {
                    JSONObject obj = new JSONObject((reslut));
                    if(obj.has("data")){
                        String tips = obj.getString("data");
                        if(!Util.isEmpty(tips)){
                            ToastManager.makeToast(context,tips);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//
            }

            @Override
            public void onError(Message msg) {
            }
        };
        if (user.isFollow()) {
            new RpcRequest(context, listener).doRequest(RpcEvent.CallCancelFollowAnchor, GlobalData.getUID(context), GlobalData.getUSecert(context), user.getUid());
        } else {
            new RpcRequest(context, listener).doRequest(RpcEvent.CallFollowAnchor, GlobalData.getUID(context), GlobalData.getUSecert(context), user.getUid());
        }



//        RoomRequest.callFollowAnchor(context, handler, user.getUid(), !user.isFollow(), new FollowAnchorImpl() {
//            @Override
//            public void followSuccess(boolean add, String uid) {
//                callFollowSucces(button, add, uid);
//            }
//
//            @Override
//            public void followFailed(String tips) {
////                ToastManager.makeToast(context,tips);
//            }
//        });
    }

    /**
     * 关注成功后 改变数据
     * @param button
     * @param isFollow
     * @param uid
     */
    private void callFollowSucces(Button button, boolean isFollow, String uid) {

        if(list!=null){
            for (int i = 0; i < list.size(); i++) {
                UserVo uv = list.get(i);
                if (uv.getUid().equals(uid)) {
                    uv.setFollow(isFollow);
                    list.set(i, uv);
                }
            }
        }
//        user.setFollow(isFollow);
//        Message msg = new Message();
//        msg.arg1 = isFollow ? 1 : 0;
//        msg.what = HandlerCmd_Follow_Success;
//        handler.sendMessage(msg);
    }

    private UIHandler handler = new UIHandler(Looper.getMainLooper(), new UIHandler.IHandler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerCmd_Follow_Success:
                    boolean flag = msg.arg1 == 1 ? true : false;
                    callButtonChange(context, button, flag,strType);
                    break;
            }
        }
    });

}

