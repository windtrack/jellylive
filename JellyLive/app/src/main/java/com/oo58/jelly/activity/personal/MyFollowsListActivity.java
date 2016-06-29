package com.oo58.jelly.activity.personal;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;
import com.oo58.jelly.adapter.FollowsAdapter;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * Created by sunjinfang on 2016/6/20 18:03.
 */
public class MyFollowsListActivity extends BaseActivity {
    private static final int HandlerCmd_GetFollowListSuccess = 10001;
    private static final int HandlerCmd_GetFollowListFailed = 10002;

    private ImageButton returnBtn;
    private TextView title;
    private List<UserVo> listFollow;
    private FollowsAdapter followsAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_list);
        initView();
        initData();
    }

    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("关注列表");
        listView = (ListView) findViewById(R.id.rank_list);

    }

    private void initData() {
        listFollow = new ArrayList<UserVo>();
        followsAdapter = new FollowsAdapter(mContext,listFollow) ;
        listView.setAdapter(followsAdapter);
        getFollowList();


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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void handler(Message msg) {
        switch (msg.what){
            case HandlerCmd_GetFollowListSuccess:
//                followsAdapter = new FollowsAdapter(mContext, listFollow) ;
//                listView.setAdapter(followsAdapter);
                followsAdapter.notifyDataSetChanged();
                break ;
            case HandlerCmd_GetFollowListFailed:
                ToastManager.makeToast(mContext,"获取关注失败");
                break ;
        }
    }

    /**
     * 关注列表
     */
    private void getFollowList() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String openid = GlobalData.getUID(mContext);
                    String secret = GlobalData.getUSecert(mContext);
                    String result = Util.addRpcEvent(RpcEvent.GetFollowList, openid, secret, 2,1);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        listFollow.clear();
                        JSONObject data = obj.getJSONObject("data");
                        JSONArray fansList = data.getJSONArray("rooms");
                        for (int i=0; i<fansList.length(); i++){
                            JSONObject fans = fansList.optJSONObject(i) ;
                            UserVo uv = new UserVo();
                            uv.setName(fans.getString("nickname"));
                            uv.setUid(fans.getString("anchors"));
                            uv.setGender(fans.getInt("gender"));
                            uv.setViplev(fans.getInt("level"));
                            uv.setFollow(fans.getInt("is_follow")==0?false:true);
                            uv.setIcon(AppUrl.USER_LOGO_ROOT+fans.getString("icon"));
                            listFollow.add(uv) ;
                        }

                        handler.sendEmptyMessage(HandlerCmd_GetFollowListSuccess);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetFollowListFailed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetFollowListFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }
}
