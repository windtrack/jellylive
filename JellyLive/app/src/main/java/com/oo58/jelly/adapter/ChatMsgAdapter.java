package com.oo58.jelly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.entity.ChatMsgVo;

import java.util.List;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/16.
 */
public class ChatMsgAdapter extends BaseAdapter {
    private List<ChatMsgVo> list;
    private Context cxt;

    public ChatMsgAdapter(List<ChatMsgVo> list, Context cxt) {
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
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(cxt).inflate(R.layout.chat_msg_item, null);
            viewHolder.msg = (TextView) convertView.findViewById(R.id.msg_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChatMsgVo chat = list.get(position);
        if (chat != null) {
            viewHolder.msg.setText(chat.getContent());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView msg;
    }
}
