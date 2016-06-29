package com.oo58.jelly.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.adapter.GiftListAdapter;
import com.oo58.jelly.adapter.GiftPagerAdapter;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.Gift;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.ConsumpUtil;
import com.oo58.jelly.util.RpcRequest;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.UIUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description 礼物的Dialog
 * @Date 2016/6/23.
 */
public class GiftDialog {

    public Context context;

    private Dialog dialog;//个人资料卡
    private LayoutInflater mLayoutInflater;//视图加载器
    private int SCREEN_WIDTH = 0;
    private int SCREEN_HEIGHT = 0;


    private List<Gift> list;//礼物的列表
    private List<View> views;//放置礼物的View的

    private GiftPagerAdapter pagerAdapter;//礼物翻页的适配器
    private ViewPager viewPager;//显示礼物的viewPager
    private LinearLayout indicatorCon;//ViewPager的指示器的容器

    private Bitmap indicatorSelected;
    private Bitmap indicatoreNormal;

    private int indicatorSize = 0;//指示器的宽度
    private int indicatorLeft = 0;//左边的距离


    private Button button_send ;


    private Gift curGift ;


    private String anchorid ;
    private String giftid;
    private int count ;
    private String roomid;
    private String from_backpack;
    private boolean isGuard ;


    private final static int GET_GIFT_LIST_SUCCESS = 1001;//获取礼物成功
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_GIFT_LIST_SUCCESS:
                    showGift();
                    break;
                default:
                    break;
            }
        }
    };


    public GiftDialog(Context context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;

        Rect outRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        SCREEN_WIDTH = outRect.width();
        SCREEN_HEIGHT = outRect.height();

        indicatorSize = UIUtil.dip2px(context, 9);
        indicatorLeft = UIUtil.dip2px(context, 5);

        indicatoreNormal = BitmapFactory.decodeResource(context.getResources(), R.mipmap.indicator_gray);
        indicatorSelected = BitmapFactory.decodeResource(context.getResources(), R.mipmap.indicator_yellow);

    }

    public void init(String anchorid,String roomid){
        this.anchorid = anchorid;
        this.roomid = roomid ;
    }


    /**
     * 显示一个等待框
     * param:context：上下文      isAnchor：是否是主播     true是主播    false不是主播
     */
    public void show() {
        creatDialog();
    }

    private View main ;
    TextView rechargeBtn ;
    private void creatDialog() {


        if(main == null){
            main = mLayoutInflater.inflate(R.layout.gift_dialog, null);



            indicatorCon = (LinearLayout) main.findViewById(R.id.view_pager_selected_con);
            list = new ArrayList<Gift>();
            views = new ArrayList<View>();
            viewPager = (ViewPager) main.findViewById(R.id.gift_pager);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

            rechargeBtn = (TextView) main.findViewById(R.id.recharge_btn);
            rechargeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(AppAction.ACTION_MY_ACCOUNT));
                }
            });

            SpannableStringBuilder builder = new SpannableStringBuilder(rechargeBtn.getText().toString());
            ForegroundColorSpan yellowSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.yellow_words));
            builder.setSpan(yellowSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            rechargeBtn.setText(builder);

            button_send = (Button) main.findViewById(R.id.send_gift_btn) ;

            button_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(curGift!=null){
                        String beans = GlobalData.getUBeans(context);
                        boolean canBuy = false ;
                        if (ConsumpUtil.compare(beans, curGift.getPrice()+ "")) {
                            canBuy = true;
                            doSendGift(curGift) ;
                        }else{
                            ToastManager.makeToast(context,"乐币不足");
                        }
                    }

                }
            });

            getGiftList() ;//获取数据
        }

        closeDialog();//调用关闭方法，防止多层显示

        if(dialog == null){
            dialog = new Dialog(context, R.style.send_msg);// 创建自定义样式dialog
            dialog.setCancelable(true);// 是否可以用返回键取消
            LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            fill_parent.width = SCREEN_WIDTH;
            fill_parent.height = SCREEN_HEIGHT;
            dialog.setContentView(main, fill_parent);// 设置布局
        }

        dialog.show();

    }


    private void getGiftList(){

        updateBeans();


        new RpcRequest(context, new RpcRequest.OnRpcRequestListener() {
            @Override
            public void onSuccess(Message msg) {

                String reslut = (String) msg.obj ;
                try {
                    JSONObject data =  new JSONObject(reslut) ;
                    JSONArray obj = new JSONArray(data.getString("data")) ;
                    list.clear();
                    for(int i=0; i<obj.length(); i++){

                        JSONObject item = obj.optJSONObject(i) ;
                        Gift gift = new Gift() ;
                        gift.setId(item.getString("id"));
                        gift.setName(item.getString("name"));
                        gift.setPrice(item.getInt("price"));
                        gift.setIcon(AppUrl.GIFT_PATH+item.getString("icon"));
                        gift.setEffect(item.getString("effect"));
                        gift.setAlt_price(item.getInt("alt_price"));
                        gift.setCategory(item.getInt("category"));
                        gift.setReceived_beans(item.getInt("received_beans"));
                        gift.setDescription(item.getString("description"));
                        list.add(gift);
                    }
                    showGift();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(Message msg) {
                ToastManager.makeToast(context,"获取列表失败");
            }

            @Override
            public void onError(Message msg) {
                ToastManager.makeToast(context,"获取列表失败");
            }
        }).doRequest(RpcEvent.GetGiftList, GlobalData.getUID(context),GlobalData.getUSecert(context));



    }


    //显示所有的礼物
    private void showGift() {
        int totalPage = 0;
        if (list.size() % 8 > 0) {
            totalPage = list.size() / 8 + 1;
        }
        Log.e("unfind", "总的页数是：" + totalPage);

        for (int i = 0; i < totalPage; i++) {
            Log.e("unfind", "当前页数：" + i);
            GridView view = new GridView(context);
            ViewPager.LayoutParams lp = new ViewPager.LayoutParams();
            lp.width = ViewPager.LayoutParams.MATCH_PARENT;
            lp.height = ViewPager.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(lp);
            view.setNumColumns(4);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            final List<Gift> gl = new ArrayList<Gift>();
            for (int x = 0; x < 8; x++) {
                Log.e("unfind", "" + (i * 8 + x));
                if (i * 8 + x > list.size() - 1) {
                    break;
                } else {
                    gl.add(list.get(i * 8 + x));
                }
            }

            final GiftListAdapter adapter = new GiftListAdapter(gl, context);
            view.setAdapter(adapter);

            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    adapter.setSelectIndex(position);

                    Gift gift = gl.get(position) ;
                    curGift = gift ;

                }
            });
            views.add(view);
        }

        viewPager.setAdapter(new GiftPagerAdapter(views));
        initIndiCator();//初始化


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


    /**
     * 关闭弹出框
     */
    public void closeDialog() {
        if (dialog != null)
            dialog.dismiss();

    }




    private void doSendGift(final Gift gift){
        new RpcRequest(context, new RpcRequest.OnRpcRequestListener() {
            @Override
            public void onSuccess(Message msg) {

//                int beans = GlobalData.getUBeans(context);
//                int price = gift.getPrice();
//                int curBeans = beans - price ;
//                SharedPreUtil.put(context, AppContance.USER_BEANS,curBeans);

                 String result = (String) msg.obj ;
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject data = obj.getJSONObject("data") ;
                    String beans = data.getString("beans");

                    SharedPreUtil.put(context, AppContance.USER_BEANS,beans);

                    updateBeans() ;


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ToastManager.makeToast(context,"送礼成功");

                SharedPreUtil.put(context,AppContance.USER_FAST_GIFT_ID,gift.getId());
                SharedPreUtil.put(context,AppContance.USER_FAST_GIFT_ICON,gift.getIcon());
                SharedPreUtil.put(context,AppContance.USER_FAST_GIFT_RPICE,gift.getPrice());

            }

            @Override
            public void onFailed(Message msg) {
                ToastManager.makeToast(context,"送礼失败");
            }

            @Override
            public void onError(Message msg) {
                ToastManager.makeToast(context,"送礼失败");
            }
        }).doRequest(RpcEvent.CallSendGift,GlobalData.getUID(context),GlobalData.getUSecert(context),anchorid,curGift.getId(),1,roomid,false,false);
    }


    private void updateBeans(){
        rechargeBtn.setText("充值："+GlobalData.getUBeans(context));
    }
}
