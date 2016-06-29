package com.oo58.jelly.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.oo58.jelly.R;
import com.oo58.jelly.adapter.SiXinListAdapter;
import com.oo58.jelly.entity.PrivateMsgVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description 私信列表对话框
 * @Date 2016/6/23.
 */
public class SiXinListDialog {

    private Context context;//上下文
    private LayoutInflater mLayoutInflater;//视图加载器
    private Dialog dialog;

    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;


    public SiXinListDialog(Context context) {
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
        View main = mLayoutInflater.inflate(R.layout.sixin_list, null);

        RelativeLayout root = (RelativeLayout) main.findViewById(R.id.root);
        root.setOnClickListener(listen);

        ListView listview = (ListView) main.findViewById(R.id.sixin_list);
        List<PrivateMsgVo> list = new ArrayList<PrivateMsgVo>();
        list.add(new PrivateMsgVo());
        list.add(new PrivateMsgVo());
        SiXinListAdapter adapter = new SiXinListAdapter(list, context);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SendSiXinDialog sendDialog = new SendSiXinDialog(context);
                sendDialog.show();
            }
        });

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
                default:
                    break;
            }
        }
    };


    /**
     * 关闭弹出框
     */
    public void closeDialog() {
        if (dialog != null)
            dialog.dismiss();

    }

}
