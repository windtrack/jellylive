package com.oo58.jelly.entity.room;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.entity.ChatMsgVo;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.socket.BaseClient;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Desc:
 * Created by sunjinfang on 2016/6/17 10:28.
 */
public class RoomChat {

    public static final int HandlerCmd_LoginAgain = 30001 ;//重复登录
    public static final int HandlerCmd_ViewerLevel = 30002 ;//更新在线人数 有人离开
    public static final int HandlerCmd_ViewerComeIn = 30003 ;//更新在线人数 有人进入
    public static final int HandlerCMd_UpdateTime = 30004;//更新礼物时间
    public static final int HandlerCMd_HelperManager = 30005;//设置/撤销管理员
    public static final int HandlerCMd_ShutUp = 30006;//设置/撤销 禁言
    public static final int HandlerCMd_ShowStar = 30007;//小星星
    public static final int HandlerCMd_ShowDanMu = 30008;//弹幕消息


    private final static int HandlerCmd_GetCHatMSG = 7 ;
    private final static int HandlerCmd_GetChatMsgError = 10086 ;


    /**
     * 聊天消息的界面以及数据
     */
    private List<ChatMessage> messages; //聊天消息数据源
    public static List<ChatMessage> giftmessages; //礼物消息数据源

    private ChatAdapter chatadapter;// 消息适配器
    private ChatGiftAdapter chatGiftAdapter; //礼物适配器

    private ListView pub_chat_listview;// 聊天界面
    private ListView chat_gift_item; //礼物界面

    private BaseClient client; //聊天服务器

    private Context mContext;
    private RoomInfo mRoomInfo;
    private Handler livinghandler;
    public RoomChat(Context context , RoomInfo mRoomInfo, Handler handler){
            this.mContext = context ;
            this.mRoomInfo = mRoomInfo ;
            this.livinghandler = handler ;

    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public  void setGiftmessages(List<ChatMessage> giftmessages) {
        RoomChat.giftmessages = giftmessages;
    }

    public void setChatadapter(ChatAdapter chatadapter) {
        this.chatadapter = chatadapter;
    }

    public void setChatGiftAdapter(ChatGiftAdapter chatGiftAdapter) {
        this.chatGiftAdapter = chatGiftAdapter;
    }

    public void setPub_chat_listview(ListView pub_chat_listview) {
        this.pub_chat_listview = pub_chat_listview;
    }

    public void setChat_gift_item(ListView chat_gift_item) {
        this.chat_gift_item = chat_gift_item;
    }


    /**
     * 聊天服务器返回的消息处理
     */
    public void doChatSocketMessage(android.os.Message msg) {
        String anchor_name = mRoomInfo.anchor_name;
        ActivityMsg activityMsg = (ActivityMsg) msg.obj;
        if(activityMsg == null){
            return ;
        }

        Log.e("sjf", "收到消息：---------" + activityMsg.getTid() + "---------" + activityMsg.getMsg());

        // 账号重复登录
        if (activityMsg.getTid() == 10) {
//            callFinishByLoginAgain();
            livinghandler.sendEmptyMessage(HandlerCmd_LoginAgain);
            return;
        }
        ParseMessage m = new ParseMessage(activityMsg, null, mContext, anchor_name);
        ChatMessage charMessage = m.getMessage();

        //星星
        if(activityMsg.getTid() == 2 ){ //201
            if(charMessage!=null && charMessage.getTid() == C_TID_ShowStart){
                livinghandler.sendEmptyMessage(HandlerCMd_ShowStar);
                return ;
            }
        }

        //设置管理员
        if (activityMsg.getTid() == 10000) {
            String chat = activityMsg.getMsg();
            JSONObject chat_object = null;
            try {
                chat_object = new JSONObject(chat);
                String uid = chat_object.getString("uid") ;// 设置的对象id
                int type = chat_object.getInt("type"); //0 撤销  //1 设置
                Message sendmes = new Message();
                sendmes.obj = uid ;
                sendmes.arg1 = type ;
                sendmes.what = HandlerCMd_HelperManager;
                livinghandler.sendMessage(sendmes);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ;
        }

        //禁言解禁
        if (activityMsg.getTid() == 5) {
            String chat = activityMsg.getMsg();
            JSONObject chat_object = null;
            try {
                chat_object = new JSONObject(chat);
                String uid = chat_object.getString("uid") ;// 设置的对象id
                int type = chat_object.getInt("type"); //0 解禁 //1 禁言
                Message sendmes = new Message();
                sendmes.obj = uid ;
                sendmes.arg1 = type ;
                sendmes.what = HandlerCMd_ShutUp;
                livinghandler.sendMessage(sendmes);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ;
        }




        // 主播开启连麦
        if (activityMsg.getTid() == 53) {
            return ;
        }

        // 主播关闭连麦
        if (activityMsg.getTid() == 54) {
            return ;
        }

        // 寄语消息
        if (activityMsg.getTid() == 14) {
            return ;
        }

        //财神消息
        if (activityMsg.getTid() == 3) {
            return ;
        }

        //中奖消息
        if (activityMsg.getTid() == 230) {
            return ;
        }

        // 主播上播
        if (activityMsg.getTid() == 27) {
            return ;
        }
        // 主播下播
        if (activityMsg.getTid() == 28) {
            return ;
        }
        // 广播 弹幕消息
        if (activityMsg.getTid() == 36) {
            String chat = activityMsg.getMsg();
            JSONObject chat_object = null;
            try {
                chat_object = new JSONObject(chat);

                String name = chat_object.getString("sname");
                String icon = AppUrl.USER_LOGO_ROOT+chat_object.getString("icon");
                String text = chat_object.getString("text");

                Message sendmes = new Message();
                ChatMsgVo chatmsg = new ChatMsgVo();
                chatmsg.setContent(text);
                chatmsg.setIcon(icon);
                chatmsg.setName(name);
                sendmes.obj = chatmsg ;
                sendmes.what = HandlerCMd_ShowDanMu;
                livinghandler.sendMessage(sendmes);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ;
        }
        // tid==42和tid==50（是系统消息和抽魔幻卡牌中奖的消息）
        if (activityMsg.getTid() == 42 || activityMsg.getTid() == 50) {
            return;
        }

        // 被踢出房间
        if (activityMsg.getTid() == 4) {
        }

        // 有人走 在线人数
        if (activityMsg.getTid() == 8) {
            String chat = activityMsg.getMsg();
            JSONObject chat_object = null;
            try {
                chat_object = new JSONObject(chat);
                int num = chat_object.getInt("onlinenum");
                String uid = chat_object.getString("suid") ;
                Message sendmes = new Message();
                sendmes.arg1 = num ;
                sendmes.obj = uid ;
                sendmes.what = HandlerCmd_ViewerLevel;
                livinghandler.sendMessage(sendmes);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        //有人来
        if(activityMsg.getTid() == 7){

            String chat = activityMsg.getMsg();
            JSONObject chat_object = null;
            try {
                chat_object = new JSONObject(chat);
                int num = chat_object.getInt("onlinenum");
                String uid = chat_object.getString("suid") ;
                String icon = AppUrl.USER_LOGO_ROOT+chat_object.getString("icon") ;
                UserVo userVo = new UserVo();
                userVo.setUid(uid);
                userVo.setIcon(icon);


                Message sendmes = new Message();
                sendmes.obj = userVo ;
                sendmes.arg1 = num;
                sendmes.what = HandlerCmd_ViewerComeIn;
                livinghandler.sendMessage(sendmes);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // 不为空，并且不为礼物，聊天
        if (charMessage != null) {
            if (charMessage.getTid() == 33) {// 礼物消息
                // 如果是幸运礼物中奖了，就刷新
                // Log.i("lvjian","-----消息-中奖内容-------->"+m.getMessage().getTname());
                if (!Util.isEmpty(charMessage.getTname())) {
                    messages.add(charMessage);
                    chatadapter.notifyDataSetChanged();
                }

                if (giftmessages.size() == 0) {
                    giftmessages.add(charMessage);
                    if (chat_gift_item == null) {
                        return;
                    }
                    if (chat_gift_item.getChildAt(0) != null) {
                        chatGiftAdapter
                                .AppearAnimPosition(chat_gift_item.getChildAt(0));
                    }
                } else if (giftmessages.size() == 1) {
                    // 如果是同一人就移除旧的直接加载新的
                    if (giftmessages.get(0).getSname()
                            .equals(charMessage.getSname())) {
                        giftmessages.remove(0);
                        giftmessages.add(charMessage);
                    } else {
                        giftmessages.add(charMessage);
                        if (chat_gift_item.getChildAt(1) != null) {
                            chatGiftAdapter.AppearAnimPosition(chat_gift_item
                                    .getChildAt(1));
                        }
                    }
                } else if (giftmessages.size() == 2) {
                    // 如果有两个用户刷，新来的用户是其中之一，直接覆盖，如果不是其中之一移除第一个，添加到最后
                    if (giftmessages.get(0).getSname()
                            .equals(charMessage.getSname())) {
                        // 名字相同，礼物也相同就累计（替换这个位置的）
                        if (giftmessages.get(0).getGift_id()
                                .equals(charMessage.getGift_id())) {
                            giftmessages.set(0, m.getMessage());
                        } else {
                            giftmessages.remove(0);
                            giftmessages.add(charMessage);
                            chatGiftAdapter.AppearAnimPosition(chat_gift_item.getChildAt(1));
                        }

                    } else if (giftmessages.get(1).getSname()
                            .equals(charMessage.getSname())) {
                        if (giftmessages.get(1).getGift_id()
                                .equals(charMessage.getGift_id())) {
                            giftmessages.set(1, charMessage);
                        }
                    } else {
                        giftmessages.remove(0);
                        giftmessages.add(charMessage);
                        chatGiftAdapter.AppearAnimPosition(chat_gift_item.getChildAt(1));
                    }
                }
                chat_gift_item.setVisibility(View.VISIBLE);

                livinghandler.sendEmptyMessage(HandlerCMd_UpdateTime);

                chatGiftAdapter.notifyDataSetChanged();
                            /*
                             * if (chat_gift_item != null) { chat_gift_item .setTranscriptMode (ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
							 * chat_gift_item.setCacheColorHint(0); }
							 */
            } else {
                //聊天消息
                messages.add(charMessage);
                chatadapter.doAutoDelete();
                chatadapter.notifyDataSetChanged();

                pub_chat_listview.smoothScrollToPosition(chatadapter.getCount() - 1);
            }
        }


    }

    private Handler chathandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HandlerCmd_GetCHatMSG:
                    doChatSocketMessage(msg);
                    break ;
                case HandlerCmd_GetChatMsgError:
                    SocketStart();
                    break ;

            }
        }
    };



    // 连接聊天服务器
    public void SocketStart() {
        Runnable socketstartrun = new Runnable() {
            @Override
            public void run() {
                try {
                    // 连接socket服务器
                    destoryChatSocket();

                    client = new BaseClient();
                    client.start(mRoomInfo.chat_url, mRoomInfo.port, chathandler,
                            HandlerCmd_GetCHatMSG,
                            HandlerCmd_GetChatMsgError);
                    JsonObject data = new JsonObject();
                    data.addProperty("userid", mRoomInfo.openid);
                    data.addProperty("roomid", mRoomInfo.roomid);
                    data.addProperty("timestamp", mRoomInfo.timestamp + "");
                    data.addProperty("openkey", mRoomInfo.openkey);
                    data.addProperty("clienttype", 1);
                    String message = data.toString();
                    byte[] request = Util.getBytes(message, HandlerCmd_GetCHatMSG);
                    client.sendmsg(request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(socketstartrun);
    }



    /**
     * 关闭聊天服务器
     */
    public void destoryChatSocket() {
        if (client != null) {
            client.disconnect();
            client = null;
//            chathandler = null ;
        }
    }


    public static final int C_TID_SendMessage = 2 ;//发送聊天
    public static final int C_TID_ShowStart = 201 ;//显示星星

    /**
     * 发送一条聊天消息
     * @param text
     */
    public void sengMessage(String text,int tid) {

        if(!GlobalData.checkLogin(mContext)){
            Toast.makeText(mContext,"您还未登陆",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(mRoomInfo.isShutUp){
            Toast.makeText(mContext,"您被禁言了",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(Util.isEmpty(text)){
            Toast.makeText(mContext,"说话不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        JsonObject chat = new JsonObject();
        chat.addProperty("t_stealth", "0");
        chat.addProperty("s_stealth", "0");
        chat.addProperty("vip_lv", "1");
        chat.addProperty("g_level", "1");
        chat.addProperty("g_type", "0");
        chat.addProperty("sname", "奋斗");
//        chat.addProperty("suid", "200838860");
        chat.addProperty("suid", SharedPreUtil.getString(mContext, AppContance.OPEN_ID));

        chat.addProperty("tuid", "");
        chat.addProperty("tname", "所有人");
        chat.addProperty("tid", String.valueOf(tid)); //发送类型的标志位
        chat.addProperty("text", text);
        String hello = chat.toString();
        byte[] send = Util.getBytes(hello, 2);
        // 发送消息
        client.sendmsg(send);
    }



}
