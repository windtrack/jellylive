package com.oo58.jelly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.oo58.jelly.R;
import com.oo58.jelly.entity.PrivateMsgVo;

import java.util.List;

/**
 * @author zhongxf
 * @Description  私信列表的适配器
 * @Date 2016/6/23.
 */
public class SiXinListAdapter extends BaseAdapter {
    private List<PrivateMsgVo> list;
    private Context context;
    private LayoutInflater mLayoutInflater;
    public SiXinListAdapter(List<PrivateMsgVo> list,Context context){
        this.list = list;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
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
        if(convertView==null){
            convertView = mLayoutInflater.inflate(R.layout.sixin_list_item,null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
    class ViewHolder{

    }
}
