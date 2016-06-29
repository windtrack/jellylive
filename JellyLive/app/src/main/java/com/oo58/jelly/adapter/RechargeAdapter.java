package com.oo58.jelly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.entity.RechargeVo;

import org.w3c.dom.Text;

import java.util.List;

/**
 * @author zhongxf
 * @Description 充值的适配器
 * @Date 2016/6/17.
 */
public class RechargeAdapter extends BaseAdapter {
    private List<RechargeVo> list;
    private Context cxt;
    private LayoutInflater li;

    public RechargeAdapter(List<RechargeVo> list, Context cxt) {
        this.list = list;
        this.cxt = cxt;
        this.li = LayoutInflater.from(cxt);
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
            convertView = li.inflate(R.layout.recharge_item, null);
            viewHolder.dimods = (TextView) convertView.findViewById(R.id.dimods_num);
            viewHolder.zs = (TextView) convertView.findViewById(R.id.zs_info);
            viewHolder.money = (Button) convertView.findViewById(R.id.money);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RechargeVo vo = list.get(position);
        if (vo != null) {
            viewHolder.money.setText("￥" + vo.getMoney());
            viewHolder.dimods.setText(String.valueOf(vo.getDiamonds()));
            if (vo.getZs() > 0) {
                viewHolder.zs.setVisibility(View.VISIBLE);
                viewHolder.zs.setText("赠送：" + vo.getZs());
            } else {
                viewHolder.zs.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView dimods;
        Button money;
        TextView zs;
    }
}
