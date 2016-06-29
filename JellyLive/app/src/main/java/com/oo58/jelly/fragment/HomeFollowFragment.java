package com.oo58.jelly.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.oo58.jelly.R;
import com.oo58.jelly.adapter.HomeHotAdapter;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.AnchorVo;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.UIHandler;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.view.refreshview.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/14.
 */
public class HomeFollowFragment extends Fragment {

    private static final int HandlerCmd_GetListSuccess = 10001;
    private static final int HandlerCmd_GetListFailed = 10002;
    private static final int HandlerCmd_GetListError = 10003;
    private static final int HandlerCmd_GetList_NoMoreData = 10004;//没有更多数据
    private static final int HandlerCmd_AddMoreDataSuccess = 10005;//没有更多数据

    private FragmentOperaInter foi;//Fragment的跳转的接口
    private LinearLayout noFllowCon;//显示没有关注的信息
    private ListView followListView;//关注信息的ListView
    private List<AnchorVo> list;//关注的主播的列表
    private ImageButton goNewBtn;//跳转到最新的按钮
    private final static int GET_FOLLOW_ANCHOR = 1001;//获取关注的主播的列表
    private View headView;//滚动头部的VIew

    private int curPageIndex;
    private HomeHotAdapter homeHotAdapter;
    private PullToRefreshLayout pullToRefreshLayout;

    //设置跳转接口
    public void setFoi(FragmentOperaInter foi) {
        this.foi = foi;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_FOLLOW_ANCHOR:
                    followListView.removeHeaderView(headView);
                    //根据关注主播的列表来判断是否显示无关注信息
                    if (list.size() > 0) {
                        followListView.setVisibility(View.VISIBLE);
                        noFllowCon.setVisibility(View.GONE);
                    } else {
                        noFllowCon.setVisibility(View.VISIBLE);
                        followListView.setVisibility(View.VISIBLE);
                    }
                    followListView.addHeaderView(headView);

                    if (homeHotAdapter == null) {
                        homeHotAdapter = new HomeHotAdapter(list, getContext());
                        followListView.setAdapter(homeHotAdapter);
                    } else {
                        homeHotAdapter.notifyDataSetChanged();
                    }

                    break;
                default:
                    break;
            }
        }
    };
    private View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.home_follow, null);
            pullToRefreshLayout = ((PullToRefreshLayout) rootView.findViewById(R.id.folowlistView));
            pullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                    doRefresh();
                }

                @Override
                public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                    getFollowList(true);
                }
            });
            followListView = (ListView) rootView.findViewById(R.id.follow_list);
            list = new ArrayList<AnchorVo>();


            headView = LayoutInflater.from(getContext()).inflate(R.layout.follow_head, null);
            noFllowCon = (LinearLayout) headView.findViewById(R.id.no_follow_con);
            goNewBtn = (ImageButton) headView.findViewById(R.id.go_new);
            goNewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    foi.jumpNew();
                }
            });

            doRefresh();
        }

        return rootView;
    }

    //加载关注主播的子线程
    class LoadNewAnchorThread implements Runnable {
        @Override
        public void run() {
            doRefresh();
        }
    }


    /**
     * 获取热门主播列表
     */
    private void getFollowList(final boolean addMore) {

        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    if(getActivity()==null){
                        ThreadPoolWrap.getThreadPool().removeTask(this);
                        return ;
                    }
                    String openid = GlobalData.getUID(getActivity());
                    String secret = GlobalData.getUSecert(getActivity());

                    String result = Util.addRpcEvent(RpcEvent.GetFollowList, openid, secret);

                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        if (!addMore) {
                            list.clear();
                        }

                        JSONObject roominfo = obj.getJSONObject("data");
                        JSONArray hotlist = roominfo.getJSONArray("rooms");
                        for (int i = 0; i < hotlist.length(); i++) {
                            JSONObject anchor = hotlist.optJSONObject(i);
                            String anchorId = anchor.getString("anchors");//主播的ID
                            String roomId = anchor.getString("room_id");
                            ;//房间的ID
                            String name = anchor.getString("nickname");
                            ;//主播的名字
                            String faceUrl = anchor.getString("icon");
                            ;//头像的URL
                            String picUrl = AppUrl.USER_LOGO_ROOT+anchor.getString("poster_url");
                            ;//宣传图片的URL
                            String num = anchor.getString("online");
                            ;//观看人数
                            String title = anchor.getString("title");
                            ;//标题
                            String url = anchor.getString("live_url");
                            String stream = anchor.getString("stream_name");
                            String streamUrl = url + "/" + stream;
                            AnchorVo vo = new AnchorVo();
                            vo.setAnchorId(anchorId);
                            vo.setRoomId(roomId);
                            vo.setName(name);
                            vo.setFaceUrl(faceUrl);
                            vo.setPicUrl(picUrl);
                            vo.setNum(num);
                            vo.setTitle(title);
                            vo.setLiveStream(streamUrl);
                            list.add(vo);
                        }

                        if (hotlist.length() == 0) {
                            handler.sendEmptyMessage(HandlerCmd_GetList_NoMoreData);
                        } else {
                            if (addMore) {
                                handler.sendEmptyMessage(HandlerCmd_AddMoreDataSuccess);
                            } else {
                                handler.sendEmptyMessage(HandlerCmd_GetListSuccess);
                            }

                        }

                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetListFailed);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetListError);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };

        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }


    private UIHandler handler = new UIHandler(Looper.getMainLooper(), new UIHandler.IHandler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerCmd_GetListSuccess:
                    homeHotAdapter = new HomeHotAdapter(list, getContext());
                    followListView.setAdapter(homeHotAdapter);
                    curPageIndex++;
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    break;
                case HandlerCmd_GetListFailed:
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    break;
                case HandlerCmd_GetListError:
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    break;
                case HandlerCmd_GetList_NoMoreData:
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    break;
                case HandlerCmd_AddMoreDataSuccess:
                    if (homeHotAdapter != null) {
                        homeHotAdapter.notifyDataSetChanged();
                    }
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    break;
            }
        }
    });

    /**
     * 刷新
     */
    public void doRefresh() {
        //清除原有数据再刷新
        curPageIndex = 1;
        getFollowList(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();

//        try {
//            Field childFragmentManager = Fragment.class
//                    .getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        handler.pause();
//        ViewGroup localViewGroup = (ViewGroup) this.rootView.getParent();
//        if (localViewGroup != null)
//            localViewGroup.removeView(this.rootView);
    }


    @Override
    public void onPause() {
        super.onPause();
        handler.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.resume();
    }
}
