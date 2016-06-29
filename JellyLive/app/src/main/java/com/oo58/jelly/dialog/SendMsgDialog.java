package com.oo58.jelly.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.oo58.jelly.R;
import com.oo58.jelly.adapter.FaceAdapter;
import com.oo58.jelly.adapter.GiftPagerAdapter;
import com.oo58.jelly.manager.LocalImageManager;
import com.oo58.jelly.manager.face.FaceData;
import com.oo58.jelly.manager.face.FaceVo;
import com.oo58.jelly.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;

/**
 * @author zhongxf
 * @Description 发送消息的Dialog
 * @Date 2016/6/23.
 */
public class SendMsgDialog {
    private Context context;//上下文
    private LayoutInflater mLayoutInflater;//视图加载器
    private Dialog dialog;

    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;

    private LinearLayout faceCon;//表情的容器
    private CheckBox danmuBtn;//弹幕开关按钮
    private EditText content;//消息内容输入框
    private Button sendBtn;//消息发按钮
    private ImageButton faceBtn;//表情按钮

    private RelativeLayout root;//最底层的Layout
    private LinearLayout operaCon;//操作的容器
    private ViewPager facePager;//现实表情的ViewPager


    private LinearLayout indicatorCon;//ViewPager的指示器的容器
    private List<View> views;//放置表情的View的

    private Bitmap indicatorSelected;
    private Bitmap indicatoreNormal;

    private int indicatorSize = 0;//指示器的宽度
    private int indicatorLeft = 0;//左边的距离
    private int vSpace = 0;


    private OnSendMsgListener onSendMsgListener;

    private View main;

    public SendMsgDialog(Context context, OnSendMsgListener listner) {
        this.context = context;
        onSendMsgListener = listner;
        mLayoutInflater = LayoutInflater.from(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Rect outRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        SCREEN_WIDTH = outRect.width();
        SCREEN_HEIGHT = outRect.height();

        indicatorSize = UIUtil.dip2px(context, 9);
        indicatorLeft = UIUtil.dip2px(context, 5);
        vSpace = UIUtil.dip2px(context, 27);

        indicatoreNormal = BitmapFactory.decodeResource(context.getResources(), R.mipmap.indicator_gray);
        indicatorSelected = BitmapFactory.decodeResource(context.getResources(), R.mipmap.indicator_yellow);
    }

    //显示对话框
    public void show() {
        creatDialog();
    }

    private void creatDialog() {
        if (main == null) {
            main = mLayoutInflater.inflate(R.layout.send_msg_dialog, null);

            root = (RelativeLayout) main.findViewById(R.id.root);
            views = new ArrayList<View>();
            operaCon = (LinearLayout) main.findViewById(R.id.opera_con);
            indicatorCon = (LinearLayout) main.findViewById(R.id.indicator_con);
            faceCon = (LinearLayout) main.findViewById(R.id.face_con);
            facePager = (ViewPager) main.findViewById(R.id.face_view_pager);
            facePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    setIndicator(position);
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            faceBtn = (ImageButton) main.findViewById(R.id.face_btn);
            faceBtn.setTag(false);
            faceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isShow = (boolean) faceBtn.getTag();

                    if (!isShow) {
                        faceCon.setVisibility(View.VISIBLE);
                        faceBtn.setTag(true);
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                        new Thread(new LoadFaceThread()).start();//开启加载表情的子线程

                    } else {
                        faceCon.setVisibility(View.GONE);
                        faceBtn.setTag(false);
                    }
                }
            });


            danmuBtn = (CheckBox) main.findViewById(R.id.danmu_btn);
            content = (EditText) main.findViewById(R.id.content);
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    faceCon.setVisibility(View.GONE);
                    faceBtn.setTag(false);
                }
            });
            sendBtn = (Button) main.findViewById(R.id.send_chat_msg_btn);
            sendBtn.setOnClickListener(listen);
        }
        closeDialog();//调用关闭方法，防止多层显示
        if (dialog == null) {
            dialog = new Dialog(context, R.style.send_msg);// 创建自定义样式dialog
            dialog.setCancelable(true);// 是否可以用返回键取消
//          dialog.setCanceledOnTouchOutside(true);//按对话框旁边可以取消
            LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            fill_parent.width = SCREEN_WIDTH;
            fill_parent.height = SCREEN_HEIGHT;
            dialog.setContentView(main, fill_parent);// 设置布局
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        dialog.show();
    }

    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.send_chat_msg_btn:

                    if(danmuBtn.isChecked()){

                        onSendMsgListener.sendDmMessage(content.getText().toString());
                        content.setText("");

                    }else{
                        onSendMsgListener.sendMessage(content.getText().toString());
                        content.setText("");
                    }



                    break;
                default:
                    break;
            }
        }
    };

    //键盘弹出的时候设置容器的距离底部的高度
    public void setShowKeyBoard(int height) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) operaCon.getLayoutParams();
        lp.bottomMargin = SCREEN_HEIGHT - height;
    }


    /**
     * 关闭弹出框
     */
    public void closeDialog() {
        if (dialog != null)
            dialog.dismiss();

    }

    private List<FaceVo> faceList;//表情数据的列表

    //加载表情的子线程
    class LoadFaceThread implements Runnable {
        @Override
        public void run() {
            FaceData fd = new FaceData(context);
            faceList = fd.getData();
            mHandler.sendEmptyMessage(GET_FACE_SUCCESS);
        }
    }

    private final static int GET_FACE_SUCCESS = 1001;
    //消息处理的Handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_FACE_SUCCESS:
                    showFace();
                    break;
                default:
                    break;
            }
        }
    };


    //显示所有的表情
    private void showFace() {
        views.clear();
        if (faceList != null && faceList.size() > 0) {
            int totalPage = 0;
            if (faceList.size() % 21 > 0) {
                totalPage = faceList.size() / 21 + 1;
            }
            Log.e("unfind", "总的页数是：" + totalPage);
            for (int i = 0; i < totalPage; i++) {
                Log.e("unfind", "当前页数：" + i);
                GridView view = new GridView(context);
                ViewPager.LayoutParams lp = new ViewPager.LayoutParams();
                lp.width = ViewPager.LayoutParams.MATCH_PARENT;
                lp.height = ViewPager.LayoutParams.WRAP_CONTENT;
                view.setLayoutParams(lp);
                view.setVerticalSpacing(vSpace);
                view.setNumColumns(7);

                final List<FaceVo> gl = new ArrayList<FaceVo>();
                for (int x = 0; x < 21; x++) {
                    Log.e("unfind", "" + (i * 21 + x));
                    if (i * 21 + x > faceList.size() - 1) {
                        break;
                    } else {
                        gl.add(faceList.get(i * 21 + x));
                    }
                }
                FaceAdapter adapter = new FaceAdapter(gl, context);
                view.setAdapter(adapter);

                view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        FaceVo fv =  gl.get(position);
                        //编辑框中加入文字
//                        content.append(fv.getText());
                        StringBuffer msg = new StringBuffer(content.getText().toString()) ;
                        msg.append(fv.getText());
                        enditShow(msg.toString()) ;

                    }
                });


                views.add(view);
            }
            facePager.setAdapter(new GiftPagerAdapter(views));
            initIndiCator();//初始化选择指示器
        }
    }

    private void enditShow(String msg){
        //表情解析
        CharSequence charSequence = FaceData.getInstance(context).compileStringToDisply(msg);
        content.setText(charSequence);
        content.setSelection(charSequence.length());
    }



    //初始化礼物指示器
    private void initIndiCator() {
        indicatorCon.removeAllViews();
        for (int i = 0; i < views.size(); i++) {
            ImageView img = new ImageView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.height = indicatorSize;
            lp.weight = indicatorSize;
            lp.leftMargin = indicatorLeft;
            img.setLayoutParams(lp);
            img.setImageBitmap(indicatoreNormal);
            indicatorCon.addView(img);
        }
    }

    //选中页数更改指示器
    private void setIndicator(int index) {
        int count = indicatorCon.getChildCount();
        for (int i = 0; i < count; i++) {
            if (index == i) {
                ((ImageView) indicatorCon.getChildAt(i)).setImageBitmap(indicatorSelected);
            } else {
                ((ImageView) indicatorCon.getChildAt(i)).setImageBitmap(indicatoreNormal);
            }
        }
    }


    public interface OnSendMsgListener {
        public void sendMessage(String msg);
        public void sendDmMessage(String msg) ;
    }


}
