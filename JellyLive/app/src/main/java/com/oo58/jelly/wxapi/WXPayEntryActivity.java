package com.oo58.jelly.wxapi;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private String APP_ID = "wxe3b6571489e8a96d";
	private IWXAPI api;
	public static final String action = "wxaction";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		
	}

	@Override
	public void onResp(BaseResp resp) {
//		 Log.i("lvjian", "onPayFinish, errCode = " + resp.errCode);
//		
//		 if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		 builder.setTitle("");
//		 builder.setMessage(getString(R.string.hello_world,
//		 resp.errStr + ";code=" + String.valueOf(resp.errCode)));
//		 builder.show();
//		 }
		// 支付结果
		Intent intent = new Intent(action);
		if (resp.errCode == 0) {
			// 成功
			intent.putExtra("type", "0");
			sendBroadcast(intent);
			finish();
		} else if (resp.errCode == -1) {
			// 失败
			intent.putExtra("type", "-1");
			sendBroadcast(intent);
			finish();
		} else if (resp.errCode == -2) {
			intent.putExtra("type", "-2");
			sendBroadcast(intent);
			finish();
		}
	}
}