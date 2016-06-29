package com.oo58.jelly.util;

import android.content.Context;
import android.widget.Toast;


/**
 * @Description: 管理toast的类
 */
public class ToastManager {

		protected static final String TAG = "AppToast";
		public static Toast toast;
		/**
		 * 信息提示
		 * @param context
		 * @param content
		 */
		public static void makeToast(Context context, String content) {
			if(context==null)return;
			if(toast != null)
				toast.cancel();
			toast = Toast.makeText(context,content,Toast.LENGTH_SHORT);
			toast.show();
		}


	}


