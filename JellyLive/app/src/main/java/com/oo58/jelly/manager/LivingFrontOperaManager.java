package com.oo58.jelly.manager;

import android.content.Context;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.oo58.jelly.R;
import com.oo58.jelly.activity.LivingActivity;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.Util;

/**
 * @author zhongxf
 * @Description 开始直播前的操作的管理类
 * @Date 2016/6/15.
 */
public class LivingFrontOperaManager {
    private ViewStub viewStub;//viewStub对象
    private View rootView;//ViewStub引入的对象
    private LivingActivity activity;//activity对象
    private ImageButton closeBtn;//关闭按钮
    private Button startBtn;//开始直播的按钮

    private ImageButton shareWBBtn;//微博分享按钮
    private ImageButton shareWXBtn;//微信分享按钮
    private ImageButton shareQQBtn;//qq分享按钮
    private ImageButton shareQQKJBtn;//QQ空间分享按钮
    private ImageButton sharePYQBtn;//分享到朋友圈按钮
    private EditText title;//直播标题填写按钮


    public LivingFrontOperaManager(ViewStub viewStub, LivingActivity activity) {
        this.viewStub = viewStub;
        this.activity = activity;
    }



    //显示
    public void show() {
        if (rootView == null) {
            rootView = viewStub.inflate();
            initView();
        } else {
            rootView.setVisibility(View.VISIBLE);
        }
    }

    //初始化
    private void initView() {
        closeBtn = (ImageButton) rootView.findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(listen);

        startBtn = (Button) rootView.findViewById(R.id.start_living_btn);
        startBtn.setOnClickListener(listen);

        shareWXBtn = (ImageButton) rootView.findViewById(R.id.share_wx);
        shareWXBtn.setOnClickListener(listen);
        shareWBBtn = (ImageButton) rootView.findViewById(R.id.share_wb);
        shareWBBtn.setOnClickListener(listen);
        shareQQBtn = (ImageButton) rootView.findViewById(R.id.share_qq);
        shareQQBtn.setOnClickListener(listen);
        shareQQKJBtn = (ImageButton) rootView.findViewById(R.id.share_qqkj);
        shareQQKJBtn.setOnClickListener(listen);
        sharePYQBtn = (ImageButton) rootView.findViewById(R.id.share_pyq);
        sharePYQBtn.setOnClickListener(listen);

        title = (EditText) rootView.findViewById(R.id.living_title);
    }

    //点击监听
    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
            switch (v.getId()) {
                case R.id.close_btn:
                    activity.finish();
                    break;
                case R.id.start_living_btn:
                    doStartLiving() ;
                    break;
                case R.id.share_wb:
                    break;
                case R.id.share_wx:
                    break;
                case R.id.share_qq:
                    break;
                case R.id.share_qqkj:
                    break;
                case R.id.share_pyq:
                    break;
                default:
                    break;
            }
        }
    };

    private void doStartLiving(){


        String strTitle = title.getText().toString();
        if(Util.isEmpty(strTitle)){
            ToastManager.makeToast(activity,"请输入一个话题吧");
            return ;
        }
        activity.callstartLiving(strTitle);
    }

}
