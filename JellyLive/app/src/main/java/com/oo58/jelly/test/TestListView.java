package com.oo58.jelly.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.oo58.jelly.R;
import com.oo58.jelly.view.listview.OnRefreshListener;
import com.oo58.jelly.view.listview.RefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/14.
 */
public class TestListView extends Activity {
    private RefreshListView listView;
    private List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        listView = (RefreshListView) findViewById(R.id.listview);
        list = new ArrayList<String>();
        for (int i = 0;i<100;i++){
            list.add("测试"+i);
        }

        listView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onDownPullRefresh() {
                Log.e("unfind","下拉刷新调用");
                listView.hideHeaderView();
            }
            @Override
            public void onLoadingMore() {
                Log.e("unfind","上拉加载调用");
                listView.hideFooterView();
            }
        });
        listView.setAdapter(new TestAdapter(list,TestListView.this));
    }
}
