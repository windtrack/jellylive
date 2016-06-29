package com.oo58.jelly.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.adapter.ManagersAdapter;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description 管理员列表界面
 * @Date 2016/6/23.
 */
public class ManagersListActivity extends BaseActivity {

    private static final int HandlerCmd_GetHelperList_Success = 10001;
    private static final int HandlerCmd_GetHelperList_Failed = 10002;


    private ImageButton returnBtn;//管理员列表
    private TextView title;//标题

    private TextView showMangersNum;

    private ListView listView;//显示管理员列表的ListVIew
    private ManagersAdapter adapter;//管理员列表的适配器
    private List<UserVo> list;//管理员的List


    private final static int GET_MANAGERS_SUCCESS = 1001;//获取管理员列表成功
    private final static int MANAGERS_TOTAL = 10;//管理员的总数

    private String roomid;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_MANAGERS_SUCCESS:
                    adapter.notifyDataSetChanged();
                    showMangersNum.setText("当前管理员（" + list.size() + "/" + MANAGERS_TOTAL + "）");
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        roomid = intent.getStringExtra("roomid");
        setContentView(R.layout.managers_list);
        initView();
    }

    //初始化界面
    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("管理员列表");

        listView = (ListView) findViewById(R.id.managers_list);
        list = new ArrayList<UserVo>();
        adapter = new ManagersAdapter(list, ManagersListActivity.this);
        listView.setAdapter(adapter);

        showMangersNum = (TextView) findViewById(R.id.show_managers_num);

        getHelpList();
    }


    //点击的监听
    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.return_btn:
                    finish();
                    break;
                default:
                    break;

            }
        }
    };

    /**
     * 管理员列表
     */
    private void getHelpList() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String openid = GlobalData.getUID(mContext);
                    String secret = GlobalData.getUSecert(mContext);
                    String result = Util.addRpcEvent(RpcEvent.CallHelperList, openid, secret, roomid);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONObject helpers = obj.getJSONObject("data");

                        JSONArray helplist = helpers.getJSONArray("helpers");
                        for (int i = 0; i < helplist.length(); i++) {
                            JSONObject userInfo = helplist.optJSONObject(i);
                            UserVo userVo = new UserVo();
                            userVo.setUid(userInfo.getString("id"));
                            userVo.setName(userInfo.getString("nickname"));
                            userVo.setIcon(AppUrl.USER_LOGO_ROOT+userInfo.getString("icon"));
                            userVo.setGender(userInfo.getInt("gender"));
//                            userVo.setViplev(userInfo.getInt("vip_lv"));
                            userVo.setCostlev(userInfo.getInt("cost_level"));
                            userVo.setReceivelev(userInfo.getInt("received_level"));
                            userVo.setSign(userInfo.getString("sign"));

//                            userVo.setFollow(userInfo.getInt("is_followed")==0?false:true);
//                            userVo.setBlacked(userInfo.getInt("is_blacked")==0?false:true);
//                            userVo.setHelper(userInfo.getInt("is_helper")==1?true:false);
//                            userVo.setShoutUp(userInfo.getInt("is_shutuped")==1?true:false);
                            list.add(userVo);
                        }
                        handler.sendEmptyMessage(HandlerCmd_GetHelperList_Success);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetHelperList_Failed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetHelperList_Failed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            case HandlerCmd_GetHelperList_Success:
                adapter.notifyDataSetChanged();
                break;
            case HandlerCmd_GetHelperList_Failed:
                break;
        }
    }
}
