package com.jiaodong.hxl;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jiaodong.hxl.entity.Dish;
import com.jiaodong.hxl.widget.EditTextWithIcon;
import com.jiaodong.hxl.widget.EditTextWithIcon.OnLeftDrawableClickedListener;
import com.jiaodong.hxl.widget.EditTextWithIcon.OnRightDrawableClickedListener;
import com.jiaodong.hxl.widget.OnlineImageView;

public class OrderActivity extends HXLActivity{
	Button back_btn,submit_btn;
	TextView total_tv;
	ListView listview;
	
	OrderListAdapter listAdapter;
	Context context;
	ArrayList<Dish> orderedDishes;//已经点过的菜集合
	int price = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);
		context = this;
		orderedDishes = getIntent().getParcelableArrayListExtra("dishes");
		total_tv = (TextView) findViewById(R.id.order_total_tv);
		back_btn = (Button) findViewById(R.id.order_back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		submit_btn = (Button) findViewById(R.id.order_submit_btn);
		submit_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmOrder();
			}
		});
		listview = (ListView) findViewById(R.id.order_list);
		listAdapter = new OrderListAdapter(context);
		listview.setAdapter(listAdapter);
		refreshOrder();
	}
	
	protected void confirmOrder() {
		Intent i = new Intent(context, OrderConfirmActivity.class);
		i.putParcelableArrayListExtra("dishes", orderedDishes);
		i.putExtra("people", getIntent().getStringExtra("people"));
		i.putExtra("price", price);
		startActivity(i);
//		dialog.setContentView(view);
//		dialog.setView(inflater.inflate(R.layout.orderconfirm, null));
//		dialog.show(); 
//		dialog.setCanceledOnTouchOutside(false);
//		dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
//		dialog.getWindow().setContentView(view);
	}
	
	private void refreshOrder() {
		int dish_count=0;
		price=0;
		for (int i = 0; i < orderedDishes.size(); i++) {
			Dish dish = orderedDishes.get(i);
			dish_count+=dish.getCount();
			price+=dish.getPrice()*dish.getCount();
		}
		total_tv.setText("共计 "+dish_count+" 个菜，￥"+ price);
	}
	
	class OrderListAdapter extends BaseAdapter{
		LayoutInflater inflater;
		public OrderListAdapter(Context context) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return orderedDishes.size();
		}

		@Override
		public Object getItem(int position) {
			return orderedDishes.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			ViewHolder viewHolder = null;
			final Dish dish = orderedDishes.get(position);
			if(convertView == null) {
				convertView = inflater.inflate(R.layout.dish_item2, null);
				viewHolder = new ViewHolder();
				viewHolder.price = (TextView) convertView.findViewById(R.id.dish_price2);
				viewHolder.name = (TextView) convertView.findViewById(R.id.dish_name2);
				viewHolder.weight = (TextView) convertView.findViewById(R.id.dish_weight2);
				viewHolder.count = (EditTextWithIcon) convertView.findViewById(R.id.dish_item_edt2);
				viewHolder.mpic = (OnlineImageView) convertView.findViewById(R.id.dish_imgv2);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.count.setText(dish.getCount()+"");
			viewHolder.count.setOnRightDrawableClickedListener(new OnRightDrawableClickedListener() {
				@Override
				public void onClick(EditTextWithIcon edt) {
					int i = Integer.valueOf(edt.getText().toString())+1;
					edt.setText(i+"");
					dish.setCount(i);
					orderedDishes.set(position, dish);
					refreshOrder();
				}
			});
			viewHolder.count.setOnLeftDrawableClickedListener(new OnLeftDrawableClickedListener() {
				@Override
				public void onClick(EditTextWithIcon edt) {
					int i = Integer.valueOf(edt.getText().toString())-1;
					if(i>0) {
						edt.setText(i+"");
						dish.setCount(i);
						orderedDishes.set(position, dish);
					} else {
						orderedDishes.remove(position);
						listAdapter.notifyDataSetInvalidated();
					}
					refreshOrder();
				}
			});
			viewHolder.mpic.setImageDrawable(dish.getImage_small(), parent, position);
			viewHolder.mpic.setAdjustViewBounds(true);
			viewHolder.mpic.setScaleType(ImageView.ScaleType.FIT_XY);
			viewHolder.mpic.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WindowManager manager = getWindowManager();  
			        Display display = manager.getDefaultDisplay();  
			        int width = display.getWidth()-60;  
					final AlertDialog dialog = new AlertDialog.Builder(context).create();
					View view = inflater.inflate(R.layout.dish_detail, null);
					TextView name_top = (TextView) view.findViewById(R.id.dish_detail_name_top);
					name_top.setText(dish.getName());
					TextView name = (TextView) view.findViewById(R.id.dish_detail_name_tv);
					name.setText(dish.getName());
					TextView price = (TextView) view.findViewById(R.id.dish_detail_price_tv);
					price.setText("￥"+dish.getPrice());
					TextView ingredients = (TextView) view.findViewById(R.id.dish_detail_ingredients);
					ingredients.setText(dish.getContent());
					OnlineImageView imgv = (OnlineImageView) view.findViewById(R.id.dish_detail_imgv);
					imgv.setImageDrawable(dish.getImage_big(), parent, position);
					imgv.setAdjustViewBounds(true);
					imgv.setScaleType(ImageView.ScaleType.FIT_XY);
					ImageView tuijian = (ImageView) view.findViewById(R.id.dish_detail_tuijian_imgv);
					if(dish.getRecommend() == 1) {
						tuijian.setVisibility(View.VISIBLE);
					} else {
						tuijian.setVisibility(View.GONE);
					}
					TextView weight = (TextView) view.findViewById(R.id.dish_detail_weight);
					weight.setText("/" + dish.getWeight() + "g");
					TextView eat_count = (TextView) view.findViewById(R.id.dish_detail_eat_count);
					eat_count.setText(dish.getOrder_number()+ "人点过");
					TextView like_count = (TextView) view.findViewById(R.id.dish_detail_like_count);
					like_count.setText(dish.getZan()+"人喜欢");
					Button add_btn = (Button) view.findViewById(R.id.dish_detail_add);
					add_btn.setVisibility(View.INVISIBLE);
					Button close_btn = (Button) view.findViewById(R.id.dish_detail_close_btn);
					close_btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show(); 
					dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
					dialog.getWindow().setContentView(view);
					
				}
			});
			viewHolder.name.setText(dish.getName());
			viewHolder.price.setText("￥" + dish.getPrice());
			viewHolder.weight.setText(dish.getWeight());
			return convertView;
		}
		
		class ViewHolder{
			TextView name;
			TextView price;
			TextView weight;
			OnlineImageView mpic;
			EditTextWithIcon count;
		}
		
	}
}
