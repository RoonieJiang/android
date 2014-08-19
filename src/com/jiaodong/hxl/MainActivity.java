package com.jiaodong.hxl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jiaodong.hxl.entity.Hotel;
import com.jiaodong.hxl.http.HttpService;
import com.jiaodong.hxl.http.HttpServiceHandler;
import com.jiaodong.hxl.http.ServiceInterface;
import com.jiaodong.hxl.map.RouteActivity;
import com.jiaodong.hxl.widget.AdvGallery;
import com.jiaodong.hxl.widget.OnlineImageView;
import com.jiaodong.hxl.widget.wheel.AbstractWheel;
import com.jiaodong.hxl.widget.wheel.OnWheelChangedListener;
import com.jiaodong.hxl.widget.wheel.OnWheelClickedListener;
import com.jiaodong.hxl.widget.wheel.adapter.ArrayWheelAdapter;

public class MainActivity extends HXLActivity implements OnWheelClickedListener {
	Button start;
	AdvGallery gallery;
	RadioGroup radioGroup;
	Context context;
	private Timer timer;
	private Timer quitTimer = new Timer();
	private boolean showNextHeader = true;
	private boolean ifQuit = false;
	TextView countTextView;
	LinearLayout mainBottom;
	TextView addressView;
	TextView telephoneView;
	TextView selectCount;
	// ProgressDialog dialog;
	int max;
	double lon;
	double lat;
	String HotelName;
	String HotelAddr;
	ArrayWheelAdapter<String> peopleAdapter;
	AbstractWheel people_count;
	ImageButton topLeftButton;
	ImageButton topRightButton;
	
	final int AnimationDelay = 1000;

	private String[] counts = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };

	@Override
	protected void networkRetry() {
		// TODO Auto-generated method stub
		super.networkRetry();

		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "1");
		HttpService.getInstance().callService(ServiceInterface.GET_HOTEL_INFO, params, hotel_handler, 10);
		ContentView = R.layout.main;
		setNetworkState(HXLApplication.NETWORK_LOADING);
	}

	@Override
	protected void initActivity() {
		// TODO Auto-generated method stub
		setContentView(R.layout.main);

		topLeftButton = (ImageButton) findViewById(R.id.top_left_button);
		topLeftButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(context, LoginActivity.class);
				SharedPreferences preferences = getSharedPreferences(HXLApplication.LOGIN_TEL, 0);
				String tel = preferences.getString(HXLApplication.LOGIN_TEL_KEY, "");
				intent.putExtra("login_intent", tel);
				startActivity(intent);
			}
		});
		topRightButton = (ImageButton) findViewById(R.id.top_right_button);
		topRightButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(context, SettingsActivity.class);
				startActivity(intent);
			}
		});
		/*
		 * dialog = new ProgressDialog(context);
		 * dialog.setMessage("正在获取酒店信息..."); dialog.show();
		 */
		gallery = (AdvGallery) findViewById(R.id.main_gallery);

		gallery.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				showNextHeader = false;
				timer.cancel();
				if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					showNextHeader = true;
					startTimer();
				}
				return false;
			}
		});

		addressView = (TextView) findViewById(R.id.address_text);
		addressView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		addressView.getPaint().setAntiAlias(true);
		addressView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("name", HotelName);
				intent.putExtra("addr", HotelAddr);
				intent.putExtra("lat", lat);
				intent.putExtra("lon", lon);
				Log.d("latlon", lat + "," + lon);
				intent.setClass(context, RouteActivity.class);
				startActivity(intent);
			}
		});

		telephoneView = (TextView) findViewById(R.id.telephone_text);
		telephoneView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		telephoneView.getPaint().setAntiAlias(true);
		telephoneView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL); // android.intent.action.DIAL
				intent.setData(Uri.parse("tel:" + telephoneView.getText()));
				startActivity(intent);
			}
		});

		countTextView = (TextView) findViewById(R.id.currentCount);
		countTextView.setText("0");
		Message message = countHighlightHandler.obtainMessage(0);
		message.arg1 = 0;
		countHighlightHandler.sendMessageDelayed(message, AnimationDelay);

		people_count = (AbstractWheel) findViewById(R.id.people_count_wheel);
		peopleAdapter = new ArrayWheelAdapter<String>(this, counts);
		peopleAdapter.setItemResource(R.layout.wheel_text_centered);
		peopleAdapter.setItemTextResource(R.id.wheel_text);
		people_count.addClickingListener(this);
		people_count.setViewAdapter(peopleAdapter);
		people_count.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (newValue != 0) {
					setStartButtonClickable(true);
				} else {
					setStartButtonClickable(false);
				}
				countTextView.setText(newValue + "");
			}
		});

		start = (Button) findViewById(R.id.main_btn_start);
		start.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent();
				i.setClass(context, MenuActivity.class);
				i.putExtra("people", countTextView.getText().toString());
				startActivity(i);
			}
		});

		setStartButtonClickable(false);
		startTimer();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;

		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "1");
		HttpService.getInstance().callService(ServiceInterface.GET_HOTEL_INFO, params, hotel_handler, 10);
		ContentView = R.layout.main;
		setNetworkState(HXLApplication.NETWORK_LOADING);
	}

	private HttpServiceHandler hotel_handler = new HttpServiceHandler() {
		protected void init() {
			setContext(MainActivity.this);
		};

		protected boolean handlerResponse(String jsonStr) {
			JsonElement element = new JsonParser().parse(jsonStr);
			if (element.getAsJsonObject().get("status").getAsInt() == 1) {
				// Toast.makeText(context, "获取酒店信息失败！",
				// Toast.LENGTH_LONG).show();
				setNetworkState(HXLApplication.NETWORK_NOTAVAILABLE);
			} else {
				setNetworkState(HXLApplication.NETWORK_NORMAL);
				JsonObject object = element.getAsJsonObject().getAsJsonObject("data");
				Hotel hotel = new Gson().fromJson(object, Hotel.class);
				hotel.setId(1);
				System.out.println(hotel.toString());
				HXLApplication.hotel = hotel;
				telephoneView.setText(hotel.getTel());
				addressView.setText(hotel.getAddress());
				gallery.setAdapter(new AdvGalleryAdapter(hotel.getImg()));
				HotelName = hotel.getName();
				HotelAddr = hotel.getAddress();
				max = hotel.getMax();
				lat = hotel.getlat();
				lon = hotel.getlon();
				counts = new String[max + 1];
				for (int i = 0; i <= max; i++) {
					counts[i] = i + "";
				}
				peopleAdapter = new ArrayWheelAdapter<String>(MainActivity.this, counts);
				peopleAdapter.setItemResource(R.layout.wheel_text_centered);
				peopleAdapter.setItemTextResource(R.id.wheel_text);
				people_count.setViewAdapter(peopleAdapter);
			}
			// dialog.dismiss();
			return true;
		};

		public void onHttpServiceError(Exception e) {
			// Toast.makeText(context, "获取酒店信息失败！", Toast.LENGTH_LONG).show();
			// dialog.dismiss();
			setNetworkState(HXLApplication.NETWORK_NOTAVAILABLE);
		};

	};

	Handler changeHeader = new Handler() {
		boolean right = true;

		@Override
		public void handleMessage(Message msg) {
			if (showNextHeader) {
				if (gallery.getSelectedItemPosition() >= gallery.getCount() - 1) {// 轮播到最后一个
					right = false;
				} else if (gallery.getSelectedItemPosition() <= 0) {
					right = true;
				}
				if (right) {
					gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
				} else {
					gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
				}
			}
		}
	};

	private void startTimer() {// 开始头图轮播
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (showNextHeader) {
					changeHeader.sendMessage(new Message());
				}
			}
		}, 5000, 5000);
	}

	public class AdvGalleryAdapter extends BaseAdapter {
		ArrayList<String> imgs;

		public AdvGalleryAdapter(ArrayList<String> imgs) {
			this.imgs = imgs;
		}

		@Override
		public int getCount() {
			return this.imgs.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new OnlineImageView(context);
			}
			OnlineImageView imageView = (OnlineImageView) convertView;
			imageView.setImageDrawable(imgs.get(position), parent, position);
			imageView.setAdjustViewBounds(true);
			imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			return convertView;
		}

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (ifQuit) {
				finish();
			} else {
				ifQuit = true;
				Toast.makeText(this, "请再按一次退出程序", Toast.LENGTH_SHORT).show();
				quitTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						ifQuit = false;
					}
				}, 2000);
			}
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (timer != null) {
			showNextHeader = false;
			timer.cancel();
			if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
				showNextHeader = true;
				startTimer();
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onResume() {
		if (timer != null) {
			showNextHeader = true;
			startTimer();
		}
		super.onResume();
	}

	protected void onPause() {
		if (timer != null) {
			showNextHeader = false;
			timer.cancel();
		}
		super.onPause();
	};

	private void setStartButtonClickable(boolean clickable) {
		mainBottom = (LinearLayout) findViewById(R.id.main_bottom_ll);
		selectCount = (TextView) findViewById(R.id.poeple_image);
		Message message = countHighlightHandler.obtainMessage(0);
		if (clickable) {
			mainBottom.setBackgroundColor(Color.parseColor("#C30A0A"));
			start.setBackgroundResource(R.drawable.btn_start_bg_normal);
			start.setTextColor(Color.WHITE);
			start.setClickable(clickable);
			selectCount.setVisibility(View.GONE);
			message.arg1 = -1;
			countHighlightHandler.sendMessage(message);
		} else {
			start.setBackgroundColor(Color.parseColor("#f0f0f0"));
			start.setBackgroundResource(R.drawable.btn_start_bg_selected);
			start.setTextColor(Color.parseColor("#C0C0C0"));
			start.setClickable(clickable);
			selectCount.setVisibility(View.VISIBLE);
			message.arg1 = 0;
			countHighlightHandler.sendMessageDelayed(message, AnimationDelay);
		}
	}

	Handler countHighlightHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case -1:
				countTextView.setBackgroundResource(R.drawable.count_selected);
				countHighlightHandler.removeMessages(0);
				break;

			case 0:
				countTextView.setBackgroundResource(R.drawable.count_selected_1);
				Message message = countHighlightHandler.obtainMessage(0);
				message.arg1 = 1;
				countHighlightHandler.sendMessageDelayed(message, AnimationDelay);
				break;

			case 1:
				countTextView.setBackgroundResource(R.drawable.count_selected);
				Message message1 = countHighlightHandler.obtainMessage(0);
				message1.arg1 = 0;
				countHighlightHandler.sendMessageDelayed(message1, AnimationDelay);
				break;
			}
		};
	};

	@Override
	public void onItemClicked(AbstractWheel wheel, int itemIndex) {
		// TODO Auto-generated method stub
		wheel.setCurrentItem(itemIndex, true);
	}

}
