package com.jiaodong.hxl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jiaodong.hxl.entity.Dish;
import com.jiaodong.hxl.entity.Order;
import com.jiaodong.hxl.http.HttpService;
import com.jiaodong.hxl.http.HttpServiceHandler;
import com.jiaodong.hxl.http.ServiceInterface;
import com.jiaodong.hxl.widget.wheel.AbstractWheel;
import com.jiaodong.hxl.widget.wheel.OnWheelChangedListener;
import com.jiaodong.hxl.widget.wheel.OnWheelClickedListener;
import com.jiaodong.hxl.widget.wheel.WheelVerticalView;
import com.jiaodong.hxl.widget.wheel.adapter.ArrayWheelAdapter;

public class OrderConfirmActivity extends HXLActivity{

	Button back_btn,submit_btn;
	EditText contact_edt,tel_edt;
	ArrayList<Dish> orderedDishes;//已经点过的菜集合
	Order order;
	String[] order_times,order_today_times,order_dates;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderconfirm);
		context = this;
		orderedDishes = getIntent().getParcelableArrayListExtra("dishes");
		order = new Order();
		initTimes();
		
		WheelVerticalView date_wheel = (WheelVerticalView) findViewById(R.id.orderconfirm_date_wheel);
		final WheelVerticalView time_wheel = (WheelVerticalView) findViewById(R.id.orderconfirm_time_wheel);
		ArrayWheelAdapter<String> dateAdapter = new ArrayWheelAdapter<String>(context, order_dates);
		ArrayWheelAdapter<String> timeAdapter = new ArrayWheelAdapter<String>(context, order_today_times.length!=0?order_today_times:order_times);
		dateAdapter.setTextSize(20);
		dateAdapter.setTextTypeface(Typeface.DEFAULT);
		dateAdapter.setTextColor(Color.rgb(88, 63, 52));
		date_wheel.setViewAdapter(dateAdapter);
		date_wheel.setCurrentItem(0);
		date_wheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				order.setDate(order_dates[newValue]);
				ArrayWheelAdapter<String> timeAdapter;
				if(newValue == 0 && order_today_times.length != 0) {
					timeAdapter = new ArrayWheelAdapter<String>(context, order_today_times);
				} else {
					timeAdapter = new ArrayWheelAdapter<String>(context, order_times);
				}
				timeAdapter.setTextSize(20);
				timeAdapter.setTextTypeface(Typeface.DEFAULT);
				timeAdapter.setTextColor(Color.rgb(88, 63, 52));
				time_wheel.setViewAdapter(timeAdapter);
			}
		});
		date_wheel.addClickingListener(new OnWheelClickedListener() {
			@Override
			public void onItemClicked(AbstractWheel wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		});
		
		timeAdapter.setTextSize(20);
		timeAdapter.setTextTypeface(Typeface.DEFAULT);
		timeAdapter.setTextColor(Color.rgb(88, 63, 52));
		time_wheel.setViewAdapter(timeAdapter);
		time_wheel.setCurrentItem(0);
		time_wheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if(order.getDate().equals("今天") && order_today_times.length != 0) {
					order.setTime(order_today_times[newValue]);
				} else {
					order.setTime(order_times[newValue]);
				}
			}
		});
		time_wheel.addClickingListener(new OnWheelClickedListener() {
			@Override
			public void onItemClicked(AbstractWheel wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		});
		
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.orderconfirm_radioGroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int index) {
				order.setRoom(index==0?false:true);
			}
		});
		contact_edt = (EditText) findViewById(R.id.orderconfim_contact_edt);
		tel_edt = (EditText) findViewById(R.id.orderconfim_tel_edt);
		back_btn = (Button) findViewById(R.id.confirm_back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		submit_btn = (Button) findViewById(R.id.confirm_submit_btn);
		submit_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
			}
		});
	}
	
	private void submit() {
		if(contact_edt.getText().toString().equals("")) {
			Toast.makeText(context, "请输入联系人姓名！", Toast.LENGTH_LONG).show();
			return;
		} else if(tel_edt.getText().toString().equals("")) {
			Toast.makeText(context, "请输入联系方式！", Toast.LENGTH_LONG).show();
			return;
		}
		
		order.setName(contact_edt.getText().toString());
		order.setTel(tel_edt.getText().toString());
		order.setHotel_id(HXLApplication.hotel.getId());
		order.setPeople(Integer.valueOf(getIntent().getStringExtra("people")));
		order.setPrice(getIntent().getIntExtra("price",0));
		
		System.out.println(order);
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("id", String.valueOf(order.getHotel_id()));
		params1.put("people", String.valueOf(order.getPeople()));
		params1.put("name", String.valueOf(order.getName()));
		params1.put("tel", String.valueOf(order.getTel()));
		params1.put("time", order.getDate()+order.getTime());
		params1.put("room", order.isRoom()?"1":"0");
		params1.put("price", String.valueOf(order.getPrice()));
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < orderedDishes.size(); i++) {
			Dish dish = orderedDishes.get(i);
			sb.append(dish.getId()).append("_").append(dish.getCount()).append("-");
		}
		params1.put("dish", sb.toString());
		System.out.println(sb.toString());
		
		HttpService.getInstance().callService(ServiceInterface.CREATE_ORDER, params1, create_order_handler, 10);
		
	}
	
	private HttpServiceHandler create_order_handler = new HttpServiceHandler() {
		protected void init() {
			setContext(OrderConfirmActivity.this);
		};
		protected boolean handlerResponse(String jsonStr) {
			JsonElement element = new JsonParser().parse(jsonStr);
			if(element.getAsJsonObject().get("status").getAsInt() == 1) {
			} else if(element.getAsJsonObject().get("status").getAsInt() == 2) {
			} else {
				Toast.makeText(context, "订单提交成功！", Toast.LENGTH_LONG).show();
			}
			return true;
		};
		
		public void onHttpServiceError(Exception e) {
		};
		
	};
	
	private void initTimes(){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> list_today = new ArrayList<String>();
		Calendar c = Calendar.getInstance();
		Calendar c1 = Calendar.getInstance();
		Date date_start = new Date();
		Date date_end = new Date();
		for (int m = 0; m < HXLApplication.hotel.getTime().length; m++) {
			String startTime = HXLApplication.hotel.getTime()[m].getStart_time();
			String endTime = HXLApplication.hotel.getTime()[m].getEnd_time();
			int interval = HXLApplication.hotel.getTime()[m].getInterval();
			try {
				date_start = sdf.parse(startTime);
				date_end = sdf.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			c.set(Calendar.HOUR_OF_DAY, date_start.getHours());
			c.set(Calendar.MINUTE, date_start.getMinutes());
			c1.set(Calendar.HOUR_OF_DAY, date_end.getHours());
			c1.set(Calendar.MINUTE, date_end.getMinutes());
			while(c.before(c1)){
				StringBuffer sb = new StringBuffer();
				sb.append(c.get(Calendar.HOUR_OF_DAY)).append(":");
				if(c.get(Calendar.MINUTE)<10) {
					sb.append("0");
				} 
				sb.append(c.get(Calendar.MINUTE))
				.append("-");
				c.add(Calendar.MINUTE, interval);
				sb.append(c.get(Calendar.HOUR_OF_DAY)).append(":");
				if(c.get(Calendar.MINUTE)<10) {
					sb.append("0");
				} 
				sb.append(c.get(Calendar.MINUTE));
				list.add(sb.toString());
				
				//当前时间在区间段时间内
				if(Calendar.getInstance().before(c)){
					list_today.add(sb.toString());
				}
			}
		}
		
		order_times = new String[list.size()];
		order_times = list.toArray(order_times);
		order_today_times = new String[list_today.size()];
		order_today_times = list_today.toArray(order_today_times);
		if(order_today_times.length==0) {//默认选择明天
			order_dates = new String[5];
			order_dates[0] = "明天";
			StringBuffer sb = new StringBuffer();
			for (int i = 1; i < order_dates.length; i++) {
				sb.setLength(0);
				Calendar c2 = Calendar.getInstance();
				int day_of_month = c2.get(Calendar.DAY_OF_MONTH)+i+1;
				c2.set(Calendar.DAY_OF_MONTH, day_of_month);
				order_dates[i] = sb.append(c2.get(Calendar.MONTH)+1).append("月")
					.append(c2.get(Calendar.DAY_OF_MONTH)).append("日").toString();
			}
			order.setTime(order_times[0]);
		} else {
			initDates();
			order.setTime(order_today_times[0]);
		}
		order.setDate(order_dates[0]);
	}
	
	private void initDates(){
		order_dates = new String[5];
		order_dates[0] = "今天";
		order_dates[1] = "明天";
		StringBuffer sb = new StringBuffer();
		for (int i = 2; i < order_dates.length; i++) {
			sb.setLength(0);
			Calendar c = Calendar.getInstance();
			int day_of_month = c.get(Calendar.DAY_OF_MONTH)+i;
			c.set(Calendar.DAY_OF_MONTH, day_of_month);
			order_dates[i] = sb.append(c.get(Calendar.MONTH)+1).append("月")
				.append(c.get(Calendar.DAY_OF_MONTH)).append("日").toString();
		}
	}
}
