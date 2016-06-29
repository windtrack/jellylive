package com.oo58.jelly.activity.personal;


import android.content.Intent;

import android.os.Bundle;
import android.os.Message;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;
import com.oo58.jelly.activity.address.ProvinceListActivity;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.dialog.CheckSexDialog;
import com.oo58.jelly.dialog.CheckUploadHead;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.manager.LocalImageManager;
import com.oo58.jelly.util.RpcRequest;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.UploadHeadUtil;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.view.CircleImageView;
import com.oo58.jelly.view.date.DateChooseWheelViewDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/16.
 */
public class BaseUserInfoActivity extends BaseActivity {
    //修改性别
    private static final int HandlerCmd_EditGenderSuccess = 10001;
    private static final int HandlerCmd_EditGenderFailed = 10002;
    //修改地址
    private static final int HandlerCmd_EditProSuccess = 10003;
    private static final int HandlerCmd_EditProFailed = 10004;


    private CircleImageView imgView_head;//头像
    private TextView textView_name; //名称
    private TextView textView_uid; //id
    private ImageView imageView_gender; //性别
    private TextView textView_pro; //地址
    private TextView textView_sign; //签名

    private ImageButton returnBtn;//返回按钮
    private TextView title;//标题

    private LinearLayout updateNickBtn;//修改昵称按钮
    private LinearLayout updateHeadIcon;//修改头像

    private LinearLayout updateSignedBtn;//修改签名
    private LinearLayout updateSexBtn;//修改性别按钮
    private LinearLayout updateArea;//修改地址

    private LinearLayout ageBtn;//年级选择按钮
    private TextView birthdayDate;//出生的日期

    public DisplayImageOptions mOptions;


    private UploadHeadUtil uploadHeadUtil;//上传头像

    private int curGender;//性别
    private String province;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_base_info);
        initView();
        initData();
    }

    //初始化界面
    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("个人资料");
        updateSignedBtn = (LinearLayout) findViewById(R.id.update_signed);
        updateSignedBtn.setOnClickListener(listen);
        updateHeadIcon = (LinearLayout) findViewById(R.id.update_face);
        updateHeadIcon.setOnClickListener(listen);
        updateNickBtn = (LinearLayout) findViewById(R.id.update_nick);
        updateNickBtn.setOnClickListener(listen);
        updateSexBtn = (LinearLayout) findViewById(R.id.update_sex);
        updateSexBtn.setOnClickListener(listen);
        updateArea = (LinearLayout) findViewById(R.id.update_area);
        updateArea.setOnClickListener(listen);
        ageBtn = (LinearLayout) findViewById(R.id.age);
        ageBtn.setOnClickListener(listen);
        birthdayDate = (TextView) findViewById(R.id.birthday);

        imgView_head = (CircleImageView) findViewById(R.id.imgHead);
        textView_name = (TextView) findViewById(R.id.username);
        textView_uid = (TextView) findViewById(R.id.userid);
        imageView_gender = (ImageView) findViewById(R.id.usergender);
        textView_pro = (TextView) findViewById(R.id.userpro);
        textView_sign = (TextView) findViewById(R.id.usersign);


        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.testpic).cacheInMemory()
                .showImageOnFail(R.mipmap.testpic)
                .showImageForEmptyUri(R.mipmap.testpic)
                .showImageOnLoading(R.mipmap.testpic)
                .cacheOnDisc().build();
    }

    //初始化数据
    private void initData() {

        //名称
        String name = GlobalData.getUName(mContext);
        textView_name.setText(name);
        //头像
        String headUrl = GlobalData.getIcon(mContext);
        GlobalData.getmImageLoader(mContext).displayImage(headUrl, imgView_head, mOptions);
        //uid
        String uid = GlobalData.getUID(mContext);
        textView_uid.setText(uid);
        //性别
        int gender = GlobalData.getGender(mContext);
        LocalImageManager.setGender(mContext, imageView_gender, gender);
        //地址
        String pro = SharedPreUtil.getString(mContext, AppContance.USER_PROVINCE);
        String city = SharedPreUtil.getString(mContext, AppContance.USER_CITY);
        textView_pro.setText(pro + city);
        //签名
        String sign = GlobalData.getSign(mContext);
        textView_sign.setText(sign);

        String birthday = SharedPreUtil.getString(mContext, AppContance.USER_AGE);

        birthdayDate.setText(Util.getAge(birthday) + "岁");

    }

    //点击监听
    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.update_face://头像
                    callUpLoadHead();
                    break;
                case R.id.update_area://地区
//                    startActivity(new Intent(AppAction.ACTION_PROVINCE));
                    Intent diqu = new Intent(BaseUserInfoActivity.this, ProvinceListActivity.class);
                    startActivityForResult(diqu, 101);
                    break;


                case R.id.return_btn://返回
                    finish();
                    break;
                case R.id.update_signed://修改签名
                    startActivity(new Intent(AppAction.ACTION_EDIT_SIGN));
                    break;
                case R.id.update_nick://修改昵称
                    startActivity(new Intent(AppAction.ACTION_UPDATE_NICK));
                    break;
                case R.id.update_sex:
                    CheckSexDialog checkSexDialog = new CheckSexDialog(BaseUserInfoActivity.this, new CheckSexDialog.OnGenderSelectListener() {
                        @Override
                        public void onSelect(int idex) {
                            curGender = idex;
                            int gender = GlobalData.getGender(mContext);
                            if (curGender != gender) {
                                callEditGender(curGender);
                            }
                        }
                    });
                    checkSexDialog.showAtLocation(findViewById(R.id.root), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;

                case R.id.age:
                    DateChooseWheelViewDialog birthDialog = new DateChooseWheelViewDialog(BaseUserInfoActivity.this,
                            new DateChooseWheelViewDialog.DateChooseInterface() {
                                @Override
                                public void getDateTime(String time, boolean longTimeChecked) {
//                                    birthdayDate.setText(time);

                                    int age = Util.getAge(time);
                                    birthdayDate.setText(age + "岁");
                                    callEditAge(time);


                                }
                            });
                    birthDialog.setTimePickerGone(true);
                    birthDialog.setDateDialogTitle("选择出生日期");
                    birthDialog.showDateChooseDialog();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            case HandlerCmd_EditGenderSuccess:
                LocalImageManager.setGender(mContext, imageView_gender, curGender);
                SharedPreUtil.put(mContext, AppContance.GENDER, curGender);
                break;
            case HandlerCmd_EditGenderFailed:
                showToast("更改性别失败");
                break;
            case HandlerCmd_EditProSuccess:
                SharedPreUtil.put(mContext, AppContance.USER_PROVINCE, province);
                SharedPreUtil.put(mContext, AppContance.USER_CITY, city);
                textView_pro.setText(province + city);
                break;
            case HandlerCmd_EditProFailed:
                showToast("更改地区失败");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();//回来的时候 重设数据
    }

    /**
     * 修改性别
     */
    private void callEditGender(final int sex) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String openid = GlobalData.getUID(mContext);
                    String secret = GlobalData.getUSecert(mContext);
                    String result = Util.addRpcEvent(RpcEvent.CallEditGender, openid, secret, sex);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        handler.sendEmptyMessage(HandlerCmd_EditGenderSuccess);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_EditGenderFailed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_EditGenderFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 修改地址
     */
    private void callEditPro(final String pro, final String city) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String openid = GlobalData.getUID(mContext);
                    String secret = GlobalData.getUSecert(mContext);
                    String result = Util.addRpcEvent(RpcEvent.CallSetArea, openid, secret, pro, city);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        handler.sendEmptyMessage(HandlerCmd_EditProSuccess);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_EditProFailed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_EditProFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 设置头像
     */
    private void callUpLoadHead() {
        CheckUploadHead checkUploadHead = new CheckUploadHead(BaseUserInfoActivity.this, new CheckUploadHead.OnUpdateTypeSelectListener() {
            @Override
            public void onSelect(int index) {


                if (uploadHeadUtil == null) {
                    uploadHeadUtil = new UploadHeadUtil(BaseUserInfoActivity.this, new UploadHeadUtil.UploadHeadListener() {
                        @Override
                        public void onUploadSuceess(String iconname) {
                            String icon = AppUrl.USER_LOGO_ROOT + iconname;
                            SharedPreUtil.put(mContext, AppContance.USER_ICON, icon);
                            GlobalData.getmImageLoader(mContext).displayImage(icon, imgView_head, mOptions);
                            callSaveIconName(iconname);
                        }

                        @Override
                        public void onUploadFailed() {

                        }
                    });
                }

                if (index == 0) {
                    uploadHeadUtil.takePhoto();
                }
                if (index == 1) {
                    uploadHeadUtil.invokePhoto();
                }


            }
        });
        checkUploadHead.showAtLocation(findViewById(R.id.root), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    /**
     * 修改地址
     */
    private void callSaveIconName(final String name) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String openid = GlobalData.getUID(mContext);
                    String secret = GlobalData.getUSecert(mContext);
                    String result = Util.addRpcEvent(RpcEvent.CallSaveIconName, openid, secret, name);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
//                        handler.sendEmptyMessage(HandlerCmd_EditProSuccess);
                    } else {
//                        handler.sendEmptyMessage(HandlerCmd_EditProFailed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    handler.sendEmptyMessage(HandlerCmd_EditProFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 修改日期
     *
     * @param date
     */
    private void callEditAge(String date) {

        new RpcRequest(mContext, new RpcRequest.OnRpcRequestListener() {
            @Override
            public void onSuccess(Message msg) {

            }

            @Override
            public void onFailed(Message msg) {

            }

            @Override
            public void onError(Message msg) {

            }
        }).doRequest(RpcEvent.CallEditAge, GlobalData.getUID(mContext), GlobalData.getUSecert(mContext), date);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (uploadHeadUtil != null) {
            uploadHeadUtil.onActivityResult(requestCode, resultCode, data);
        }

        if (resultCode == 21) {
            province = data.getStringExtra("provincename");
            city = data.getStringExtra("cityname");
            StringBuffer sb1 = new StringBuffer("");
            callEditPro(province, city);
        }
    }
}
