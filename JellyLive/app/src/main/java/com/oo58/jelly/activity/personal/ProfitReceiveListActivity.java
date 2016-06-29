package com.oo58.jelly.activity.personal;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;

/**
 * @author zhongxf
 * @Description 收益的领取记录
 * @Date 2016/6/16.
 */
public class ProfitReceiveListActivity extends BaseActivity {

    private ImageButton returnBtn;//返回按钮
    private TextView topTitle;//顶部标题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profit_receive_list);
        initView();
    }

    //初始化界面
    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        topTitle = (TextView) findViewById(R.id.top_title);
        topTitle.setText("领取记录");
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
