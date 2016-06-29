package com.oo58.jelly.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.entity.RankListVo;
import com.oo58.jelly.view.CircleImageView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/16.
 */
public class RankListAdapter extends BaseAdapter {

    private List<RankListVo> list;
    private Context cxt;

    private LayoutInflater li;

    private final static int ONE = 0;//排名第一
    private final static int TWO = 1;//排名第二
    private final static int THREE = 2;//排名第三
    private final static int FOUR = 3;//排名以后

    private Bitmap nan;//男
    private Bitmap nv;//女

    public RankListAdapter(List<RankListVo> list, Context cxt) {
        this.list = list;
        this.cxt = cxt;
        this.li = LayoutInflater.from(cxt);

        this.nan = BitmapFactory.decodeResource(cxt.getResources(), R.mipmap.sexman);
        this.nv = BitmapFactory.decodeResource(cxt.getResources(), R.mipmap.sexwoman);
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ONE;
        } else if (position == 1) {
            return TWO;
        } else if (position == 2) {
            return THREE;
        } else {
            return FOUR;
        }
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
        ViewHolder holder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case ONE://第一
                    convertView = li.inflate(R.layout.rank_first, null);
                    break;
                case TWO://第二
                    convertView = li.inflate(R.layout.rank_second, null);
                    break;
                case THREE://第三
                    convertView = li.inflate(R.layout.rank_third, null);
                    break;
                case FOUR://第四
                    convertView = li.inflate(R.layout.rank_list_item, null);
                    holder.xh = (TextView) convertView.findViewById(R.id.position);
                    break;

            }
            holder.img = (CircleImageView) convertView.findViewById(R.id.face);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.label = (ImageView) convertView.findViewById(R.id.label_icon);
            holder.sex = (ImageView) convertView.findViewById(R.id.sex_icon);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RankListVo vo = list.get(position);
        if (vo != null) {
            holder.name.setText(vo.getName());
            if (vo.getSex() == 1) {
                holder.sex.setImageBitmap(nan);
            } else {
                holder.sex.setImageBitmap(nv);
            }
            holder.info.setText("贡献" + vo.getMoney() + "果冻币");
            if (vo.getPosition() > 2 && holder.xh != null) {
                holder.xh.setText("NO." + (vo.getPosition() + 1));
            }
        }
        return convertView;
    }

    //后面排序的ViewHolder
    class ViewHolder {
        CircleImageView img;//头像
        TextView name;//名称
        TextView info;//贡献欣喜
        ImageView sex;//性别
        ImageView label;//等级
        TextView xh;//排序
    }
}
