package com.jiaodong.hxl;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class SettingsActivity extends HXLActivity {
	
	Button backButton;
	Button exitButton;
	RelativeLayout aboutusLayout;
	RelativeLayout feedbackLayout;
	RelativeLayout clearLayout;
	RelativeLayout checkupdateLayout;
	RelativeLayout scoreLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		backButton = (Button) findViewById(R.id.setting_back_btn);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		exitButton = (Button) findViewById(R.id.setting_exit_btn);
		exitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		aboutusLayout = (RelativeLayout) findViewById(R.id.setting_aboutus);
		aboutusLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		feedbackLayout = (RelativeLayout) findViewById(R.id.setting_feedback);
		feedbackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		clearLayout = (RelativeLayout) findViewById(R.id.setting_clear);
		clearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		checkupdateLayout = (RelativeLayout) findViewById(R.id.setting_checkupdate);
		checkupdateLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		scoreLayout = (RelativeLayout) findViewById(R.id.setting_score);
		scoreLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
