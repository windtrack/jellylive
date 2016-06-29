package com.oo58.jelly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.oo58.jelly.R;
import com.oo58.jelly.entity.PrivateMsgVo;

import java.util.List;

/**
 * @author zhongxf
 * @Description 发送私信的适配器
 * @Date 2016/6/23.
 */
public class SendSiXinAdapter extends BaseAdapter {

    private List<PrivateMsgVo> list;
    private Context context;
    private LayoutInflater mLayoutInflater;

    public SendSiXinAdapter(List<PrivateMsgVo> list, Context context) {
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
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.send_sixin_item, null);
            viewHolder = new ViewHolder();
            viewHolder.receiveCon = (LinearLayout) convertView.findViewById(R.id.receive_sixin_con);
            viewHolder.sendCon = (LinearLayout) convertView.findViewById(R.id.send_sixin_con);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            viewHolder.sendCon.setVisibility(View.VISIBLE);
            viewHolder.receiveCon.setVisibility(View.GONE);
        } else {
            viewHolder.sendCon.setVisibility(View.GONE);
            viewHolder.receiveCon.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        LinearLayout sendCon;
        LinearLayout receiveCon;
    }

}
