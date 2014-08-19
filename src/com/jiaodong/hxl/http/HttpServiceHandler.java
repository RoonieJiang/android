package com.jiaodong.hxl.http;
import java.io.IOException;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * 处理返回结果回调类
 * @author 
 *
 */
public abstract class HttpServiceHandler extends Handler{
	private Context context;//Android上下文环境
	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public HttpServiceHandler(){
		init();
	}
	
	public HttpServiceHandler(Context context) {
		this.context = context;
	}
	/**
	 * 初始化，在使用匿名内部类时需调用此方法给context传值
	 */
	protected void init() {};
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		Bundle bundle = msg.getData();
		switch (msg.what) {
		case HttpService.SUCCESS_STRING:
			String responseString = bundle.getString(HttpService.RESONSE_STRING);
			onHttpServiceFinished(responseString);
			break;
		case HttpService.SUCCESS_BYTE:
			byte[] responseBuff = bundle.getByteArray(HttpService.RESONSE_BYTE);
			handleResponseBuff(responseBuff);
			break;
		case HttpService.ERROR:
			Exception e = (Exception) bundle.getSerializable(HttpService.EXCEPTION);
			e.printStackTrace();
			onHttpServiceError(e);
		default:
			break;
		}
	}
	
	protected void handleResponseBuff(byte[] responseBuff) {
		
	}

	protected void onHttpServiceFinished(String responseString) {
		if (handlerResponse(responseString)) {
			return ;
		}
		else {
			try {
			handlerJSONResponse(new JSONObject(responseString));
		} catch (Exception e) {
			 onHttpServiceError(e);
		}
	}
		
	}

	protected boolean handlerResponse(String responseString) {
		return false;
	}


	
	protected boolean handlerJSONResponse(JSONObject jsonObject) {
		return false;
	}

	

	/**
	 * 访问服务时发生的异常处理
	 * @param e
	 */
	public void onHttpServiceError(Exception e) {
		if(e instanceof ParseException){
			Toast.makeText(context, "访问服务时发生解析错误", Toast.LENGTH_SHORT).show();
		}else if(e instanceof IOException){
			Toast.makeText(context, "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
		}else if(e instanceof JSONException){
			Toast.makeText(context, "访问服务处理返回的数据时发生错误", Toast.LENGTH_SHORT).show();			
		}else if (e instanceof ClientProtocolException) {
			Toast.makeText(context, "客户端与服务端的协议发生错误", Toast.LENGTH_SHORT).show();
		}else {
			Toast.makeText(context, "发生未知错误", Toast.LENGTH_SHORT).show();
		}
		
	}
		
}
