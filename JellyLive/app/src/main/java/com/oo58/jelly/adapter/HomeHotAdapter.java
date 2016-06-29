package com.oo58.jelly.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.oo58.jelly.R;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.entity.AnchorVo;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.view.CircleImageView;


import java.util.List;

/**
 * @author zhongxf
 * @Description 热门主播的列表
 * @Date 2016/6/14.
 */
public class HomeHotAdapter extends BaseAdapter {

    private List<AnchorVo> list;
    private Context cxt;

    private static int SCREEN_WIDHT = 0;
    public DisplayImageOptions mOptions;
    public HomeHotAdapter(List<AnchorVo> list, Context cxt) {
        this.list = list;
        this.cxt = cxt;
        DisplayMetrics dm = cxt.getResources().getDisplayMetrics();
        SCREEN_WIDHT = dm.widthPixels;

        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.testpic).cacheInMemory()
                .showImageOnFail(R.mipmap.testpic)
                .showImageForEmptyUri(R.mipmap.testpic)
                .showImageOnLoading(R.mipmap.testpic)
                .cacheOnDisc().build();
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
            convertView = LayoutInflater.from(cxt).inflate(R.layout.home_hot_item, null);

            viewHolder.face = (CircleImageView) convertView.findViewById(R.id.anchor_face);
            viewHolder.name = (TextView) convertView.findViewById(R.id.anchor_name);
            viewHolder.title = (TextView) convertView.findViewById(R.id.anchor_title);
            viewHolder.num = (TextView) convertView.findViewById(R.id.look_num);
            viewHolder.livingFlag = (ImageView) convertView.findViewById(R.id.living_flag);
            viewHolder.pic = (ImageView) convertView.findViewById(R.id.anchor_pic);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final  AnchorVo vo = list.get(position);
        if (vo != null) {
            viewHolder.name.setText(vo.getName());
            viewHolder.num.setText(vo.getNum());
            viewHolder.title.setText(vo.getTitle());
            GlobalData.getmImageLoader(cxt).displayImage(vo.getFaceUrl(),viewHolder.face,mOptions);
            GlobalData.getmImageLoader(cxt).displayImage(vo.getPicUrl(),viewHolder.pic,mOptions);
        }

        ViewGroup.LayoutParams lp = viewHolder.pic.getLayoutParams();
        lp.width = SCREEN_WIDHT;
        lp.height = SCREEN_WIDHT;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppAction.ACTION_LIVGING_ROOM);
                intent.putExtra("anchorid",vo.getAnchorId());
                intent.putExtra("roomid",vo.getRoomId());
                intent.putExtra("stream",vo.getLiveStream());
                ((Activity)cxt).startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        CircleImageView face;//用户头像
        TextView name;//用户名字
        TextView title;//直播标题
        TextView num;//观看人数
        ImageView pic;//宣传图片
        ImageView livingFlag;//直播的标志
    }
}
