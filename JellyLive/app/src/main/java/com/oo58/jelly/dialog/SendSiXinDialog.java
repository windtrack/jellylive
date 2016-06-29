package com.oo58.jelly.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.oo58.jelly.R;
import com.oo58.jelly.adapter.SendSiXinAdapter;
import com.oo58.jelly.adapter.SiXinListAdapter;
import com.oo58.jelly.entity.PrivateMsgVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description 发送私信对话框
 * @Date 2016/6/23.
 */
public class SendSiXinDialog {


    private Context context;//上下文
    private LayoutInflater mLayoutInflater;//视图加载器
    private Dialog dialog;

    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;

    private ListView listView;//显示私信的ListView
    private List<PrivateMsgVo> list;//私信的数据源
    private SendSiXinAdapter adapter;//私信的适配器

    private final static int SIXIN = 1001;//接受和发送私信成功

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SIXIN:
                    adapter.notifyDataSetChanged();
                    listView.setSelection(adapter.getCount() - 1);
                    break;
                default:
                    break;
            }
        }
    };


    public SendSiXinDialog(Context context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Rect outRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        SCREEN_WIDTH = outRect.width();
        SCREEN_HEIGHT = outRect.height();
    }

    //显示对话框
    public void show() {
        creatDialog();
    }

    private void creatDialog() {
        View main = mLayoutInflater.inflate(R.layout.send_sixin, null);

        RelativeLayout root = (RelativeLayout) main.findViewById(R.id.root);
        root.setOnClickListener(listen);
        listView = (ListView) main.findViewById(R.id.sixin_list);
        list = new ArrayList<PrivateMsgVo>();
        adapter = new SendSiXinAdapter(list, context);
        listView.setAdapter(adapter);

        Button sendSiXinBtn = (Button) main.findViewById(R.id.send_sixin_btn);
        sendSiXinBtn.setOnClickListener(listen);


        closeDialog();//调用关闭方法，防止多层显示
        dialog = new Dialog(context, R.style.send_msg);// 创建自定义样式dialog
        dialog.setCancelable(true);// 是否可以用返回键取消
//        dialog.setCanceledOnTouchOutside(true);//按对话框旁边可以取消
        LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        fill_parent.width = SCREEN_WIDTH;
        fill_parent.height = SCREEN_HEIGHT;
        dialog.setContentView(main, fill_parent);// 设置布局
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.show();
    }

    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.root:
                    closeDialog();//关闭对话框
                    break;
                case R.id.send_sixin_btn:
                    new Thread(new SendSiXinThread()).start();
                    break;
                default:
                    break;
            }
        }
    };

    class SendSiXinThread implements Runnable {
        @Override
        public void run() {
            list.add(new PrivateMsgVo());
            mHandler.sendEmptyMessage(SIXIN);
        }
    }


    /**
     * 关闭弹出框
     */
    public void closeDialog() {
        if (dialog != null)
            dialog.dismiss();

    }
}
