package com.oo58.jelly.activity.personal.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;
import com.oo58.jelly.adapter.ManagerMsgAdapter;
import com.oo58.jelly.entity.UserVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description 消息管理界面
 * @Date 2016/6/22.
 */
public class MsgManagerActivity extends BaseActivity {

    private ImageButton returnBtn;//返回按钮
    private TextView title;//标题

    private ListView listView;//关注的人的ListView
    private List<UserVo> list;//关注点的人的列表
    private ManagerMsgAdapter adapter;//关注人的适配器
    private final static int GET_USER_SUCCESS = 1001;//获取用户成功

    private CheckBox livingMsgBtn;//是否接口直播开播消息

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_USER_SUCCESS:
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_manager);
        initView();
    }

    //初始化界面
    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("消息管理");

        listView = (ListView) findViewById(R.id.user_list);
        list = new ArrayList<UserVo>();
        adapter = new ManagerMsgAdapter(list, MsgManagerActivity.this);
        listView.setAdapter(adapter);

        livingMsgBtn = (CheckBox) findViewById(R.id.living_msg);
        livingMsgBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    listView.setVisibility(View.VISIBLE);
                } else {
                    listView.setVisibility(View.GONE);
                }
            }
        });

        new Thread(new LoadUserThread()).start();
    }

    //加载用户的子线程
    class LoadUserThread implements Runnable {

        @Override
        public void run() {
            UserVo vo = new UserVo();
            vo.setName("unfind");
            vo.setSign("签名");
            vo.setGender(1);
            vo.setIsGetMsg(0);
            list.add(vo);

            UserVo vo2 = new UserVo();
            vo2.setName("zhongxiaofei");
            vo2.setSign("签名2");
            vo2.setGender(1);
            vo2.setIsGetMsg(0);
            list.add(vo2);
            mHandler.sendEmptyMessage(GET_USER_SUCCESS);
        }
    }

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

    @Override
    protected void handler(Message msg) {

    }
}
