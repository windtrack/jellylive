package com.oo58.jelly.activity;

import android.graphics.Rect;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.oo58.jelly.R;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.dialog.GiftDialog;
import com.oo58.jelly.dialog.ShowPromptDialog;
import com.oo58.jelly.entity.room.RoomChat;
import com.oo58.jelly.entity.room.RoomRequest;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.ConsumpUtil;
import com.oo58.jelly.util.RpcRequest;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.UIHandler;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.view.zan.PeriscopeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author zhongxf
 * @Description 观看直播界面
 * @Date 2016/6/13.
 */
public class LivingRoomActivity extends BaseLivingActivity implements
        IjkMediaPlayer.OnPreparedListener,
        IjkMediaPlayer.OnInfoListener,
        SurfaceHolder.Callback {
    private ImageButton closeBtn;//关闭按钮
    private ViewStub bottomViewStub;//底部操作按钮的ViewStub
    private View operaRootView;//底部操作按钮的RootView

    private RelativeLayout operaCon;//所有操作按钮和消息礼物等界面的容器

    private  PeriscopeLayout periscopeLayout;//星星触摸层
    //底层按钮
    private ImageButton button_chat; //聊天
    private ImageButton button_email;//消息
    private ImageButton button_gift;//送礼
    private ImageButton button_share;//分享

    private RelativeLayout rootView;//根View

    @Override
    int getContentView() {
        return R.layout.living_room;
    }

    @Override
    void initView() {
        initPlayer();
        super.cameraBtn.setVisibility(View.GONE);
        closeBtn = (ImageButton) findViewById(R.id.living_close_btn);
        closeBtn.setOnClickListener(roomListen);
        bottomViewStub = (ViewStub) findViewById(R.id.base_living_opera_con);
        bottomViewStub.setLayoutResource(R.layout.base_livingroom_operabtns);
        operaRootView = bottomViewStub.inflate();

        operaCon = (RelativeLayout) findViewById(R.id.living_opera_con);
        operaCon.setVisibility(View.VISIBLE);//开始直播前默认隐藏

        rootView = (RelativeLayout) findViewById(R.id.root);
        rootView.getViewTreeObserver().
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        rootView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = rootView.getRootView().getHeight();
                        int heightDifference = screenHeight - (r.bottom - r.top);
                        if (sendMsgDialog != null) {
                            sendMsgDialog.setShowKeyBoard(r.bottom - r.top);
                        }

                    }
                });

        periscopeLayout =  (PeriscopeLayout) findViewById(R.id.periscope);
        periscopeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roomChat!=null){
//                    periscopeLayout.addHeart();
                    roomChat.sengMessage("xx", RoomChat.C_TID_ShowStart);
                }
            }
        });


        initButton();


    }

    private SurfaceHolder surfaceHolder;
    private IjkMediaPlayer player;
    private DisplayMetrics displayMetrics;
    private int videoWidth, videoHeight;

    /**
     * 初始化播放器
     */
    private void initPlayer() {


        IjkMediaPlayer.loadLibrariesOnce(null);
        String storage_path = Environment.getExternalStorageDirectory().getPath();
        IjkMediaPlayer.psglobal_init(storage_path);

        surfaceView = (SurfaceView) findViewById(R.id.surface);
//        surfaceView.getHolder().addCallback(new TestHolderCallBack());
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        player = new IjkMediaPlayer(0, 0);
        player.setOnPreparedListener(this);
        player.setOnInfoListener(this);

        try {
            String url = "pzsp://pzenc.powzamedia.com:8000/live/ld/trans/0058/mlinkm/123456";
//            String url = "rtmp://video.0058.com/RoomVideoChat/936a397a9c3749c072ad18b441e17d65";
            player.setDataSource(url);
//            player.setDataSource(roomInfo.live_url);
            player.prepareAsync();
        } catch (IOException e) {
        }

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        videoWidth = 0;
        videoHeight = 0;
    }


    /**
     * 初始化观看直播界面 按钮 添加监听
     */
    private void initButton() {

        button_chat = (ImageButton) findViewById(R.id.chat_btn);
        button_email = (ImageButton) findViewById(R.id.msg_btn);
        button_gift = (ImageButton) findViewById(R.id.gift_btn);
        button_share = (ImageButton) findViewById(R.id.share_btn);

        button_chat.setOnClickListener(roomListen);
        button_email.setOnClickListener(roomListen);
        button_gift.setOnClickListener(roomListen);
        button_share.setOnClickListener(roomListen);
    }

    private View.OnClickListener roomListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.living_close_btn:
                    final ShowPromptDialog logoutDialog = new ShowPromptDialog();
                    logoutDialog.setCancleListen(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logoutDialog.closeDialog();
                        }
                    }).setSureListen(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logoutDialog.closeDialog();
                            finish();
                        }
                    }).show(LivingRoomActivity.this,"确定结束观看直播吗？");

                    break;
                case R.id.chat_btn:
                    showChatView();
                    break;
                case R.id.msg_btn:
//                    SiXinListDialog sxDialog = new SiXinListDialog(LivingRoomActivity.this);
//                    sxDialog.show();
                    break;
                case R.id.gift_btn:
                    if (giftDialog == null) {
                        giftDialog = new GiftDialog(LivingRoomActivity.this);
                    }
                    giftDialog.init(roomInfo.anchorid, roomInfo.roomid);
                    giftDialog.show();
                    break;
                case R.id.share_btn:
//                    RoomRequest.getGiftList(mContext, mHandler);
                    break;
            }
        }
    };

    @Override
    public void handler(Message msg) {
        super.handler(msg);
        switch (msg.what) {
            case RoomChat.HandlerCMd_ShowStar:
                if(periscopeLayout!=null){
                    periscopeLayout.addHeart();
                }

                break ;
        }

    }

//    private UIHandler livingRoomHandler = new UIHandler(Looper.getMainLooper(), new UIHandler.IHandler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//
//            }
//        }
//    });

    @Override
    protected void onStop() {
        super.onStop();
        player.setBackground(1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        player.setBackground(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
        IjkMediaPlayer.psglobal_release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        Log.e("sjf", ": " + i);
        return true;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        videoWidth = player.getVideoWidth();
        videoHeight = player.getVideoHeight();
        Log.e("sjf", "onPrepared size: " + videoWidth + "x" + videoHeight);

        float ratio = (float) videoWidth / (float) videoHeight;
        videoWidth = surfaceView.getWidth();
        videoHeight = (int) Math.ceil((float) videoWidth / ratio);
        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(videoWidth, videoHeight));
        Log.e("sjf", "onPrepared new size: " + videoWidth + "x" + videoHeight);

        player.start();
    }


    /**
     * 快捷刷礼物
     */
    private void callFastSendGift() {

        String giftid = SharedPreUtil.getString(mContext, AppContance.USER_FAST_GIFT_ID);
        int giftprice = SharedPreUtil.getInt(mContext, AppContance.USER_FAST_GIFT_RPICE);
        String gifticon = SharedPreUtil.getString(mContext, AppContance.USER_FAST_GIFT_ICON);

        if (Util.isEmpty(giftid)) {
            return;
        }

        String beans = GlobalData.getUBeans(mContext);
        boolean canBuy = false;
        //钱是否足够
        if (ConsumpUtil.compare(beans, giftprice + "")) {
            canBuy = true;
            new RpcRequest(mContext, new RpcRequest.OnRpcRequestListener() {
                @Override
                public void onSuccess(Message msg) {

                    String result = (String) msg.obj;
                    try {
                        JSONObject obj = new JSONObject(result);
                        JSONObject data = obj.getJSONObject("data");
                        String beans = data.getString("beans");
                        SharedPreUtil.put(mContext, AppContance.USER_BEANS, beans);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ToastManager.makeToast(mContext, "送礼成功");
                }

                @Override
                public void onFailed(Message msg) {
                    ToastManager.makeToast(mContext, "送礼失败");
                }

                @Override
                public void onError(Message msg) {
                    ToastManager.makeToast(mContext, "送礼失败");
                }
            }).doRequest(RpcEvent.CallSendGift, GlobalData.getUID(mContext), GlobalData.getUSecert(mContext), roomInfo.anchorid, giftid, 1, roomid, false, false);

        } else {
            ToastManager.makeToast(mContext, "乐币不足");
        }

    }
}
