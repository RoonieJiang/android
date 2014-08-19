package com.jiaodong.hxl;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

public class HXLActivity extends Activity {
	HXLApplication application;
	int ContentView;

	TextView retry;
	TextView check;
	
	public void setNetworkState(int state) {
		switch (state) {
		case HXLApplication.NETWORK_LOADING:
			setContentView(R.layout.network_state);
			findViewById(R.id.network_notavailable).setVisibility(View.GONE);
			break;
		case HXLApplication.NETWORK_NOTAVAILABLE:
			setContentView(R.layout.network_state);
			findViewById(R.id.network_loading).setVisibility(View.GONE);
			retry = (TextView) findViewById(R.id.retry_text);
			check = (TextView) findViewById(R.id.check_text);
			retry.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					networkRetry();
				}
			});
			check.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
		            startActivity(intent);
				}
			});
			break;
		case HXLApplication.NETWORK_NORMAL:
			initActivity();
			break;
		}
	}

	protected void initActivity() {
		
	}
	
	protected void networkRetry() {
		
	}
	
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
