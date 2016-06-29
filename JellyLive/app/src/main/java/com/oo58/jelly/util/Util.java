package com.oo58.jelly.util;



import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Desc:一个工具类
 * Created by sunjinfang on 2016/6/1 11:54.
 */
public class Util {

    /**
     * 发送一个rpc请求
     * @param event  命令
     * @param params 数据
     * @return 服务器返回的json数据
     */
    public static String addRpcEvent(RpcEvent event, Object ...params){

        String code = "" ;
        for (int i = 0; i < params.length; i++) {
                code+= params[i].toString()+",";
        }
        String s = "";
        try {
            String uri = AppUrl.APP_RPC;
            MyOkHttpClient client = new MyOkHttpClient(uri);
            client.setConnectTimeout(30, TimeUnit.SECONDS);
            String data = client.doRequest(event.name, params);
            JSONObject jsonObject = new JSONObject(data);
            s = jsonObject.getString("result");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("sjf","RpcError = " +event.name +" , "+code+" "+e.toString());
        }
        Log.i("sjf","RpcEvent = " +event.name +" , "+code+";返回： "+s);
        return s;
    }


    public static List<String> toListString(String[] arrayStr, List<String> list ){
        for (String str: arrayStr) {
            list.add(str) ;
        }
        return list ;
    }
    /**
     * 拼接数组
     *
     * @param
     * @return
     */
    public static byte[] getBytes(String body, int tid) {
        // 包长
        byte[] package_length = intToByteArray(body.getBytes().length + 12);
        byte[] message_package = new byte[body.getBytes().length + 12];
        // 方法
        byte[] id = intToByteArray(tid);
        // 包体长度
        byte[] body_length = intToByteArray(body.getBytes().length);
        // 包体
        byte[] body_bytes = body.getBytes();

        System.arraycopy(package_length, 0, message_package, 0, 4);
        System.arraycopy(id, 0, message_package, 4, 4);
        System.arraycopy(body_length, 0, message_package, 8, 4);
        System.arraycopy(body_bytes, 0, message_package, 12,
                body.getBytes().length);
        return message_package;
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }
    /**
     * 判断字符串是否为空
     *
     */
    public static boolean isEmpty(String str) {
        // str=str.trim();
        if (str == null || "".equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * udicode字符串转汉字
     *
     * @param str
     * @return
     */
    public static String uncodeParser(String str) {
        String v = "'" + str + "'";
        try {
            String result = new JSONTokener(v).nextValue().toString();
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /***
     * 判断 String 是否是 int
     *
     * @param input
     * @return
     */
    public static boolean isInteger(String input) {
        Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(input);
        return mer.find();
    }

    /**
     * 隐藏软键盘
     * @param act
     * @param view
     */
    public static void hideInput(Context act,View view){
        if (act!=null && view != null) {
            InputMethodManager inputmanger = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable createDrawable(Context context ,int rid,String letter) {

        Bitmap imgMarker = BitmapFactory.decodeResource(context.getResources(), rid);
        int width = imgMarker.getWidth();
        int height = imgMarker.getHeight() ;
        Bitmap imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(imgTemp);

        Paint paint = new Paint(); // 建立画笔
        paint.setDither(true);
        paint.setFilterBitmap(true);
        Rect src = new Rect(0, 0, width, height);
        Rect dst = new Rect(0, 0, width, height);
        canvas.drawBitmap(imgMarker, src, dst, paint);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);

        int textSize = UIUtil.dip2px(context,15);
        textPaint.setTextSize(textSize);
        int textWidth = textSize*letter.length();

        textPaint.setTypeface(Typeface.DEFAULT); // 采用默认的宽度
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(letter), width /2+10, height-(height-textSize)/2-3, textPaint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return (Drawable) new BitmapDrawable(context.getResources(), imgTemp);

    }





    public static int getAge(Date birthDay)  {
        //获取当前系统时间
        Calendar cal = Calendar.getInstance();
        //如果出生日期大于当前时间，则抛出异常
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        //取出系统当前时间的年、月、日部分
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        //将日期设置为出生日期
        cal.setTime(birthDay);
        //取出出生日期的年、月、日部分
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        //当前年份与出生年份相减，初步计算年龄
        int age = yearNow - yearBirth;
        //当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
        if (monthNow <= monthBirth) {
            //如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            }else{
                age--;
            }
        }
        System.out.println("age:"+age);
        return age;
    }
    public static int getAge(String date)  {
        if(isEmpty(date)){
            return 101 ;
        }
        int age = 0 ;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDay = null;//提取格式中的日期
        try {
            birthDay = sdf.parse(date);

            age = getAge(birthDay) ;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return age;
    }
}
