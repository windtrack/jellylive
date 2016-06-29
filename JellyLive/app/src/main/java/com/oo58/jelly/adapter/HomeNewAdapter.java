package com.oo58.jelly.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.entity.AnchorVo;
import com.oo58.jelly.util.UIUtil;

import org.w3c.dom.Text;

import java.util.List;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/15.
 */
public class HomeNewAdapter extends BaseAdapter {
    private List<AnchorVo> list;
    private Context cxt;

    private int SCREEN_WIDTH = 0;
    private int height = 0;

    public HomeNewAdapter(List<AnchorVo> list, Context cxt) {
        this.list = list;
        this.cxt = cxt;
        SCREEN_WIDTH = cxt.getResources().getDisplayMetrics().widthPixels;
        height = (SCREEN_WIDTH - UIUtil.dip2px(cxt, 10)) / 2;
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
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(cxt).inflate(R.layout.home_new_item,null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.anchor_name);
            viewHolder.pic = (ImageView) convertView.findViewById(R.id.anchor_pic);
            viewHolder.livingFlag = (ImageView) convertView.findViewById(R.id.living_flag);
            viewHolder.num = (TextView) convertView.findViewById(R.id.look_num);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AnchorVo vo = list.get(position);

        if(vo!=null){
            viewHolder.name.setText(vo.getName());
            viewHolder.num.setText(vo.getNum()+"人");
            viewHolder.pic.setImageResource(R.mipmap.testpic);
        }
        ViewGroup.LayoutParams lp = viewHolder.pic.getLayoutParams();
        lp.width = height;
        lp.height = height;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppAction.ACTION_LIVGING_ROOM);
                intent.putExtra("anchorid",vo.getAnchorId());
                intent.putExtra("roomid",vo.getRoomId());
                intent.putExtra("stream",vo.getLiveStream());
                cxt. startActivity(intent);
            }
        });


        return convertView;
    }

    static class ViewHolder {

        ImageView pic;
        TextView name;
        ImageView livingFlag;
        TextView num;
    }

}
