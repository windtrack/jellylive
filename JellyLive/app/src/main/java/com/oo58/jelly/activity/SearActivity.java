package com.oo58.jelly.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.oo58.jelly.R;
import com.oo58.jelly.adapter.SearchAdapter;
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
 * @Description 搜索界面
 * @Date 2016/6/15.
 */
public class SearActivity extends BaseActivity {
    private static final int HandlerCmd_GetSerach_Success = 10001;
    private static final int HandlerCmd_GetSerach_Failed = 10002;


    private ImageButton returnBtn;//返回按钮
    private ListView listView;//显示数据的ListView
    private List<UserVo> list;//数据源
    private SearchAdapter adapter;//适配器

    private EditText editTextSerach;//搜索框
    private Button bt_serach;//搜索按钮
    private ImageButton bt_clear;//清空
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        initView();
    }

    //初始化界面
    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        listView = (ListView) findViewById(R.id.sear_list);
        list = new ArrayList<UserVo>();
        adapter = new SearchAdapter(list, SearActivity.this);
        listView.setAdapter(adapter);

        editTextSerach = (EditText) findViewById(R.id.edittext_serach);

        bt_serach = (Button) findViewById(R.id.bt_serach);
        bt_serach.setOnClickListener(listen);

        bt_clear = (ImageButton)findViewById(R.id.clear_btn);
        bt_clear.setOnClickListener(listen);
    }


    //点击监听
    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_serach: //开始搜索
                    callSerach();
                    break;
                case R.id.clear_btn: //清空
                    editTextSerach.setText("");
                    list.clear();
                    break;
                case R.id.return_btn:
                    Util.hideInput(mContext,v);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * handler消息接收
     *
     * @param msg
     */
    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            case HandlerCmd_GetSerach_Success:
                adapter.notifyDataSetChanged();
                break;
            case HandlerCmd_GetSerach_Failed:

                break;
        }
    }

    /**
     * 开始搜索
     */
    private void callSerach() {
        String contend = editTextSerach.getText().toString();
        if (Util.isEmpty(contend)) {
            return;
        }
        getSearchList(contend);
    }

    /**
     * 搜索数据
     */
    private void getSearchList(final String text) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String uid = GlobalData.getUID(mContext);
                    String secerts = GlobalData.getUSecert(mContext);
                    String result = Util.addRpcEvent(RpcEvent.CallSearch, uid, secerts, text,1,1,100);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONArray rankList = obj.getJSONArray("data");
                        list.clear();
                        for (int i = 0; i < rankList.length(); i++) {
                            JSONObject rank = rankList.optJSONObject(i);
                            UserVo lv = new UserVo();
                            lv.setName(rank.getString("nickname"));
                            lv.setUid(rank.getString("id"));
                            lv.setIcon(AppUrl.USER_LOGO_ROOT + rank.getString("icon"));
//                            lv.setViplev(rank.getInt("level"));
                            lv.setReceivelev(rank.getInt("received_level"));
                            lv.setCostlev(rank.getInt("cost_level"));
                            lv.setGender(rank.getInt("gender"));
                            list.add(lv);
                        }
                        handler.sendEmptyMessage(HandlerCmd_GetSerach_Success);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetSerach_Failed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetSerach_Failed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }


}
