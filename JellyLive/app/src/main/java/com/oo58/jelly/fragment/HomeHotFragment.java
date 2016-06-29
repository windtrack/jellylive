package com.oo58.jelly.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.oo58.jelly.R;
import com.oo58.jelly.adapter.BannerAdapter;
import com.oo58.jelly.adapter.HomeHotAdapter;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.AnchorVo;
import com.oo58.jelly.entity.Banner;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.UIHandler;
import com.oo58.jelly.util.UIUtil;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.view.refreshview.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zhongxf
 * @Description 首页的热门的Fragment
 * @Date 2016/6/14.
 */
public class HomeHotFragment extends Fragment {
    private static final int HandlerCmd_GetHotListSuccess = 10001;
    private static final int HandlerCmd_GetHotListFailed = 10002;
    private static final int HandlerCmd_GetHotListError = 10003;
    private static final int HandlerCmd_GetHotList_NoMoreData = 10004;//没有更多数据
    private static final int HandlerCmd_AddMoreDataSuccess = 10007;

    private static final int HandlerCmd_GetBannerSuccess = 10005;
    private static final int HandlerCmd_GetBannerFailed = 10006;

    private ViewPager viewPager;//广告的ViewPager
    private List<Banner> bannerList;//广告的列表
    private ListView listView;//热门主播的ListView
    private List<AnchorVo> list;//热门主播的列表

    private BannerAdapter bannarAdapter;
    private HomeHotAdapter hotAdapter;
    private int curPageIndex;
    private int SCREEN_WIDTH = 0;//屏幕的宽度


    private final static int GET_BANNER = 1001;//获取广告成功
    private final static int GET_HOT_ANCHOR = 1002;//获取热门主播成功

    private Timer bannerTimer;//顶部Banner自动滚动的定时器
    private int bannerPager = 0;
    private PullToRefreshLayout pullToRefreshLayout;

    private View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if(rootView == null){
            rootView = inflater.inflate(R.layout.home_hot, null);
            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            SCREEN_WIDTH = dm.widthPixels;
            pullToRefreshLayout = ((PullToRefreshLayout) rootView.findViewById(R.id.hotrefreshview));
            pullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                    doRefresh();
                }

                @Override
                public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                    getHotList(true);
                }
            });
            pullToRefreshLayout.setTellLoadState(false);

            listView = (ListView) rootView.findViewById(R.id.hot_list);
            list = new ArrayList<AnchorVo>();

            viewPager = new ViewPager(this.getContext());
            ListView.LayoutParams lp = new ListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
            lp.width = SCREEN_WIDTH;
            lp.height = UIUtil.dip2px(getContext(), 103);
            viewPager.setLayoutParams(lp);
            listView.addHeaderView(viewPager);

            viewPager.setOffscreenPageLimit(4);

            bannerList = new ArrayList<Banner>();


//        }
        getBannerList();
        doRefresh();
        return rootView;
    }



    //子线程加载广告
    class LoadBannerThread implements Runnable {
        @Override
        public void run() {
            getBannerList();
        }
    }



    //顶部广告自动滚动
    private void scrollBanner() {
        if (bannerList.size() >= 2) {
            bannerTimer = new Timer();
            bannerTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    bannerPager = bannerPager + 1;
                    if (bannerPager > bannerList.size() - 1) {
                        bannerPager = 0;
                    }
                    viewPager.setCurrentItem(bannerPager);
                }
            }, 5000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("unfind", "onDesory");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("unfind", "onStop");
    }


    /**
     * 刷新
     */
    public void doRefresh() {
        //清除原有数据再刷新
        curPageIndex = 1;

        getHotList(false);
    }

    private void getBannerList() {
        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    String result = Util.addRpcEvent(RpcEvent.GetHotBanner);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        bannerList.clear();
                        JSONArray allBanner = obj.getJSONArray("data");
                        for (int i = 0; i < allBanner.length(); i++) {
                            JSONObject banner = allBanner.optJSONObject(i);
                            String act = banner.getString("act");
                            String room_id = banner.getString("room_id");
                            String url = banner.getString("url");
                            String img = banner.getString("img");

                            Banner ba = new Banner();
                            ba.setAct(act);
                            ba.setRoom_id(room_id);
                            ba.setUrl(url);
                            ba.setImg(img);
                            bannerList.add(ba);
                        }
                        handler.sendEmptyMessage(HandlerCmd_GetBannerSuccess);


                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetBannerFailed);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetBannerFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };

        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }


    /**
     * 获取热门主播列表
     */
    private void getHotList(final boolean addMore) {

        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    String result = Util.addRpcEvent(RpcEvent.GetHotList, 1);
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
                            String faceUrl =  AppUrl.USER_LOGO_ROOT + anchor.getString("icon");
                            //宣传图片的URL
                            String picUrl = AppUrl.USER_LOGO_ROOT + anchor.getString("poster_url");
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
                            handler.sendEmptyMessage(HandlerCmd_GetHotList_NoMoreData);
                        } else {
     
                            if (addMore) {
                                handler.sendEmptyMessage(HandlerCmd_AddMoreDataSuccess);
                            } else {
                                handler.sendEmptyMessage(HandlerCmd_GetHotListSuccess);
                            }

                        }

                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetHotListFailed);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetHotListError);
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
                case HandlerCmd_GetHotListSuccess:

                    hotAdapter = new HomeHotAdapter(list, getActivity());
                    listView.setAdapter(hotAdapter);
                    curPageIndex++;
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    break;
                case HandlerCmd_GetHotListFailed:
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    break;
                case HandlerCmd_GetHotListError:
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    break;
                case HandlerCmd_GetHotList_NoMoreData:
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    break;
                case HandlerCmd_GetBannerSuccess:
                    if (bannarAdapter == null) {
                        bannarAdapter = new BannerAdapter(bannerList, getActivity());
                        viewPager.setAdapter(bannarAdapter);
                    } else {
                        bannarAdapter.notifyDataSetChanged();
                    }
                    break;
                case HandlerCmd_GetBannerFailed:
                    break;
                case HandlerCmd_AddMoreDataSuccess:
                    if(hotAdapter!=null){
                        hotAdapter.notifyDataSetChanged();
                    }
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    break;
            }
        }
    });
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
