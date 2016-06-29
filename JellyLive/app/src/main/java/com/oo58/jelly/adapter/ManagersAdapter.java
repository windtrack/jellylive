package com.oo58.jelly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.oo58.jelly.R;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.manager.LocalImageManager;
import com.oo58.jelly.view.CircleImageView;

import java.util.List;

/**
 * @author zhongxf
 * @Description 管理员列表的适配器
 * @Date 2016/6/23.
 */
public class ManagersAdapter extends BaseAdapter {

    private List<UserVo> list;//黑名单的列表
    private Context cxt;//上下文
    private LayoutInflater li;//视图加载器
    public DisplayImageOptions mOptions;
    public ManagersAdapter(List<UserVo> list, Context cxt) {
        this.list = list;
        this.cxt = cxt;
        li = LayoutInflater.from(cxt);
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
            convertView = li.inflate(R.layout.managers_list_item, null);
            viewHolder.img = (CircleImageView) convertView.findViewById(R.id.face);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.sex = (ImageView) convertView.findViewById(R.id.sex);
            viewHolder.sign = (TextView) convertView.findViewById(R.id.sign);
            viewHolder.lebel = (ImageView) convertView.findViewById(R.id.lebel_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserVo vo = list.get(position);
        if (vo != null) {

            GlobalData.getmImageLoader(cxt).displayImage(vo.getIcon(),viewHolder.img,mOptions);
            viewHolder.name.setText(vo.getName());
            viewHolder.sign.setText(vo.getSign());
            LocalImageManager.setGender(cxt, viewHolder.sex,vo.getGender());
            LocalImageManager.setWealthLev(cxt,viewHolder.lebel,vo.getViplev());
        }
        return convertView;
    }

    class ViewHolder {
        CircleImageView img;//头像
        TextView name;//名字
        ImageView sex;//性别
        TextView sign;//签名
        ImageView lebel;//等级
    }
}
