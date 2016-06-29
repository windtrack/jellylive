package com.oo58.jelly.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oo58.jelly.R;


/**
 * @author zhongxf
 * @Description 提示用户信息
 * @Date 2016/5/16.
 */
public class ShowPromptDialog {


    private Dialog dialog;
    private View.OnClickListener cancleListen;//取消按钮的监听
    private View.OnClickListener sureListen;//确定按钮的执行方法

    /**
     * 显示一个提示框
     */
    public void show(Context context, String msg) {
        creatDialog(context, msg);
    }

    //设置取消按钮的点击监听
    public ShowPromptDialog setCancleListen(View.OnClickListener cancleListen) {
        this.cancleListen = cancleListen;
        return this;
    }

    //设置确定按钮点击监听
    public ShowPromptDialog setSureListen(View.OnClickListener sureListen) {
        this.sureListen = sureListen;
        return this;
    }

    //创建提示框
    private void creatDialog(Context context, String msg) {
        dialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        View main = LayoutInflater.from(context).inflate(
                R.layout.prompt_dialog, null);

        Button cancleBtn = (Button) main.findViewById(R.id.cancle_btn);
        if (cancleListen != null) {
            cancleBtn.setOnClickListener(cancleListen);
            cancleBtn.setVisibility(View.VISIBLE);
        } else {
            cancleBtn.setVisibility(View.GONE);
        }

        Button sureBtn = (Button) main.findViewById(R.id.suer_btn);
        if (sureListen != null) {
            sureBtn.setOnClickListener(sureListen);
            sureBtn.setVisibility(View.VISIBLE);
        } else {
            sureBtn.setVisibility(View.GONE);
        }
        TextView tv = (TextView) main.findViewById(R.id.info);
        tv.setText(msg);

        LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(main, fill_parent);// 设置布局
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 关闭提示框
     */
    public void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
