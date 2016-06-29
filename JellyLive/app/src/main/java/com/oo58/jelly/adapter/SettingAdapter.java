package com.oo58.jelly.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.util.UIUtil;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/18.
 */
public class SettingAdapter extends BaseAdapter {

    private String[] datas = {"账号与安全", "黑名单", "消息管理", "清除缓存", "帮助与反馈", "关于果冻"};
    private Context cxt;

    public SettingAdapter(Context cxt) {
        this.cxt = cxt;
    }

    @Override
    public int getCount() {
        return datas.length;
    }

    @Override
    public Object getItem(int position) {
        return datas[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
           viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(cxt).inflate(R.layout.setting_item,null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(datas[position]);

        return convertView;
    }

    class ViewHolder {
        TextView name;
    }
}
