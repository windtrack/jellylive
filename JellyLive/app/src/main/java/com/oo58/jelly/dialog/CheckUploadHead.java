package com.oo58.jelly.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.oo58.jelly.R;

/**
 * @author zhongxf
 * @Description 选择性别的弹出框
 * @Date 2016/6/18.
 */
public class CheckUploadHead extends PopupWindow {
    public Context context;
    private View view;
    private OnUpdateTypeSelectListener onGenderSelectListener;

    public CheckUploadHead(Context context, OnUpdateTypeSelectListener listener) {
        super(context);
        this.context = context;
        this.onGenderSelectListener = listener;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.checkuploadtype, null);
        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.mypopwindow_anim_style);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.root).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        Button bt_man = (Button) view.findViewById(R.id.select_photo);
        bt_man.setOnClickListener(clicklistener);
        Button bt_woman = (Button) view.findViewById(R.id.select_local);
        bt_woman.setOnClickListener(clicklistener);
        Button bt_secert = (Button) view.findViewById(R.id.canacle);
        bt_secert.setOnClickListener(clicklistener);
    }

    private View.OnClickListener clicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.select_photo:
                    dismiss();
                    doSelect(0);
                    break;
                case R.id.select_local:
                    dismiss();
                    doSelect(1);
                    break;
                case R.id.canacle:
                    dismiss();
                    break;
            }
        }
    };

    private void doSelect(int index) {
        if (onGenderSelectListener != null) {
            onGenderSelectListener.onSelect(index);
        }
    }

    public interface OnUpdateTypeSelectListener {
        public void onSelect(int idex);
    }


}

