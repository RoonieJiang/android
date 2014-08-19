package com.jiaodong.hxl;

import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jiaodong.hxl.http.HttpService;
import com.jiaodong.hxl.http.HttpServiceHandler;
import com.jiaodong.hxl.http.ServiceInterface;

public class LoginActivity extends HXLActivity{

	Button backButton;
	Button loginButton;
	ImageButton settingButton;
	EditText contact;
	EditText telephone;
	EditText captcha;
	TextView getCaptcha;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String tel = intent.getStringExtra("login_intent");
		if (tel == null || tel.equals("")) {
			setContentView(R.layout.login);
			initLoninContentView();
		} else {
			setNetworkState(HXLApplication.NETWORK_LOADING);
			Map<String, String> params = new HashMap<String, String>();
			params.put("name", contact.getText().toString());
			params.put("telephone", telephone.getText().toString());
			params.put("captcha", captcha.getText().toString());
			
			HttpService.getInstance().callService(ServiceInterface.USER_INFO, params, user_info_handler, 10);
		}
	}
	
	public void initLoninContentView() {
		backButton = (Button) findViewById(R.id.user_back_btn);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		loginButton = (Button) findViewById(R.id.user_login_btn);
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				submit();
			}
		});
		getCaptcha = (TextView) findViewById(R.id.user_captcha_button);
		getCaptcha.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		contact = (EditText) findViewById(R.id.user_contact_edit);
		telephone = (EditText) findViewById(R.id.user_tel_edit);
		captcha = (EditText) findViewById(R.id.user_captcha_edit);
	}
	
	//初始化显示登陆的用户信息
	@Override
	protected void initActivity() {
		// TODO Auto-generated method stub
		super.initActivity();
		setContentView(R.layout.user);
		
		settingButton = (ImageButton) findViewById(R.id.user_top_right_button);
		settingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, SettingsActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void submit() {
		if(contact.getText().toString().equals("")) {
			Toast.makeText(this, "请输入联系人姓名！", Toast.LENGTH_LONG).show();
			return;
		} else if(telephone.getText().toString().equals("")) {
			Toast.makeText(this, "请输入联系方式！", Toast.LENGTH_LONG).show();
			return;
		} else if(captcha.getText().toString().equals("")) {
			Toast.makeText(this, "请输入验证码！", Toast.LENGTH_LONG).show();
			return;
		}
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", contact.getText().toString());
		params.put("telephone", telephone.getText().toString());
		params.put("captcha", captcha.getText().toString());
		setNetworkState(HXLApplication.NETWORK_LOADING);
		//提交数据到服务器
		HttpService.getInstance().callService(ServiceInterface.LOGIN, params, login_handler, 10);
	}
	
	private HttpServiceHandler login_handler = new HttpServiceHandler() {
		protected void init() {
			setContext(LoginActivity.this);
		};
		protected boolean handlerResponse(String jsonStr) {
			JsonElement element = new JsonParser().parse(jsonStr);
			if(element.getAsJsonObject().get("status").getAsInt() == 1) {
				
			} else if(element.getAsJsonObject().get("status").getAsInt() == 2) {
				
			} else {
				Toast.makeText(LoginActivity.this, "订单提交成功！", Toast.LENGTH_LONG).show();
				Map<String, String> params = new HashMap<String, String>();
				params.put("name", contact.getText().toString());
				params.put("telephone", telephone.getText().toString());
				params.put("captcha", captcha.getText().toString());
				HttpService.getInstance().callService(ServiceInterface.USER_INFO, params, user_info_handler, 10);
			}
			
			return true;
		};
		
		public void onHttpServiceError(Exception e) {
			
		};
		
	};
	
	private HttpServiceHandler user_info_handler = new HttpServiceHandler() {
		protected void init() {
			setContext(LoginActivity.this);
		};
		protected boolean handlerResponse(String jsonStr) {
			JsonElement element = new JsonParser().parse(jsonStr);
			if(element.getAsJsonObject().get("status").getAsInt() == 1) {
				
			} else if(element.getAsJsonObject().get("status").getAsInt() == 2) {
				
			} else {
				Toast.makeText(LoginActivity.this, "订单提交成功！", Toast.LENGTH_LONG).show();
				setNetworkState(HXLApplication.NETWORK_NORMAL);
			}
			
			return true;
		};
		
		public void onHttpServiceError(Exception e) {
			
		};
		
	};
	
}
