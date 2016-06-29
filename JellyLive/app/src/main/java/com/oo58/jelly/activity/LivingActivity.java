package com.oo58.jelly.activity;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.oo58.jelly.R;
import com.oo58.jelly.dialog.SendMsgDialog;
import com.oo58.jelly.dialog.SiXinListDialog;
import com.oo58.jelly.entity.room.RoomRequest;
import com.oo58.jelly.manager.LivingFrontOperaManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zhongxf
 * @Description 直播界面
 * @Date 2016/6/13.
 */
public class LivingActivity extends BaseLivingActivity {
    private static final int HandlerCmd_CallUpdateLivingTime = 15100;//更新麦时


    private ViewStub bottomViewStub;//底部操作按钮的ViewStub
    private View operaRootView;//底部操作按钮的RootView

    private ViewStub frontLivingViewStub;//开始直播前的ViewStub
    private LivingFrontOperaManager frontOperaManager;//开始直播前的操作

    private RelativeLayout operaCon;//所有操作按钮和消息礼物等界面的容器

    private ImageButton button_chat; //聊天
    private ImageButton button_email;//消息
    private ImageButton button_lianmai;//连麦
    private ImageButton button_music;//声音
    private ImageButton button_screen;//截屏
    private ImageButton button_share;//分享

    private Timer timerAnchorTime;//主播麦时

    private RelativeLayout rootView;//根View

    @Override
    int getContentView() {
        return R.layout.living;
    }

    @Override
    void initView() {

        bottomViewStub = (ViewStub) findViewById(R.id.base_living_opera_con);
        bottomViewStub.setLayoutResource(R.layout.base_living_opera_btns);
        operaRootView = bottomViewStub.inflate();

        operaCon = (RelativeLayout) findViewById(R.id.living_opera_con);
        operaCon.setVisibility(View.GONE);//开始直播前默认隐藏
        frontLivingViewStub = (ViewStub) findViewById(R.id.front_living_opera_con);
        frontOperaManager = new LivingFrontOperaManager(frontLivingViewStub, this);
        frontOperaManager.show();

        rootView = (RelativeLayout) findViewById(R.id.root);
        rootView.getViewTreeObserver().
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        rootView.getWindowVisibleDisplayFrame(r);

                        int screenHeight = rootView.getRootView().getHeight();
                        int heightDifference = screenHeight - (r.bottom - r.top);
                        Log.e("Keyboard Size", "Size:" + heightDifference + "========================" + screenHeight);
                        if (sendMsgDialog != null) {
                            sendMsgDialog.setShowKeyBoard(r.bottom - r.top);
                        }

                    }
                });


        initButton();
    }

    private void initButton() {
        button_chat = (ImageButton) findViewById(R.id.chat_btn);
        button_email = (ImageButton) findViewById(R.id.msg_btn);
        button_lianmai = (ImageButton) findViewById(R.id.meiyan_btn);
        button_music = (ImageButton) findViewById(R.id.shy_btn);
        button_screen = (ImageButton) findViewById(R.id.catscreen_btn);
        button_share = (ImageButton) findViewById(R.id.share_btn);

        button_chat.setOnClickListener(listenr);
        button_email.setOnClickListener(listenr);
        button_lianmai.setOnClickListener(listenr);
        button_music.setOnClickListener(listenr);
        button_screen.setOnClickListener(listenr);
        button_share.setOnClickListener(listenr);
    }



    private View.OnClickListener listenr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chat_btn:
                    showChatView();
                    break;
                case R.id.msg_btn:
//                    SiXinListDialog sxDialog = new SiXinListDialog(LivingActivity.this);
//                    sxDialog.show();
                    break;
                case R.id.meiyan_btn:
                    break;
                case R.id.shy_btn:
                    break;
                case R.id.catscreen_btn:
                    break;
                case R.id.share_btn:
                    break;
            }
        }
    };


    private Handler livingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //开始直播
                case RoomRequest.HandlerCmd_StartLiving_Success:
                    doStartLiving();
                    break;
                case RoomRequest.HandlerCmd_StartLiving_Fialed:
                    break;
                //结束直播
                case RoomRequest.HandlerCmd_EndLiving_Success:
                    break;
                case RoomRequest.HandlerCmd_EndLiving_Fialed:
                    break;
                //更新麦时
                case HandlerCmd_CallUpdateLivingTime:
                    callUpdateAnchorTime();
                    break;
            }
        }
    };


    //开始直播方法
    public void callstartLiving(String title) {
        RoomRequest.callStartLiving(mContext, livingHandler, title);
    }

    /**
     * 开始直播
     */
    protected void doStartLiving() {
        //关闭直播操作界面
        frontLivingViewStub.setVisibility(View.GONE);
        frontOperaManager = null;
        operaCon.setVisibility(View.VISIBLE);
        System.gc();

        callUpdateAnchorTime();//更新麦时
    }

    /**
     * 结束直播
     */
    protected void doFinishLiving() {
        RoomRequest.callEndLiving(mContext, livingHandler);
    }

    /**
     * 定时器控制主播播放时间
     */
    private void callUpdateAnchorTime() {
//        updateAnchorTime();
        RoomRequest.callUpdateLivingTime(mContext, livingHandler);
        timerAnchorTime = new Timer();
        timerAnchorTime.schedule(new TimerTask() {
            public void run() {
                System.out.println("----------------设定要指定任务------------------");
                livingHandler.sendEmptyMessage(HandlerCmd_CallUpdateLivingTime);
            }
        }, 46000);// 设定指定的时间time
    }
}
