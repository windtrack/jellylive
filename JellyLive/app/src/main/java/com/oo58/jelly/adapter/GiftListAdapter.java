package com.oo58.jelly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.oo58.jelly.R;
import com.oo58.jelly.entity.Gift;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.BaseViewHolder;

import java.util.List;

/**
 * @author zhongxf
 * @Description 礼物里列表的适配器
 * @Date 2016/6/24.
 */
public class GiftListAdapter extends BaseAdapter {

    private List<Gift> list;
    private Context context;
    private LayoutInflater mLayouInflater;

    private DisplayImageOptions mOptions ;

    private int selectIndex ;

    public GiftListAdapter(List<Gift> list, Context context) {
        this.context = context;
        this.list = list;
        mLayouInflater = LayoutInflater.from(context);

        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.test1).cacheInMemory()
                .showImageOnFail(R.mipmap.test1)
                .showImageForEmptyUri(R.mipmap.test1)
                .showImageOnLoading(R.mipmap.test1)
                .cacheOnDisc().build();

        selectIndex = -1 ;
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
        if (convertView == null) {
            convertView = mLayouInflater.inflate(R.layout.gift_list_item, null);
        }

        ImageView icon = BaseViewHolder.get(convertView,R.id.gift_pic);
        TextView text_price = BaseViewHolder.get(convertView,R.id.price);
        TextView text_sendex = BaseViewHolder.get(convertView,R.id.exper);

        //选中
        CheckBox select =  BaseViewHolder.get(convertView,R.id.selected_flag);
        select.setChecked(selectIndex == position);

        Gift gift = (Gift)getItem(position) ;
        GlobalData.getmImageLoader(context).displayImage(gift.getIcon(),icon,mOptions);
        text_price.setText(gift.getPrice()+"");
        text_sendex.setText(gift.getName());


        return convertView;
    }

    public void  setSelectIndex(int index){
        if(selectIndex != index){
            selectIndex = index ;
            notifyDataSetChanged();
        }

    }

}
