package com.oo58.jelly.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;
import com.oo58.jelly.adapter.SettingAdapter;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.dialog.ShowPromptDialog;
import com.oo58.jelly.util.SharedPreUtil;


/**
 * @author zhongxf
 * @Description 设置界面
 * @Date 2016/6/18.
 */
public class SettingActivity extends BaseActivity {

    private ImageButton returnBtn;//返回按钮
    private TextView title;//标题
    private ListView listView;//显示设置的ListView
    private Button bt_logout;//退出登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initView();
    }

    //初始化界面
    private void initView() {


        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);

        bt_logout = (Button) findViewById(R.id.logout_btn);
        bt_logout.setOnClickListener(listen);


        title = (TextView) findViewById(R.id.top_title);
        title.setText("设置");
        listView = (ListView) findViewById(R.id.setting_list);
        listView.setAdapter(new SettingAdapter(SettingActivity.this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://账号与安全
                        startActivity(new Intent(AppAction.ACTION_SECURITY));
                        break;
                    case 1://黑名单
                        startActivity(new Intent(AppAction.ACTION_BLACK_LIST));
                        break;
                    case 2://消息管理
                        startActivity(new Intent(AppAction.ACTION_MSG_MANAGER));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.return_btn:
                    finish();
                    break;
                case R.id.logout_btn:
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
                            doLogout();
                        }
                    }).show(SettingActivity.this, "确定退出吗？");
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 退出登录
     */
    private void doLogout() {
//        SharedPreUtil.clear(mContext);//清空数据
        SharedPreUtil.put(mContext, AppContance.USER_AUTO_LOGIN, false);
        SharedPreUtil.put(mContext, AppContance.USER_LOGIN, false);
        SharedPreUtil.put(mContext, AppContance.PASS, "");
        startActivity(new Intent(AppAction.ACTION_LOGIN));
    }

    @Override
    protected void handler(Message msg) {

    }
}
