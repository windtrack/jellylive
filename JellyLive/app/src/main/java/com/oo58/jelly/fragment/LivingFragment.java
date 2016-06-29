package com.oo58.jelly.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oo58.jelly.R;

/**
 * @author zhongxf
 * @Description 直播的Fragment
 * @Date 2016/6/14.
 */
public class LivingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_living,null);

        return view;
    }
}
