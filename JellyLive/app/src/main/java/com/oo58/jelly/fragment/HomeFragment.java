package com.oo58.jelly.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.oo58.jelly.R;
import com.oo58.jelly.adapter.HomeFragmentAdapter;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.view.CircleImageView;
import com.oo58.jelly.view.PagerSlidingTabStrip;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description 主页的Fragment
 * @Date 2016/6/14.
 */
public class HomeFragment extends Fragment implements FragmentOperaInter {

    private ViewPager viewpager;//显示三个Fragment的ViewPager
    private PagerSlidingTabStrip pst;//滑动标题

    private ImageButton searchBtn;//搜索按钮

    private CircleImageView imageViewHead;
    public DisplayImageOptions mOptions;//头像

    private      View view ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if(view == null){
            view = inflater.inflate(R.layout.main_home, null);
            mOptions = new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showStubImage(R.mipmap.testpic)
                    .showImageForEmptyUri(R.mipmap.testpic)
                    .showImageOnFail(R.mipmap.testpic)
                    .showImageOnLoading(R.mipmap.testpic).cacheOnDisc()
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();


            imageViewHead = (CircleImageView)  view.findViewById(R.id.login_face);
            viewpager = (ViewPager) view.findViewById(R.id.main_content);
            pst = (PagerSlidingTabStrip) view.findViewById(R.id.dg_indicator);
            List<Fragment> viewList = new ArrayList<Fragment>();
            //往列表中添加关注Fragment
            HomeFollowFragment followFragment = new HomeFollowFragment();
            followFragment.setFoi(this);
            viewList.add(followFragment);
            //往列表中添加热门Fragment
            viewList.add(new HomeHotFragment());
            //往列表中添加最新Fragment
            viewList.add(new HomeNewFragment());
            viewpager.setAdapter(new HomeFragmentAdapter(getChildFragmentManager(), viewList));
            pst.setViewPager(viewpager);
            pst.setSelectedTextColorResource(R.color.blackthreef);
            pst.setIndicatorColorResource(R.color.blackthreef);
            pst.setTextSize(getContext().getResources().getDimensionPixelSize(
                    R.dimen.livehall_tab_textsize));
            viewpager.setCurrentItem(1);

            searchBtn = (ImageButton) view.findViewById(R.id.search_btn);
            searchBtn.setOnClickListener(listen);

            GlobalData.getmImageLoader(getActivity()).displayImage(GlobalData.getIcon(getActivity()),imageViewHead,mOptions);

//        }
        return view;
    }


    //点击的监听
    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.search_btn:
                    startActivity(new Intent(AppAction.ACTION_SEARCH));
                default:
                    break;
            }
        }
    };
    @Override
    public void jumpNew() {
        viewpager.setCurrentItem(0);
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
}
