package com.oo58.jelly.activity.personal.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;
import com.oo58.jelly.adapter.BlackListAdapter;
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
 * @author zhongxf
 * @Description 黑名单列表
 * @Date 2016/6/21.
 */
public class BlackListActivity extends BaseActivity {
    private static final int HandlerCmd_GetBlackList_Success = 10001 ;
    private static final int HandlerCmd_GetBlackList_Failed = 10002 ;

    private static final int HandlerCMd_Rmove_Success = 10003 ;
    private static final int HandlerCMd_Rmove_Failed = 10004 ;

    private ImageButton returnBtn;//返回按钮
    private TextView title;//顶部标题

    private ListView listView;//显示黑名单的LISTvIEW
    private List<UserVo> list;//黑名单的用户列表
    private BlackListAdapter adapter;//黑名单列表的适配器

    private final static int GET_BLACK_LIST = 1001;//获取黑名单成功
    private final static int DISMISS_BLACK_LIST = 1002;//解除黑名单成功



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.black_list);
        initViews();
        ToastManager.makeToast(BlackListActivity.this, "长按可解除拉黑");
    }

    //初始化界面
    private void initViews() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("黑名单");
        listView = (ListView) findViewById(R.id.black_list);
        list = new ArrayList<UserVo>();
        adapter = new BlackListAdapter(list, BlackListActivity.this);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                UserVo uv = list.get(position) ;
                removeFormBlackList(uv.getUid()) ;
                return false;
            }
        });
        getBlackList();
    }

    //从黑名单
//    class DismissBlackThread implements Runnable {
//
//        public int position;//解除黑名单的List的位置
//
//        public DismissBlackThread(int position) {
//            this.position = position;
//        }
//
//        @Override
//        public void run() {
//            removeFormBlackList() ;
////            list.remove(position);
////            mHandler.sendEmptyMessage(DISMISS_BLACK_LIST);
//
//        }
//    }


    //点击监听
    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.return_btn:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void handler(Message msg) {
        switch (msg.what){
            //获取列表
            case HandlerCmd_GetBlackList_Success:
                adapter.notifyDataSetChanged();
                break ;
            case HandlerCmd_GetBlackList_Failed:
                break ;
            //删除
            case HandlerCMd_Rmove_Success:
                String uid = (String)msg.obj;
                doDelect(uid);

                break ;
            case HandlerCMd_Rmove_Failed:
                break ;
        }
    }


    /**
     * 删除一条黑名单
     * @param uid
     */
    private void doDelect(String uid){
        for (UserVo uv:list){
            if(uv.getUid().equals(uid)){
                list.remove(uv);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取黑名单列表
     */
    private void getBlackList( ) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String openid = GlobalData.getUID(mContext);
                    String secret = GlobalData.getUSecert(mContext);
                    String result = Util.addRpcEvent(RpcEvent.GetMyBlackList, openid, secret);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONArray blackList = obj.getJSONArray("data");
                        for(int i=0; i<blackList.length(); i++){
                            JSONObject black = blackList.optJSONObject(i) ;
                            UserVo uv = new UserVo() ;
                            uv.setName(black.getString("nickname"));
                            uv.setUid(black.getString("id"));
                            uv.setIcon(black.getString("icon"));
                            uv.setGender(black.getInt("gender"));
                            uv.setSign(black.getString("sign"));
                            list.add(uv);
                        }
                        handler.sendEmptyMessage(HandlerCmd_GetBlackList_Success);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetBlackList_Failed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetBlackList_Failed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }


    /**
     * 删除黑名单
     * @param uid
     */
    private void removeFormBlackList(final String uid) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String openid = GlobalData.getUID(mContext);
                    String secret = GlobalData.getUSecert(mContext);
                    String result = Util.addRpcEvent(RpcEvent.CallRemoveBlackList, openid, secret,uid);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        Message msg = new Message();
                        msg.obj = uid ;
                        msg.what = HandlerCMd_Rmove_Success ;
                        handler.sendMessage(msg) ;
                    } else {
                        handler.sendEmptyMessage(HandlerCMd_Rmove_Failed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCMd_Rmove_Failed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }
}
