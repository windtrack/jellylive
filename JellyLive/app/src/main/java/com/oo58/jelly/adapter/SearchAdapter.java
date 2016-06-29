package com.oo58.jelly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.oo58.jelly.R;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.manager.LocalImageManager;
import com.oo58.jelly.util.FollowListener;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.view.CircleImageView;

import java.util.List;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/18.
 */
public class SearchAdapter extends BaseAdapter {

    private List<UserVo> list;//
    private Context context;
    private LayoutInflater mInflater;

    public DisplayImageOptions mOptions;
    public SearchAdapter(List<UserVo> list, Context cxt) {
        this.list = list;
        this.context = cxt;
        mInflater = LayoutInflater.from(cxt);
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

        if(convertView == null){  //sear_item
            convertView = LayoutInflater.from(context).inflate(R.layout.sear_item,null);
        }

        CircleImageView img = (CircleImageView) convertView.findViewById(R.id.face);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        final Button followBtn = (Button) convertView.findViewById(R.id.follow_btn);
        ImageView sexIcon = (ImageView) convertView.findViewById(R.id.sex);
        ImageView labelIcon = (ImageView) convertView.findViewById(R.id.label_icon);
        LinearLayout con = (LinearLayout) convertView.findViewById(R.id.content);

        final UserVo user = (UserVo)getItem(position) ;
        if(!Util.isEmpty(user.getIcon())){
            GlobalData.getmImageLoader(context).displayImage(user.getIcon(),img,mOptions);
        }
        name.setText(user.getName());

        LocalImageManager.setGender(context,sexIcon,user.getGender());
        LocalImageManager.setWealthLev(context,labelIcon,user.getViplev());

        followBtn.setOnClickListener(new FollowListener(context,user,list,followBtn,0));


        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        return convertView;
    }







    class ViewHolder {
        LinearLayout con;
        CircleImageView img;
        TextView name;
        ImageView sexIcon;
        ImageView labelIcon;
        Button followBtn;
    }
}
