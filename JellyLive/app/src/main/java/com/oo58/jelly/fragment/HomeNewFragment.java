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
import android.widget.GridView;

import com.oo58.jelly.R;
import com.oo58.jelly.adapter.HomeNewAdapter;
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description 首页的最新的Fragment
 * @Date 2016/6/14.
 */
public class HomeNewFragment extends Fragment {
    private static final int HandlerCmd_GetNewListSuccess = 10001;
    private static final int HandlerCmd_GetNewListFailed = 10002;
    private static final int HandlerCmd_GetNewListError = 10003;
    private static final int HandlerCmd_GetNewList_NoMoreData = 10004;//没有更多数据

    private static final int HandlerCmd_addMoreDataSuccess = 10005;


    private GridView gridView;//显示的的GridView
    private List<AnchorVo> list;//最新主播的列表
    private PullToRefreshLayout pullToRefreshLayout;
    private HomeNewAdapter hotAdapter;
    private View rootView;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView= inflater.inflate(R.layout.home_new, null);
            pullToRefreshLayout = ((PullToRefreshLayout) rootView.findViewById(R.id.newanchorlayout));
            pullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                    doRefresh();
                }

                @Override
                public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                    getNewAnchorList(true);
                }
            });

            list = new ArrayList<AnchorVo>();
            gridView = (GridView) rootView.findViewById(R.id.new_anchor);
            doRefresh();
        }

        return rootView;
    }

    //加载主播的子线程
    class LoadNewAnchorThread implements Runnable {

        @Override
        public void run() {
            doRefresh();
        }

    }


    private void getNewAnchorList(final boolean addMore) {

        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    if(getActivity()==null){
                        return ;
                    }

                    String result = Util.addRpcEvent(RpcEvent.GetHotList,2);

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
                            //主播的ID
                            String anchorId = anchor.getString("anchors");
                            //房间的ID
                            String roomId = anchor.getString("room_id");
                            //主播的名字
                            String name = anchor.getString("nickname");
                            //头像的URL
                            String faceUrl = anchor.getString("icon");
                            //宣传图片的URL
                            String picUrl = AppUrl.USER_LOGO_ROOT+anchor.getString("poster_url");
                            //观看人数
                            String num = anchor.getString("online");
                            //标题
                            String title = anchor.getString("notice_public");
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
                            handler.sendEmptyMessage(HandlerCmd_GetNewList_NoMoreData);
                        } else {
                            if(addMore){
                                handler.sendEmptyMessage(HandlerCmd_addMoreDataSuccess);
                            }else{
                                handler.sendEmptyMessage(HandlerCmd_GetNewListSuccess);
                            }
                        }
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetNewListFailed);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetNewListError);
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
                case HandlerCmd_GetNewListSuccess:
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    hotAdapter = new HomeNewAdapter(list, getContext());
                    gridView.setAdapter(hotAdapter);
                    break;
                case HandlerCmd_GetNewListFailed:
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    break;
                case HandlerCmd_GetNewListError:
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    break;
                case HandlerCmd_GetNewList_NoMoreData:
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    break;
                case HandlerCmd_addMoreDataSuccess:
                    if (hotAdapter != null) {
                        hotAdapter.notifyDataSetChanged();
                    }
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    break;
            }
        }
    }
    );




    /**
     * 刷新
     */
    public void doRefresh() {
        //清除原有数据再刷新
        getNewAnchorList(false);
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
