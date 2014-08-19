package com.jiaodong.hxl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class LogoActivity extends HXLActivity {
	ImageView topImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		MobclickAgent.updateOnlineConfig(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		
		// 设置布局
		RelativeLayout layout = new RelativeLayout(this);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		layout.setBackgroundResource(R.drawable.start);
		topImageView = new ImageView(this);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		topImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		topImageView.setLayoutParams(layoutParams);

		layout.addView(topImageView);
		setContentView(layout);
		
		// 设置延迟，播放登陆界面
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent i2 = new Intent();
				i2.setClass(LogoActivity.this, MainActivity.class);
				startActivity(i2);
				LogoActivity.this.finish();
			}
		}, 3000);
	}
}
