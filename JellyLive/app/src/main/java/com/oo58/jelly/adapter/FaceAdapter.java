package com.oo58.jelly.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.oo58.jelly.R;
import com.oo58.jelly.manager.face.FaceVo;

import java.util.List;

/**
 * @author zhongxf
 * @Description 表情的适配器
 * @Date 2016/6/25.
 */
public class FaceAdapter extends BaseAdapter {
    private List<FaceVo> list;
    private Context context;
    private LayoutInflater mLayoutInflater;

    public FaceAdapter(List<FaceVo> list, Context context) {
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
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.face_item, null);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.face);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FaceVo vo = list.get(position);
        viewHolder.img.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), vo.getResId()));

        return convertView;
    }

    class ViewHolder {
        ImageView img;
    }
}
