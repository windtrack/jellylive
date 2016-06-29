package com.oo58.jelly.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.oo58.jelly.R;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.impl.OnUserTouchListener;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.view.CircleImageView;

import java.util.List;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/15.
 */
public class LivingPersonAdaper extends RecyclerView.Adapter<LivingPersonAdaper.ViewHolder> {
    private LayoutInflater mInflater;
    private List<UserVo> list;
    public DisplayImageOptions mOptions;
    private Context  mContext ;
    private OnUserTouchListener onUserTouchListener ;

    public LivingPersonAdaper(Context context, List<UserVo> datats, OnUserTouchListener listener) {
        this.mContext = context ;
        mInflater = LayoutInflater.from(context);
        list = datats;
        onUserTouchListener = listener ;
        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.testpic).cacheInMemory()
                .showImageOnFail(R.mipmap.testpic)
                .showImageForEmptyUri(R.mipmap.testpic)
                .showImageOnLoading(R.mipmap.testpic)
                .cacheOnDisc().build();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        CircleImageView img;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view  = mInflater.inflate(R.layout.living_user_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.img = (CircleImageView) view.findViewById(R.id.user_face);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        final UserVo userVo = list.get(position);
        GlobalData.getmImageLoader(mContext).displayImage(userVo.getIcon(),holder.img,mOptions);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onUserTouchListener!=null){
                    onUserTouchListener.onTouch(userVo.getUid());
                }
            }
        });
    }

    /**
     * 具体的给View设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

    }
}
