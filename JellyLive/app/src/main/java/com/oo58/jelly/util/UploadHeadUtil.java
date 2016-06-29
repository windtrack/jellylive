package com.oo58.jelly.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Toast;

import com.oo58.jelly.R;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.view.LoadingDialog;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Desc:
 * Created by sunjinfang on 2016/6/20 15:48.
 */
public class UploadHeadUtil {
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;


    private static final int HandlerCmd_UploadSuccess = 10001 ;
    private static final int HandlerCmd_UploadFailed = 10002 ;
    private static final int HandlerCmd_ToUpLoad = 10003 ;
    private String inputurl = "";

    private Activity activity ;
    private LoadingDialog loadingDialog ;
    private Uri photoUri;

    private UploadHeadListener uploadHeadListener ;

    public UploadHeadUtil(Activity act, UploadHeadListener listener){
        activity = act ;
        uploadHeadListener = listener ;

        loadingDialog = new LoadingDialog();

        String openid = GlobalData.getUID(activity);
        String openkey = GlobalData.getOpenKey(activity);
        String timestamp = GlobalData.getUTimeStamp(activity);
        inputurl = "http://picture.0058.com/useravator.php?type=45&openid="
                + openid + "&openkey=" + openkey + "&timestamp=" + timestamp;
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.TITLE, filename);
        photoUri = activity.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        activity.startActivityForResult(intent, this.CAMERA_REQUEST_CODE);
    }

    public void invokePhoto() {
        Intent localIntent = new Intent("android.intent.action.PICK", null);
        localIntent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(localIntent, this.IMAGE_REQUEST_CODE);
    }

    protected void doCropPhoto(Uri paramUri) {
        activity.startActivityForResult(getCropImageIntent(paramUri),
                this.RESULT_REQUEST_CODE);
    }
    private Intent getCropImageIntent(Uri paramUri) {
        Intent localIntent = new Intent("com.android.camera.action.CROP");
        localIntent.setDataAndType(paramUri, "image/*");
        localIntent.putExtra("crop", "true");
        localIntent.putExtra("aspectX", 1);
        localIntent.putExtra("aspectY", 1);
        localIntent.putExtra("outputX", 600);
        localIntent.putExtra("outputY", 600);
        localIntent.putExtra("return-data", true);
        return localIntent;
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
//            .setImageDrawable(drawable);
        }
    }

    public void saveMyBitmap(Bitmap mBitmap) {

        String sddizhi = "/sdcard/" + System.currentTimeMillis() + ".jpg";
        File f = new File(sddizhi);
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 10, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
            Message msg = new Message();
            msg.what = HandlerCmd_ToUpLoad;
            msg.obj = sddizhi;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case HandlerCmd_ToUpLoad:
                    final String fileurl = msg.obj.toString();
//                    Thread t1 = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            uploadFile(fileurl);
//                        };
//                    });
//                    t1.start();

                    Thread t2 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadFile200(fileurl);
                        };
                    });
                    t2.start();
                    break;
                case HandlerCmd_UploadSuccess:
                    String iconUrl = (String)msg.obj;

                    loadingDialog.closeDialog();
//                    String icon = AppUrl.USER_LOGO_ROOT+msg.obj.toString();
//                    SharedPreUtil.put(activity, AppContance.USER_ICON,icon);
                    ToastManager.makeToast(activity,"上传成功");
                    if(uploadHeadListener!=null){
                        uploadHeadListener.onUploadSuceess(iconUrl);
                    }
                    break;
                case HandlerCmd_UploadFailed:
                    loadingDialog.closeDialog();
                    ToastManager.makeToast(activity,"上传失败");
                    if(uploadHeadListener!=null){
                        uploadHeadListener.onUploadFailed();
                    }
                    break;
                default:
                    break;
            }
        };
    };


    /* 上传文件至Server的方法 */
    private void uploadFile(String uploadFile) {
        try {
            URL url = new URL(inputurl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "application/octet-stream");
			/* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            FileInputStream fStream = new FileInputStream(uploadFile);
			/* 设置每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
			/* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
			/* close streams */
            fStream.close();
            ds.flush();
			/* 取得Response内容 */
            int res = con.getResponseCode();
            if (res == 200) {
                InputStream is = con.getInputStream();
                int ch;
                StringBuffer b = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    b.append((char) ch);
                }
                Message msg = new Message();
                String result = b.toString();
                String icon = result.substring((result.indexOf("|") + 1), result.length());
                msg.obj = icon;
                msg.what = HandlerCmd_UploadSuccess;
                handler.sendMessage(msg);
            } else {
                handler.sendEmptyMessage(HandlerCmd_UploadFailed);
            }
            ds.close();
        } catch (Exception e) {
            handler.sendEmptyMessage(HandlerCmd_UploadFailed);
        }
    }

    /* 上传文件至Server的方法 */
    private void uploadFile200(String uploadFile) {
        String openid = GlobalData.getUID(activity);
        String openkey = GlobalData.getOpenKey(activity);
        String timestamp = GlobalData.getUTimeStamp(activity);
        String dizhi = "http://picture.0058.com/useravator.php?type=200&openid="
                + openid + "&openkey=" + openkey + "&timestamp=" + timestamp;
        try {
            URL url = new URL(dizhi);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "application/octet-stream");
			/* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            FileInputStream fStream = new FileInputStream(uploadFile);
			/* 设置每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
			/* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
			/* close streams */
            fStream.close();
            ds.flush();
			/* 取得Response内容 */
            int res = con.getResponseCode();
            if (res == 200) {
                InputStream is = con.getInputStream();
                int ch;
                StringBuffer b = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    b.append((char) ch);
                }
                Message msg = new Message();
                String result = b.toString();
                String icon = result.substring((result.indexOf("|") + 1), result.length());
                msg.obj = icon;
                msg.what = HandlerCmd_UploadSuccess;
                handler.sendMessage(msg);

            } else {
                handler.sendEmptyMessage(HandlerCmd_UploadFailed);
            }
            ds.close();
        } catch (Exception e) {
            handler.sendEmptyMessage(HandlerCmd_UploadFailed);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub


        if (resultCode != activity.RESULT_CANCELED) {
            // 本地上传
            if (requestCode == IMAGE_REQUEST_CODE) {
                if ((resultCode == -1) && (data != null)) {
                    doCropPhoto(data.getData());
                }
            }
            // 图片处理结果
            if (requestCode == this.RESULT_REQUEST_CODE) {

                String openid = GlobalData.getUID(activity);
                String openkey = GlobalData.getOpenKey(activity);
                String timestamp = GlobalData.getUTimeStamp(activity);
                final String url = "http://picture.0058.com/useravator.php?type=45&openid="
                        + openid
                        + "&openkey="
                        + openkey
                        + "&timestamp="
                        + timestamp;
                if (data != null) {
//                    getImageToView(data);
                }
                final Bitmap localBitmap = (Bitmap) data.getParcelableExtra("data");
                loadingDialog.show(activity,"正在上传",false,false);
                saveMyBitmap(localBitmap);

            }
            // 相机拍照
            if (requestCode == CAMERA_REQUEST_CODE) {
                Uri localUri = null;
                if ((data != null) && (data.getData() != null)) {
                    localUri = data.getData();
                }
                // 一些机型无法从getData中获取uri，则需手动指定拍照后存储照片的Uri
                if (localUri == null) {
                    if (photoUri != null) {
                        localUri = photoUri;
                    }
                }
                doCropPhoto(localUri);
            }
        }
    }




    public interface  UploadHeadListener{
        public void onUploadSuceess(String iconname);
        public void onUploadFailed();
    }


}
