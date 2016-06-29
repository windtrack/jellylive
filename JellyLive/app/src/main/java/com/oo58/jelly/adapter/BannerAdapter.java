package com.oo58.jelly.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.oo58.jelly.R;
import com.oo58.jelly.entity.Banner;
import com.oo58.jelly.manager.GlobalData;

import java.util.List;

/**
 * Desc:
 * Created by sunjinfang on 2016/6/15 14:37.
 */
public class BannerAdapter extends PagerAdapter {

    public DisplayImageOptions mOptions;
    private Context context;
    private List<Banner> list;

    public BannerAdapter(List<Banner> paramList, Context paramContext) {
        this.list = paramList;
        this.context = paramContext;
        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.go_new_bg).cacheInMemory()
                .showImageOnFail(R.mipmap.go_new_bg)
                .showImageForEmptyUri(R.mipmap.go_new_bg)
                .showImageOnLoading(R.mipmap.go_new_bg)
                .cacheOnDisc().build();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (list != null && list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
    }


    @Override
    public boolean isViewFromObject(View paramView, Object paramObject) {
        // TODO Auto-generated method stub
        return paramView == paramObject;
    }

    @Override
    public Object instantiateItem(ViewGroup paramViewGroup, final int paramInt) {
        final Banner advertise = list.get(paramInt);
        View localView = ((Activity) this.context).getLayoutInflater().inflate(R.layout.item_banner, paramViewGroup, false);
        ImageView localImageView = (ImageView) localView
                .findViewById(R.id.ad_img);
        GlobalData.getmImageLoader(context).displayImage(list.get(paramInt).getImg(), localImageView, mOptions);
        paramViewGroup.addView(localView);
        localImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//                0 不跳转 2 跳转到 url 100 跳转到首充活动
                if ("2".equals(advertise.getAct())) {
                    // 跳转到url
//                    Intent intent = new Intent(context, HuoDongActivity.class);
//                    intent.putExtra("url", advertise.getUrl());
//                    intent.putExtra("title", "活动");
//                    context.startActivity(intent);


                } else if ("1".equals(advertise.getAct())) {
                    // 跳转到房间
//                    Intent localIntent = new Intent(context, LiveRoomActivity.class);
//                    localIntent.putExtra("room_id", advertise.getRoom_id());
//                    context.startActivity(localIntent);
                } else if ("11".equals(advertise.getAct())) {
                } else  if ("100".equals(advertise.getAct())) {
//                    Intent intent = new Intent(context, FirstPayActivity.class);
//                    context.startActivity(intent);
                }else{

                }
            }
        });
        return localView;
    }

    @Override
    public void destroyItem(ViewGroup paramViewGroup, int paramInt,
                            Object paramObject) {
        paramViewGroup.removeView((View) paramObject);
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return POSITION_NONE;
    }

}
