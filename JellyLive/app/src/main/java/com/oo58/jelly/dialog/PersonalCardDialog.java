package com.oo58.jelly.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.oo58.jelly.R;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.util.RpcRequest;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.manager.LocalImageManager;
import com.oo58.jelly.util.BaseViewHolder;
import com.oo58.jelly.util.FollowListener;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.UIUtil;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.view.CircleImageView;


/**
 * @author zhongxf
 * @Description 个人资料卡
 * @Date 2016/6/22.
 */
public class PersonalCardDialog {

    private Dialog cardDialog;//个人资料卡
    private Context mContext;//上下文
    private LayoutInflater mLayoutInflater;//视图加载器
    private int SCREEN_WIDTH = 0;
    public DisplayImageOptions mOptions;
    private UserVo userVo;
    private OnPersonalViewListener onPersonalViewListener;
    private Button followBtn;//关注按钮

    private ManagerDialog managerDialog;

    private String roomid ;
    public PersonalCardDialog(Context cxt,OnPersonalViewListener listener) {
        this.mContext = cxt;
        onPersonalViewListener = listener;
        this.mLayoutInflater = LayoutInflater.from(cxt);
        SCREEN_WIDTH = cxt.getResources().getDisplayMetrics().widthPixels;
        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.testpic).cacheInMemory()
                .showImageOnFail(R.mipmap.testpic)
                .showImageForEmptyUri(R.mipmap.testpic)
                .showImageOnLoading(R.mipmap.testpic)
                .cacheOnDisc().build();
    }

    /**
     * 显示一个等待框
     * param:context：上下文      isAnchor：是否是主播     true是主播    false不是主播
     */
    public void show(boolean isAnchor, UserVo user,String roomid) {
        creatDialog(isAnchor, user,roomid);
    }

    private View main;

    private void creatDialog(boolean isAnchor, final UserVo user,String roomid) {
        if (main == null) {
            main = mLayoutInflater.inflate(R.layout.personal_card, null);
        }
        userVo = user;
        this.roomid = roomid ;
        //设置信息
        CircleImageView headiconView = BaseViewHolder.get(main, R.id.face);
        TextView textView_name = BaseViewHolder.get(main, R.id.name);
        ImageView imageView_gender = BaseViewHolder.get(main, R.id.sex_icon);
        ImageView imageView_lev = BaseViewHolder.get(main, R.id.label_icon);
        TextView textView_uid = BaseViewHolder.get(main, R.id.userid);
        TextView textView_sign = BaseViewHolder.get(main, R.id.usersign);
        TextView textView_follow = BaseViewHolder.get(main, R.id.follownum);
        TextView textView_fans = BaseViewHolder.get(main, R.id.fansNum);


        //头像
        GlobalData.getmImageLoader(mContext).displayImage(user.getIcon(), headiconView, mOptions);
        //名称
        textView_name.setText(user.getName());
        //性别
        LocalImageManager.setGender(mContext, imageView_gender, user.getGender());
        //等级
        LocalImageManager.setWealthLev(mContext, imageView_lev, user.getViplev());
        //果冻号
        textView_uid.setText("果冻号:" + user.getUid());
        //签名
        if (Util.isEmpty(user.getSign())) {
            textView_sign.setText("TA好像忘记写签名了");
        } else {
            textView_sign.setText(user.getSign());
        }
        //关注
        textView_follow.setText("关注 :" + user.getFollowCount());
        //粉丝
        textView_fans.setText("粉丝 :" + user.getFansCount());


        ImageButton closeBtn = BaseViewHolder.get(main, R.id.close_btn);//关闭按钮
        closeBtn.setOnClickListener(btlistener);
        Button reportBtn = BaseViewHolder.get(main, R.id.top_report_btn);//举报按钮
        reportBtn.setOnClickListener(btlistener);
        Button manageBtn = BaseViewHolder.get(main, R.id.top_manage_btn);//管理按钮
        manageBtn.setOnClickListener(btlistener);
        followBtn = BaseViewHolder.get(main, R.id.follow_btn);//关注按钮
        followBtn.setOnClickListener(new FollowListener(mContext, userVo, null, followBtn, 1));

        Button sixinBtn = BaseViewHolder.get(main, R.id.sixin_btn);//私信按钮
        sixinBtn.setOnClickListener(btlistener);
        Button indexBtn = BaseViewHolder.get(main, R.id.index_btn);//主页按钮
        indexBtn.setOnClickListener(btlistener);

        onChangerFollowBT(userVo.isFollow());

        if (isAnchor) {//如果是主播
            manageBtn.setVisibility(View.VISIBLE);
            reportBtn.setVisibility(View.GONE);
        } else {//如果不是主播
            manageBtn.setVisibility(View.GONE);
            reportBtn.setVisibility(View.VISIBLE);
        }
        //如果打开的是自己界面
        if(GlobalData.getUID(mContext).equals(userVo.getUid())){
            manageBtn.setVisibility(View.GONE);
            reportBtn.setVisibility(View.GONE);
        }


        closeDialog();//调用关闭方法，防止多层显示

        if (cardDialog == null) {
            cardDialog = new Dialog(mContext, R.style.loading_dialog);// 创建自定义样式dialog
            cardDialog.setCancelable(true);// 是否可以用返回键取消
            LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            fill_parent.width = SCREEN_WIDTH - UIUtil.dip2px(mContext, 40);
            cardDialog.setContentView(main, fill_parent);// 设置布局
        }
        cardDialog.show();
    }

    private View.OnClickListener btlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.close_btn:
                    closeDialog();
                    if(onPersonalViewListener!=null){
                        onPersonalViewListener.onClosePersonView(userVo);
                    }
                    break;
                case R.id.top_report_btn:
                    doReport();
                    break;
                case R.id.top_manage_btn:
                    showManagerView(userVo);
                    break;
                case R.id.follow_btn:
                    break;
                case R.id.sixin_btn:
                    doPrivateMsg();
                    break;
                case R.id.index_btn:
                    doPrivateMainPage();
                    break;
            }
        }
    };

    /**
     * 举报
     */
    private void doReport() {
        new RpcRequest(mContext, new RpcRequest.OnRpcRequestListener() {
            @Override
            public void onSuccess(Message msg) {
                ToastManager.makeToast(mContext, "举报成功");
            }

            @Override
            public void onFailed(Message msg) {
            }

            @Override
            public void onError(Message msg) {
            }
        }).doRequest(RpcEvent.CallReport, GlobalData.getUID(mContext), GlobalData.getUSecert(mContext), userVo.getUid());
    }


    /**
     * 关注
     */
    private void doFollow() {

    }

    /**
     * 私信
     */
    private void doPrivateMsg() {
        if(GlobalData.getUID(mContext).equals(userVo.getUid())){
            ToastManager.makeToast(mContext,"不能私信自己");
            return ;
        }
    }

    /**
     * 主页
     */
    private void doPrivateMainPage() {
        Intent intent = new Intent(AppAction.ACTION_OTHER_USER);
        intent.putExtra("uid", userVo.getUid());
        mContext.startActivity(intent);
    }


    public void onChangerFollowBT(boolean isFollow) {
        followBtn.setText(isFollow ? "已关注" : "关注");
    }

    /**
     * 关闭等待层
     */
    public void closeDialog() {
        if (cardDialog != null)
            cardDialog.dismiss();
    }


    /**
     * 显示管理界面
     *
     * @param userVo
     */
    protected void showManagerView(UserVo userVo) {
        if (managerDialog == null) {
            managerDialog = new ManagerDialog(mContext);
        }
        managerDialog.show(userVo,roomid);
    }

    public interface OnPersonalViewListener {
        public void onClosePersonView(UserVo userVo) ;
    }

}
