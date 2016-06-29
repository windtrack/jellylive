package com.oo58.jelly.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oo58.jelly.R;

import java.util.List;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/14.
 */
public class TestAdapter extends BaseAdapter {

    private List<String> list;
    private Context cxt;

    public TestAdapter(List<String> list, Context cxt) {
        this.list = list;
        this.cxt = cxt;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(cxt).inflate(R.layout.test_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.test_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(list.get(position));
        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
