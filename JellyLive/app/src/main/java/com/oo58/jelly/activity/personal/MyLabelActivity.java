package com.oo58.jelly.activity.personal;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;

/**
 * @author zhongxf
 * @Description 我的等级界面
 * @Date 2016/6/22.
 */
public class MyLabelActivity extends BaseActivity {

    private ImageButton returnBtn;//返回按钮
    private TextView title;//标题

    private SeekBar jd;//进度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_label);
        initView();
    }

    //初始化界面
    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("我的等级");

        jd = (SeekBar) findViewById(R.id.level_jd);
        jd.setProgress(50);
        jd.setEnabled(false);

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
