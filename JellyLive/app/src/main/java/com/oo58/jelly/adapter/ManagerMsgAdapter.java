package com.oo58.jelly.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.view.CircleImageView;

import java.util.List;

/**
 * @author zhongxf
 * @Description 消息管理的用户的适配器
 * @Date 2016/6/22.
 */
public class ManagerMsgAdapter extends BaseAdapter {


    private List<UserVo> list;//黑名单的列表
    private Context cxt;//上下文
    private LayoutInflater li;//视图加载器
    private Bitmap man;//男
    private Bitmap woman;//女

    public ManagerMsgAdapter(List<UserVo> list, Context cxt) {
        this.list = list;
        this.cxt = cxt;
        li = LayoutInflater.from(cxt);
        man = BitmapFactory.decodeResource(cxt.getResources(), R.mipmap.sexman);
        woman = BitmapFactory.decodeResource(cxt.getResources(), R.mipmap.sexwoman);
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
            convertView = li.inflate(R.layout.manager_msg_item, null);
            viewHolder.img = (CircleImageView) convertView.findViewById(R.id.face);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.sex = (ImageView) convertView.findViewById(R.id.sex);
            viewHolder.sign = (TextView) convertView.findViewById(R.id.sign);
            viewHolder.isGetMsg = (CheckBox) convertView.findViewById(R.id.is_get_msg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserVo vo = list.get(position);
        if (vo != null) {
            viewHolder.name.setText(vo.getName());
            viewHolder.sign.setText(vo.getSign());

            if (vo.getGender() == 1) {
                viewHolder.sex.setImageBitmap(man);
            } else {
                viewHolder.sex.setImageBitmap(woman);
            }
            if (vo.getIsGetMsg() == 0) {
                viewHolder.isGetMsg.setChecked(true);
            } else {
                viewHolder.isGetMsg.setChecked(false);
            }

        }
        return convertView;
    }

    class ViewHolder {
        CircleImageView img;//头像
        TextView name;//名字
        ImageView sex;//性别
        TextView sign;//签名

        CheckBox isGetMsg;
    }

}
