package com.oo58.jelly.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.util.Util;

/**
 * Desc: 本地的可设置的图片管理
 * Created by sunjinfang on 2016/6/16 10:10.
 */
public class LocalImageManager {

    /**
     * 设置财富等级
     * @param context
     * @param view
     * @param lev
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public  static  void setWealthLev(Context context , ImageView view, int lev ){
        if (lev >= ImagerResource.Image_wealth.length) {
            lev = ImagerResource.Image_wealth.length-1;
        }
        Bitmap received_img = BitmapFactory.decodeResource(context.getResources(), ImagerResource.Image_wealth[lev]);
        view.setImageBitmap(received_img);
//        view.setImageDrawable(Util.createDrawable(context,ImagerResource.Image_wealth[index],"LV"+lev));
    }


    public static Drawable getWealthLevDrawable(Context context , int lev){
        if (lev >= ImagerResource.Image_wealth.length) {
            lev = ImagerResource.Image_wealth.length-1;
        }
        Drawable image = context.getResources().getDrawable( ImagerResource.Image_wealth[lev]);
//        Bitmap received_img = BitmapFactory.decodeResource(context.getResources(), ImagerResource.Image_wealth[lev]);
//        return Util.createDrawable(context,ImagerResource.Image_wealth[lev],"LV"+lev);
        return image ;
    }
//
//    private  static  void setWealthBGLev(Context context , ImageView view,int lev ){
//
//        int index = lev/5;
//        if (index >= ImagerResource.Image_wealth.length) {
//            return;
//        }
//        Bitmap received_img = BitmapFactory.decodeResource(context.getResources(), ImagerResource.Image_wealth[index]);
//        view.setImageBitmap(received_img);
//    }




    /**
     * 设置性别
     * @param context
     * @param view
     * @param sex
     */
    public  static  void setGender(Context context , ImageView view,int sex ){
        if (sex >= ImagerResource.Image_sex.length) {
            return;
        }
        if(sex ==0){
            view.setVisibility(View.INVISIBLE);
        }else{
            view.setVisibility(View.VISIBLE);
            Bitmap received_img = BitmapFactory.decodeResource(context.getResources(), ImagerResource.Image_sex[sex]);
            view.setImageBitmap(received_img);
        }
    }


//
//
//    public static Bitmap LayoutToBitmap(Context context,int lev ){
//
//
//        LayoutInflater inflator = ((Activity)context).getLayoutInflater();
//        View viewHelp = inflator.inflate(R.layout.chatvip, null);
//
//        ImageView img = (ImageView) viewHelp.findViewById(R.id.display);
//        LocalImageManager.setWealthBGLev(context,img,lev);
//
//        TextView textView = (TextView)viewHelp.findViewById(R.id.text_textTitle);
//        textView.setText("LV"+lev);
//
//        int size = (int)textView.getText().length();
//        Bitmap snapshot = convertViewToBitmap(viewHelp, size);
//        return snapshot;
//    }
//    public static Drawable LayoutToDrawable(Context context,int lev ){
//
//
//        LayoutInflater inflator = ((Activity)context).getLayoutInflater();
//        View viewHelp = inflator.inflate(R.layout.chatvip, null);
//
//        ImageView img = (ImageView) viewHelp.findViewById(R.id.display);
//        LocalImageManager.setWealthBGLev(context,img,lev);
//
//        TextView textView = (TextView)viewHelp.findViewById(R.id.text_textTitle);
//        textView.setText("LV"+lev);
//
//        int size = (int)textView.getText().length();
//        Bitmap snapshot = convertViewToBitmap(viewHelp, size);
//        Drawable drawable = (Drawable)new BitmapDrawable(snapshot);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//
//        return drawable;
//    }
//
//    public static Bitmap convertViewToBitmap(View view, int size) {
//        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
////        int width = size*20;
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());  //根据字符串的长度显示view的宽度
//        view.buildDrawingCache();
//        Bitmap bitmap = view.getDrawingCache();
//        return bitmap;
//    }









//    /**
//     * 设置财富等级
//     *
//     * @param lev
//     * @param view
//     * @param context
//     */
//    public static void setCostLv(Context context, ImageView view,int lev) {
//        if (lev >= ImagerResource.Image_wealth.length) {
//            return;
//        }
//        Bitmap received_img = BitmapFactory.decodeResource(context.getResources(), ImagerResource.Image_wealth[lev]);
//        view.setImageBitmap(received_img);
//    }



}
