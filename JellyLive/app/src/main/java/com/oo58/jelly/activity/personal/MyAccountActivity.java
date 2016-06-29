package com.oo58.jelly.activity.personal;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;
import com.oo58.jelly.adapter.RechargeAdapter;
import com.oo58.jelly.entity.RechargeVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description 我的账户界面
 * @Date 2016/6/16.
 */
public class MyAccountActivity extends BaseActivity {

    private ImageButton returnBtn;//返回按钮
    private TextView title;//标题

    private List<RechargeVo> list;//重置的List
    private ListView listView;//显示重置列表的ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);
        initView();
    }

    //初始化界面
    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("我的账户");

        listView = (ListView) findViewById(R.id.pay_list);
        list = new ArrayList<RechargeVo>();

        setRecharge();

    }

    //设置重置列表
    private void setRecharge() {
        RechargeVo vo = new RechargeVo();
        vo.setMoney(100.00);
        vo.setDiamonds(100);
        vo.setZs(50);
        list.add(vo);

        RechargeVo vo2 = new RechargeVo();
        vo2.setMoney(10.00);
        vo2.setDiamonds(10);
        vo2.setZs(0);
        list.add(vo2);
        listView.setAdapter(new RechargeAdapter(list, MyAccountActivity.this));
    }


    //点击监听
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
