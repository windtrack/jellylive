package com.oo58.jelly.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oo58.jelly.R;

/**
 * @author zhongxf
 * @Description 全屏的Loading框
 * @Date 2016/6/14.
 */
public class LoadingDialog {
    private Dialog loadingDialog;

    /**
     * 显示一个等待框
     */
    public void show(Context context, boolean isCancel) {
        creatDialog(context, "", isCancel, false);
    }

    /**
     * 显示一个等待框
     */
    public void show(Context context, String msg, boolean isCancel, boolean isRight) {
        creatDialog(context, msg, isCancel, isRight);
    }

    private void creatDialog(Context context, String msg, boolean isCancel, boolean isRight) {
        LinearLayout.LayoutParams wrap_content = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams wrap_content0 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout main = new LinearLayout(context);
//		main.setBackgroundColor(Color.WHITE);
        if (isRight) {
            main.setOrientation(LinearLayout.HORIZONTAL);
            wrap_content.setMargins(10, 0, 35, 0);
            wrap_content0.setMargins(35, 25, 0, 25);
        } else {
            main.setOrientation(LinearLayout.VERTICAL);
            wrap_content.setMargins(10, 5, 10, 15);
            wrap_content0.setMargins(35, 25, 35, 0);
        }
        main.setGravity(Gravity.CENTER);
        ImageView spaceshipImage = new ImageView(context);
        spaceshipImage.setImageResource(R.mipmap.loading);
        TextView tipTextView = new TextView(context);
        tipTextView.setText("请稍候...");
        // 加载旋转动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        if (msg != null && !"".equals(msg))
            tipTextView.setText(msg);// 设置加载信息,否则加载默认值

        closeDialog();
        loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(isCancel);// 是否可以用返回键取消
        main.addView(spaceshipImage, wrap_content0);
        main.addView(tipTextView, wrap_content);
        LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        loadingDialog.setContentView(main, fill_parent);// 设置布局
        loadingDialog.show();
    }

    /**
     * 关闭等待层
     */
    public void closeDialog() {
        if(loadingDialog!=null)
            loadingDialog.dismiss();
    }
}
