package com.oo58.jelly.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/14.
 */
public class HomeFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> list;
    private String[] TITLES = {"关注", "热门", "最新"};

    public HomeFragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    /**
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        Log.e("unfind","执行销毁");
    }
}
