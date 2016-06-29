package com.oo58.jelly.activity.personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;
import com.oo58.jelly.adapter.RankListAdapter;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.Gift;
import com.oo58.jelly.entity.RankListVo;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.UIUtil;
import com.oo58.jelly.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zhongxf
 * @Description 直播的排行界面
 * @Date 2016/6/16.
 */
public class RankListActivity extends BaseActivity {

    private static final int HandlerCmd_GetRankList_Success = 10001 ;
    private static final int HandlerCmd_GetRankList_Failed = 10002 ;

    private ImageButton returnBtn;
    private TextView title;

    private ListView listView;//显示观众排行的列表
    private List<RankListVo> list;//观众排行的列表
    private RankListAdapter adapter ;//观众排行的适配器

    private String userid ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userid = intent.getStringExtra("uid") ;

        setContentView(R.layout.rank_list);
        initViews();
    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what){
            case HandlerCmd_GetRankList_Success:
                adapter.notifyDataSetChanged();
                break ;
            case HandlerCmd_GetRankList_Failed:

                break ;
        }
    }

    //初始化View
    private void initViews() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("粉丝贡献榜");

        listView = (ListView) findViewById(R.id.rank_list);
        list = new ArrayList<RankListVo>();
        View view = LayoutInflater.from(RankListActivity.this).inflate(R.layout.rank_list_head, null);
        listView.addHeaderView(view);
        adapter = new RankListAdapter(list, RankListActivity.this);
        listView.setAdapter(adapter);
//        new Thread(new LoadRankListThread()).start();
        getRankList(userid) ;
    }

    //加载排行榜数据的子线程
    class LoadRankListThread implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                RankListVo vo = new RankListVo();
                vo.setPosition(i);
                vo.setMoney("1000");
                vo.setLabel(10);
                vo.setSex(i % 2);
                vo.setName("测试" + i);
                list.add(vo);
            }
//            mHandler.sendEmptyMessage(GET_RANK_SUCCESS);
        }
    }

    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.return_btn:
                    finish();
                    break;
            }
        }
    };



    /**
     * 活动贡献排行
     */
    public  void getRankList(final String uid){
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String id = GlobalData.getUID(mContext) ;
                    String secerts = GlobalData.getUSecert(mContext) ;
                    String result = Util.addRpcEvent(RpcEvent.GetConRankList,id,secerts,uid,1,100);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONArray rankList = obj.getJSONArray("data");
                        list.clear();
                        for(int i=0; i<rankList.length() ; i++){
                            JSONObject rank = rankList.optJSONObject(i) ;
                            RankListVo lv = new RankListVo();
                            lv.setName(rank.getString("rank"));
                            lv.setSex(rank.getInt("rank"));
                            lv.setLabel(rank.getInt("level"));
                            lv.setMoney(rank.getString("score"));
                            list.add(lv);
                        }
                        handler.sendEmptyMessage(HandlerCmd_GetRankList_Success);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetRankList_Failed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetRankList_Failed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

}
