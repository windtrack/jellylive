package com.oo58.jelly.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.oo58.jelly.R;
import com.oo58.jelly.adapter.ChatMsgAdapter;
import com.oo58.jelly.adapter.LivingPersonAdaper;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.dialog.GiftDialog;
import com.oo58.jelly.dialog.PersonalCardDialog;
import com.oo58.jelly.dialog.SendMsgDialog;
import com.oo58.jelly.dialog.ShowPromptDialog;
import com.oo58.jelly.entity.ChatMsgVo;
import com.oo58.jelly.entity.room.ChatAdapter;
import com.oo58.jelly.entity.room.ChatGiftAdapter;
import com.oo58.jelly.entity.room.ChatMessage;
import com.oo58.jelly.entity.room.RoomChat;
import com.oo58.jelly.entity.room.RoomInfo;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.entity.room.RoomRequest;
import com.oo58.jelly.manager.DanMuManager;
import com.oo58.jelly.util.RpcRequest;
import com.oo58.jelly.impl.OnUserTouchListener;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.UIHandler;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


/**
 * @author zhongxf
 * @Description 直播和直播间的抽象类
 * @Date 2016/6/16.
 */
public abstract class BaseLivingActivity extends Activity {


    private final static int GET_USER_SUCCESS = 1001;//获取观众列表
    private static final int M_Handler_ShowGiftAnimation = 10;//礼物动画显示

    public SurfaceView surfaceView;//底层的SurfaceView（测试专用，正式开发删掉）
    private RecyclerView livingUserList;//直播间的用户的列表
    private ListView chatMsgListView;//收到的消息的列表
    private List<ChatMsgVo> chatList;//消息的列表
    private Button hotBtn;//热度按钮

    //获取设置的布局
    abstract int getContentView();

    //初始化子界面的初始化View
    abstract void initView();

    //界面数据
    private CircleImageView img_head;
    private TextView textView_name;//主播名称
    private TextView textView_onlineNum;//观看人数
    private TextView textView_hot;// 热度
    private TextView textView_userid;//主播id
    private Button bt_follow;//关注

    /**
     * 聊天消息的界面以及数据
     */
    private List<ChatMessage> messages; //聊天消息数据源

    public static List<ChatMessage> giftmessages; //礼物消息数据源
    private ChatAdapter chatadapter;// 消息适配器
    private ChatGiftAdapter chatGiftAdapter; //礼物适配器
    private ListView pub_chat_listview;// 聊天界面
    private ListView chat_gift_item; //礼物界面
    private Timer timer;//礼物显示的定时器

    public RoomInfo roomInfo;// 房间信息
    public RoomChat roomChat;//房间聊天消息

    public Context mContext;
    //跳转传来的三个数据
    public String roomid;
    private String liveingStream;
    private String anchorid;
    public DisplayImageOptions mOptions;//头像

    private List<UserVo> userList;//观看直播的列表
    private LivingPersonAdaper livingPersonAdaper;//观看直播的人

    private PersonalCardDialog personalCardDialog; //个人资料


    public SendMsgDialog sendMsgDialog;//发送消息的对话框
    public GiftDialog giftDialog;//送礼界面

    //弹幕的View
    private RelativeLayout dmTop;//弹幕top
    private RelativeLayout dmBottom;//弹幕bottom


    private CircleImageView topFace;
    private CircleImageView bottomFace;

    private TextView topContent;
    private TextView bottomContent;

    private TextView topNick;
    private TextView bottomNick;


    private DanMuManager dmMananger;//弹幕动画和消息的管理
    protected UIHandler mHandler = new UIHandler(Looper.getMainLooper());

    protected ImageButton cameraBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        Intent intent = getIntent();
        roomid = intent.getStringExtra("roomid");
        liveingStream = intent.getStringExtra("stream");
        anchorid = intent.getStringExtra("anchorid");


        mContext = this;

        mHandler.setHandler(new UIHandler.IHandler() {
            public void handleMessage(Message msg) {
                handler(msg);
            }
        });


        baseInitView();//调用基类的初始化方法，初始化所有共同的组件
        initView();//调用子类的初始化View的方法初始化所有特有的View
        initData();


    }

    public void handler(Message msg) {
        switch (msg.what) {
            case GET_USER_SUCCESS:

                break;
            //获取房间信息
            case RoomInfo.HandlerCmd_GetRoomInfo_Success:
                doGetRoomInfoSuccess();
                break;
            case RoomInfo.HandlerCmd_GetRoomInfo_Failed:
                String tips = (String) msg.obj;
                if (!Util.isEmpty(tips)) {
                    ToastManager.makeToast(mContext, tips);
                } else {
                    ToastManager.makeToast(mContext, "获取房间信息错误");
                }

                finish();
                break;
            case RoomInfo.HandlerCmd_GetRoomInfo_Error:
                ToastManager.makeToast(mContext, "获取房间信息错误");
                break;
            //重复登录
            case RoomChat.HandlerCmd_LoginAgain:
                break;
            //更新在线人数
            case RoomChat.HandlerCmd_ViewerLevel: {
                int count = msg.arg1;
                String uid = (String) msg.obj;
                updateOnlineView(false, count, uid, "");
            }
            break;
            case RoomChat.HandlerCmd_ViewerComeIn: {
                UserVo userVo = (UserVo) msg.obj;
                int count = msg.arg1;
                String uid = userVo.getUid();
                String icon = userVo.getIcon();
                updateOnlineView(true, count, uid, icon);
            }
            break;
            //更新礼物
            case RoomChat.HandlerCMd_UpdateTime:
                break;
            case M_Handler_ShowGiftAnimation:
//                    doShowGiftAnimation();
                break;
            //关注
            case RoomRequest.HandlerCmd_Follow_Success:
                roomInfo.isFollow = true;
                checkFollowBt();
                ToastManager.makeToast(mContext, "关注成功");
                break;
            case RoomRequest.HandlerCmd_Follow_Fialed:
                ToastManager.makeToast(mContext, "关注失败");
                break;
            //设置管理员
            case RoomRequest.HandlerCmd_Helper_Success:
                break;
            case RoomRequest.HandlerCmd_Helper_Fialed:
                break;


            //更新直播时间
            case RoomRequest.HandlerCmd_UpdateTime_Success:
                break;
            case RoomRequest.HandlerCmd_UpdateTime_Fialed:
                break;
            //禁言
            case RoomRequest.HandlerCmd_ShutUp_Success:
                break;
            case RoomRequest.HandlerCmd_ShutUp_Fialed:
                break;
            //所有用户
            case RoomRequest.HandlerCmd_GetAllViewer_Success:
                userList = (List<UserVo>) msg.obj;
                showLivingPlayer();
                break;
            case RoomRequest.HandlerCmd_GetAllViewer_Fialed:
                ToastManager.makeToast(mContext, "获取房间用户失败");
                break;
            //获取用户信息
            case RoomRequest.HandlerCmd_GetUserInfo_Success:
                UserVo user = (UserVo) msg.obj;
                boolean flag = GlobalData.getUID(mContext).equals(roomInfo.anchorid) || roomInfo.isHelper;
                personalCardDialog.show(flag, user, roomInfo.roomid);
                break;
            case RoomRequest.HandlerCmd_GetUserInfo_Fialed:
                ToastManager.makeToast(mContext, "获取用户信息失败");
                break;
            //管理员
            case RoomChat.HandlerCMd_HelperManager: {
                String uid = (String) msg.obj;
                int type = msg.arg1;
                if (uid.equals(GlobalData.getUID(mContext))) {
                    roomInfo.isHelper = type == 1 ? true : false;
                    ToastManager.makeToast(mContext, roomInfo.isHelper ? "您被设置为管理员" : "您被撤销了管理员");
                }
            }

            break;
            //禁言
            case RoomChat.HandlerCMd_ShutUp: {
                String uid = (String) msg.obj;
                int type = msg.arg1;
                if (uid.equals(GlobalData.getUID(mContext))) {
                    roomInfo.isShutUp = type == 1 ? true : false;
                    ToastManager.makeToast(mContext, roomInfo.isShutUp ? "您被禁言了" : "您被解除了禁言");
                }
            }
            case RoomChat.HandlerCMd_ShowDanMu:
                ChatMsgVo chat = (ChatMsgVo) msg.obj;
                if (chat != null) {
                    dmMananger.addMsg(chat);
                }
                break;
            default:
                break;
        }
    }


    private void initData() {
        roomInfo = new RoomInfo();
        roomInfo.getRoomInfo(mContext, mHandler, roomid);

        messages = new ArrayList<ChatMessage>();
        giftmessages = new ArrayList<ChatMessage>();
        messages.clear();
        chatadapter = new ChatAdapter(mContext, messages, new ChatAdapter.OnNameClickListener() {

            @Override
            public void OnClickName(String uid) {
                showUserInfo(uid);
            }
        });// 初始化聊天
        // 公聊不为空
        if (pub_chat_listview != null) {
            pub_chat_listview.setAdapter(chatadapter);
        }
        chatGiftAdapter = new ChatGiftAdapter(mContext, giftmessages);
        //礼物
        if (chat_gift_item != null) {
            chat_gift_item.setAdapter(chatGiftAdapter);
        }


    }

    protected void startChat() {
        if (roomChat == null) {
            roomChat = new RoomChat(mContext, roomInfo, mHandler);
            roomChat.setChat_gift_item(chat_gift_item);
            roomChat.setChatadapter(chatadapter);
            roomChat.setChatGiftAdapter(chatGiftAdapter);
            roomChat.setMessages(messages);
            roomChat.setPub_chat_listview(pub_chat_listview);
            roomChat.setGiftmessages(giftmessages);
        }
        roomChat.SocketStart();
    }


    //初始化界面
    private void baseInitView() {

        initTopView();
        //测试绘制底层的图像
        surfaceView = (SurfaceView) findViewById(R.id.surface);
//        surfaceView.getHolder().addCallback(new TestHolderCallBack());

        //设置顶部的观众列表
        livingUserList = (RecyclerView) findViewById(R.id.look_user_list);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        livingUserList.setLayoutManager(linearLayoutManager);
        userList = new ArrayList<UserVo>();


        //初始化消息列表
        chatMsgListView = (ListView) findViewById(R.id.chat_msg_list);
        chatList = new ArrayList<ChatMsgVo>();
        //测试消息的代码（后续对好数据接口删掉）
        ChatMsgVo testMsg = new ChatMsgVo();
        for (int i = 0; i < 20; i++) {
            testMsg.setContent("测试" + i);
            chatList.add(testMsg);
        }
        chatMsgListView.setAdapter(new ChatMsgAdapter(chatList, BaseLivingActivity.this));
        chatMsgListView.setSelection(chatList.size() - 1);//滚动到最后
        //热度按钮
        hotBtn = (Button) findViewById(R.id.living_hot_btn);
        hotBtn.setOnClickListener(baseListen);


        //聊天界面
        pub_chat_listview = (ListView) findViewById(R.id.chat_msg_list);
//        chat_gift_item = (ListView) findViewById(R.id.listView_gift);
//        rl_chat = (RelativeLayout) findViewById(R.id.rl_chat);
//        rl_gift = (RelativeLayout) findViewById(R.id.rl_gift);


        mOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showStubImage(R.mipmap.testpic)
                .showImageForEmptyUri(R.mipmap.testpic)
                .showImageOnFail(R.mipmap.testpic)
                .showImageOnLoading(R.mipmap.testpic).cacheOnDisc()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();


        //获取弹幕的所有的View
        dmTop = (RelativeLayout) findViewById(R.id.danmu_top_con);
        topFace = (CircleImageView) findViewById(R.id.danmu_top_face);
        topContent = (TextView) findViewById(R.id.danmu_top_content);
        topNick = (TextView) findViewById(R.id.danmu_top_nick);

        dmBottom = (RelativeLayout) findViewById(R.id.danmu_bottom_con);
        bottomFace = (CircleImageView) findViewById(R.id.danmu_bottom_face);
        bottomContent = (TextView) findViewById(R.id.danmu_bottom_content);
        bottomNick = (TextView) findViewById(R.id.danmu_bottom_nick);

        //初始化弹幕管理
        dmMananger = new DanMuManager(BaseLivingActivity.this);
        dmMananger.init(dmTop, dmBottom, topFace, bottomFace, topContent, bottomContent, topNick, bottomNick);
        dmMananger.start();

        cameraBtn = (ImageButton) findViewById(R.id.check_living_camera);

    }


    /**
     * 初始化上方数据
     */
    private void initTopView() {

        img_head = (CircleImageView) findViewById(R.id.anchoricon);
        img_head.setOnClickListener(baseListen);

        textView_name = (TextView) findViewById(R.id.anchor_name);
        textView_onlineNum = (TextView) findViewById(R.id.look_show_num);
        textView_hot = (TextView) findViewById(R.id.living_hot_btn);
        textView_userid = (TextView) findViewById(R.id.userid);

        bt_follow = (Button) findViewById(R.id.bt_anchorfollow);
        bt_follow.setOnClickListener(baseListen);

    }

    /**
     * 检测是否关注
     */
    private void checkFollowBt() {

        //自己的房间不显示关注
        if (roomInfo.anchorid.equals(GlobalData.getUID(mContext))) {
            bt_follow.setVisibility(View.INVISIBLE);
            return;
        }
        if (roomInfo.isFollow) {
            bt_follow.setText("已关注");
            bt_follow.setBackgroundResource(R.drawable.fllow_btn_cancel);
        } else {
            bt_follow.setText("关注");
            bt_follow.setBackgroundResource(R.drawable.fllow_btn);
        }
    }

    //测试的Holder
    class TestHolderCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            int SCREEN_WIDTH = getResources().getDisplayMetrics().widthPixels;
            int SCREEN_HEIGHT = getResources().getDisplayMetrics().heightPixels;
            Canvas canvas = holder.lockCanvas();
            RectF rectF = new RectF(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.test), null, rectF, new Paint());
            holder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
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
                }).show(BaseLivingActivity.this, "确定结束观看直播吗？");
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    //点击监听
    private View.OnClickListener baseListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.living_hot_btn:
                    startActivity(new Intent(AppAction.ACTION_RANK_LIST));
                    break;
                case R.id.living_close_btn:

                    break;
                case R.id.bt_anchorfollow://关注

                    doFollowAnchor();

                    break;
                case R.id.anchoricon:
                    showUserInfo(roomInfo.anchorid);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 点击关注主播
     */
    protected void doFollowAnchor() {
        if (GlobalData.checkLogin(mContext)) {
            RpcRequest.OnRpcRequestListener listener = new RpcRequest.OnRpcRequestListener() {
                @Override
                public void onSuccess(Message msg) {
                    roomInfo.isFollow = !roomInfo.isFollow;
                    checkFollowBt();
                    ToastManager.makeToast(mContext, roomInfo.isFollow ? "关注成功" : "取消关注");
                }

                @Override
                public void onFailed(Message msg) {
                }

                @Override
                public void onError(Message msg) {
                }
            };
            if (roomInfo.isFollow) {
                new RpcRequest(mContext, listener).doRequest(RpcEvent.CallCancelFollowAnchor, GlobalData.getUID(mContext), GlobalData.getUSecert(mContext), roomInfo.anchorid);
            } else {
                new RpcRequest(mContext, listener).doRequest(RpcEvent.CallFollowAnchor, GlobalData.getUID(mContext), GlobalData.getUSecert(mContext), roomInfo.anchorid);
            }
        } else {
            ToastManager.makeToast(mContext, "尚未登录");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mHandler.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (roomChat != null) {
            roomChat.destoryChatSocket();
        }
        mHandler.pause();
        roomInfo = null;
        if (dmMananger != null) {//销毁弹幕动画
            dmMananger.destory();
        }
    }


    /**
     * 设置界面
     */
    private void setRoomViewInfo() {
        GlobalData.getmImageLoader(mContext).displayImage(roomInfo.anchor_head_iconUrl, img_head, mOptions);
        textView_name.setText(roomInfo.anchor_name);
        updateOnlineCount(roomInfo.onlineNum);
        updateHot(roomInfo.hotNum);
        textView_userid.setText("主播号:" + roomInfo.anchorid);
    }

    /**
     * 获取房间信息成功后
     */
    private void doGetRoomInfoSuccess() {
        checkFollowBt();
        setRoomViewInfo();
        startChat(); //连接聊天服务器
        addAdminTips();//添加房间系统提示
        RoomRequest.getRoomAllViewer(mContext, mHandler, roomInfo.roomid, null);
    }


    /****
     * ************************************************************
     * 聊天模块
     **************************************************************
     */
    /**
     * 账号重读登录
     */
    private void callFinishByLoginAgain() {
        Intent intent = new Intent();
        intent.setAction(AppAction.RECVIVE_LOGINAGIAN);
        intent.putExtra("loginagain", true);
        sendBroadcast(intent);
        this.finish();
    }

    /**
     * 主播开播时的 系统通知
     */
    private void addAdminTips() {
        String tips = "系统公告：我国有关法律法规禁止直播涉黄、涉政、涉暴等内容，中视24小时实时监管，发现违禁内容会立即封号并上传依法机关哦";
        ChatMessage charMessage = new ChatMessage();
        // 恭喜升级通知
        charMessage.setContent(tips);
        charMessage.setTid(43);
        messages.add(charMessage);
    }


    /**
     * 发送聊天
     *
     * @param str
     */
    public void sengMessage(String str) {
        if (roomChat != null) {
            roomChat.sengMessage(str, RoomChat.C_TID_SendMessage);
        }
    }

    /**
     * 发送指定的聊天类型
     *
     * @param str
     * @param tid
     */
    public void sengMessage(String str, int tid) {
        if (roomChat != null) {
            roomChat.sengMessage(str, tid);
        }
    }

    /**
     * 弹幕消息  rpc转发
     *
     * @param tips
     */
    public void callSendDMMessage(final String tips) {


        if (!GlobalData.checkLogin(mContext)) {
            Toast.makeText(mContext, "您还未登陆", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Util.isEmpty(tips)) {
            Toast.makeText(mContext, "说话不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

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
//                ChatMsgVo chat = new ChatMsgVo();
//                chat.setContent(tips);
//                dmMananger.addMsg(chat);

            }

            @Override
            public void onFailed(Message msg) {
                String result = (String) msg.obj;
                try {
                    JSONObject obj = new JSONObject(result);
                    String data = obj.getString("data");

                    ToastManager.makeToast(mContext, data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Message msg) {

            }
        }).doRequest(RpcEvent.CallSendDanMu, GlobalData.getUID(mContext), GlobalData.getUSecert(mContext), roomInfo.roomid, tips, 0);


    }

    /**
     * 清除聊天消息
     */
    private void cleanChat() {
        messages.clear();
        if (chatadapter != null) {
            chatadapter.notifyDataSetChanged();
        }
    }

    /**
     * 更新在线人数 显示
     *
     * @param count
     */
    private void updateOnlineCount(int count) {
        textView_onlineNum.setText("在线：" + count);
    }

    /**
     * 更新热度
     *
     * @param count
     */
    private void updateHot(int count) {
        textView_hot.setText("热度：" + count);
    }


    /**
     * 更新在线人数  增加删除新人列表
     *
     * @param add 是否家人
     * @param num
     * @param uid
     */
    private void updateOnlineView(boolean add, int num, String uid, String icon) {
        updateOnlineCount(num);
        showLivingPlayer();
        if (add) {
            //检测列表是否有该玩家
            boolean flag = false;
            String selfUid = GlobalData.getUID(mContext);
            for (UserVo vo : userList) {
                //列表里已有的 和自己 不显示
                if (vo.getUid().equals(uid) || uid.equals(selfUid)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                UserVo us = new UserVo();
                us.setUid(uid);
                us.setIcon(icon);
                userList.add(us);
            }

        } else {
            for (UserVo vo : userList) {
                if (vo.getUid().equals(uid)) {
                    userList.remove(vo);
                    break;
                }
            }
        }
    }

    /**
     * 显示在线玩家列表
     */
    private void showLivingPlayer() {
        if (livingPersonAdaper == null) {
            livingPersonAdaper = new LivingPersonAdaper(mContext, userList, new OnUserTouchListener() {
                @Override
                public void onTouch(String uid) {
                    showUserInfo(uid);
                }
            });
            livingUserList.setAdapter(livingPersonAdaper);
        }

    }

    /**
     * 显示个人资料界面
     *
     * @param uid
     */
    protected void showUserInfo(String uid) {
        if (Util.isEmpty(uid)) {
            Log.e("sjf", "个人资料面板 uid为空");
            return;
        }
        if (personalCardDialog == null) {
            personalCardDialog = new PersonalCardDialog(mContext, new PersonalCardDialog.OnPersonalViewListener() {
                @Override
                public void onClosePersonView(UserVo userVo) {

                    if (userVo.getUid().equals(roomInfo.anchorid)) {
                        roomInfo.isFollow = userVo.isFollow();
                        checkFollowBt();
                    }

                }
            });
        }
        RoomRequest.getUserInfo(mContext, mHandler, uid, roomInfo.roomid);
    }


    /**
     * 显示聊天界面
     */
    public void showChatView() {
        if (sendMsgDialog == null) {
            sendMsgDialog = new SendMsgDialog(mContext, new SendMsgDialog.OnSendMsgListener() {
                @Override
                public void sendMessage(String msg) {
                    sengMessage(msg);
                }

                @Override
                public void sendDmMessage(String msg) {
                    callSendDMMessage(msg);
                }
            });
        }
        sendMsgDialog.show();
    }

}
