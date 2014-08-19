package com.jiaodong.hxl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jiaodong.hxl.entity.Dish;
import com.jiaodong.hxl.entity.DishSet;
import com.jiaodong.hxl.entity.DishType;
import com.jiaodong.hxl.http.HttpService;
import com.jiaodong.hxl.http.HttpServiceHandler;
import com.jiaodong.hxl.http.ServiceInterface;
import com.jiaodong.hxl.widget.AdvGallery;
import com.jiaodong.hxl.widget.EditTextWithIcon;
import com.jiaodong.hxl.widget.EditTextWithIcon.OnBottomDrawableClickedListener;
import com.jiaodong.hxl.widget.EditTextWithIcon.OnTopDrawableClickedListener;
import com.jiaodong.hxl.widget.OnlineImageView;
import com.jiaodong.hxl.widget.SeparatedListAdapter;
import com.jiaodong.hxl.widget.EditTextWithIcon.OnLeftDrawableClickedListener;
import com.jiaodong.hxl.widget.EditTextWithIcon.OnRightDrawableClickedListener;

public class MenuActivity extends HXLActivity {

	FrameLayout layout;
	Button back_btn,tuijian_btn,submit_btn;
	AdvGallery gallery;
	TextView total_tv,price_tv,avg_price_tv;
	ListView leftListView,rightListView;
	List<DishType> dishTypes;//左侧菜系
	int[] dishesCount;
	int[] orderedDishesCount;//已经点过的菜计数，显示在左侧菜系表中
	ArrayList<boolean[]> ifDishesOrdered;//右侧菜单是否已点
	ArrayList<Dish> allDishes;//所有菜的集合
	ArrayList<ArrayList<Dish>> dishes;//所有菜的分类集合
	ArrayList<Dish> orderedDishes;//已经点过的菜集合
	ArrayList<Dish> setDishes = new ArrayList<Dish>();//套餐菜品集合
	boolean selectSetDish = false;//是否选中了推荐套餐
	Context context;
	int selectPos = 0;//左侧listview的选中位置
	private Timer timer;
	private boolean showNextHeader = true;
	private ArrayList<String> headerImgs = new ArrayList<String>();
	
	LeftAdapter leftAdapter;
	SeparatedListAdapter rightAdapter;
	AlertDialog dialog;
	
	@Override
	protected void networkRetry() {
		super.networkRetry();
		setNetworkState(HXLApplication.NETWORK_LOADING);
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "1");
		
		HttpService.getInstance().callService(ServiceInterface.GET_DISH_TYPE_AND_LIST, params, dish_type_handler, 10);
		
	}
	
	@Override
	protected void initActivity() {
		super.initActivity();
		setContentView(R.layout.menu);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		setNetworkState(HXLApplication.NETWORK_LOADING);
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "1");
		
		HttpService.getInstance().callService(ServiceInterface.GET_DISH_TYPE_AND_LIST, params, dish_type_handler, 10);
		
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("id", "1");
		params1.put("people", getIntent().getStringExtra("people"));
		
		HttpService.getInstance().callService(ServiceInterface.GET_SET_DISH, params1, set_dish_handler, 10);
		
	}
	
	private HttpServiceHandler dish_type_handler = new HttpServiceHandler() {
		protected void init() {
			setContext(MenuActivity.this);
		};
		protected boolean handlerResponse(String jsonStr) {
			JsonElement element = new JsonParser().parse(jsonStr);
			if(element.getAsJsonObject().get("status").getAsInt() == 1) {
				Toast.makeText(context, "获取菜单失败！", Toast.LENGTH_LONG).show();
				setNetworkState(HXLApplication.NETWORK_NOTAVAILABLE);
			} else {
				setNetworkState(HXLApplication.NETWORK_NORMAL);
				orderedDishes = new ArrayList<Dish>();
				dishes = new ArrayList<ArrayList<Dish>>();
				dishTypes = new ArrayList<DishType>();
				allDishes = new ArrayList<Dish>();
				JsonArray jsonArray = element.getAsJsonObject().getAsJsonArray("data");
				for (int j = 0; j < jsonArray.size(); ++j) {
					JsonObject object = (JsonObject) jsonArray.get(j);
					DishType dishType = new Gson().fromJson(object, DishType.class);
					ArrayList<Dish> innerDishes = new ArrayList<Dish>();
					for (int k = 0; k < dishType.getNumber(); k++) {
						Dish dish = dishType.getDishes().get(k);
						allDishes.add(dish);
						innerDishes.add(dish);
						if(dish.getShow() == 1) {
							headerImgs.add(dish.getImage_big());
						}
					}
					allDishes.add(null);//null的位置，就是section的pos
					dishes.add(innerDishes);
					dishTypes.add(dishType);
					initOrderedDish();
				}
			}
			initView();
			//dialog.dismiss();
			return true;
		};
		
		public void onHttpServiceError(Exception e) {
			//Toast.makeText(context, "获取菜单失败！", Toast.LENGTH_LONG).show();
			//dialog.dismiss();
			setNetworkState(HXLApplication.NETWORK_NOTAVAILABLE);
			//finish();
		};
		
	};
	
	private HttpServiceHandler set_dish_handler = new HttpServiceHandler() {
		protected void init() {
			setContext(MenuActivity.this);
		};
		protected boolean handlerResponse(String jsonStr) {
			JsonElement element = new JsonParser().parse(jsonStr);
			if(element.getAsJsonObject().get("status").getAsInt() == 1) {
				tuijian_btn.setVisibility(View.INVISIBLE);
			} else if(element.getAsJsonObject().get("status").getAsInt() == 2) {
				tuijian_btn.setVisibility(View.INVISIBLE);
			} else {
				tuijian_btn.setVisibility(View.VISIBLE);
				JsonArray jsonArray = element.getAsJsonObject().getAsJsonArray("data");
				for (int j = 0; j < jsonArray.size(); ++j) {
					JsonObject object = (JsonObject) jsonArray.get(j);
					DishSet dishSet = new Gson().fromJson(object, DishSet.class);
					setDishes.addAll(dishSet.getDish_list());
				}
			}
			return true;
		};
		
		public void onHttpServiceError(Exception e) {
		};
		
	};
	
	Handler changeHeader = new Handler() {
		boolean right = true;
		@Override
		public void handleMessage(Message msg) {
			if(showNextHeader) {
				if(gallery.getSelectedItemPosition() >= gallery.getCount()-1) {//轮播到最后一个
					right = false;
				} else if(gallery.getSelectedItemPosition() <=0){
					right = true;
				}
				if(right) {
					gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
				} else {
					gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
				}
			}
		}
	};
	
	private void initOrderedDish() {
		dishesCount = new int[dishTypes.size()];
		for (int i = 0; i < dishesCount.length; i++) {
			dishesCount[i] = dishTypes.get(i).getNumber();
		}
		orderedDishesCount = new int[dishTypes.size()];
		for (int i = 0; i < orderedDishesCount.length; i++) {
			orderedDishesCount[i] = 0;
		}
		ifDishesOrdered = new ArrayList<boolean[]>();
		for (int i = 0; i < dishTypes.size(); i++) {
			boolean[] b = new boolean[dishTypes.get(i).getNumber()];
			ifDishesOrdered.add(b);
		}
	}
	
	private void clearOrderedDishes() {
		for (int i = 0; i < orderedDishesCount.length; i++) {
			orderedDishesCount[i] = 0;
		}
		ifDishesOrdered.clear();
		for (int i = 0; i < dishTypes.size(); i++) {
			boolean[] b = new boolean[dishTypes.get(i).getNumber()];
			ifDishesOrdered.add(b);
		}
		orderedDishes.clear();
	}
	
	private void addToOrderedDishes() {
		clearOrderedDishes();
		for (int i = 0; i < setDishes.size(); i++) {
			Dish sd = setDishes.get(i);
			for (int j = 0; j < dishes.size(); j++) {
				for (int k = 0; k < dishes.get(j).size(); k++) {
					Dish d = dishes.get(j).get(k);
					if(sd.equals(d)) {
						addDish(d,false,null);
					}
				}
			}
		}
		Rect fromRect = new Rect();
		tuijian_btn.getGlobalVisibleRect(fromRect);
		Rect toRect = new Rect();
		leftListView.getChildAt(0).getGlobalVisibleRect(toRect);
		
		final ImageView _img = (ImageView) findViewById(R.id.menu_anim_imgv);
		_img.setImageResource(R.drawable.dish_tuijian);
		
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation translateAnimation = 
				new TranslateAnimation(fromRect.left, toRect.exactCenterX()
						, fromRect.top, toRect.top);
		ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 0.3f, 0.8f, 0.3f); 
		animationSet.setDuration(300);
		animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animationSet.addAnimation(scaleAnimation); 
		animationSet.addAnimation(translateAnimation); 
		animationSet.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				_img.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				_img.setVisibility(View.GONE);
			}
		});
		_img.startAnimation(animationSet);
	}
	
	private void tuijian() {
		if(selectSetDish) {//点击取消推荐
			clearOrderedDishes();
			tuijian_btn.setText("推荐\n套餐");
			selectSetDish = false;
			refresh2List();
		} else {//点击推荐套餐
			if(orderedDishes.size() != 0) {
				if(dialog == null) {
					dialog = new AlertDialog.Builder(context)
					.setMessage("套餐会替换当前已选菜品，是否继续？")
					.setPositiveButton("是", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							addToOrderedDishes();
							tuijian_btn.setText("取消\n推荐");
							selectSetDish = true;
							refresh2List();
						}
					})
					.setNegativeButton("否", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.create();
				}
				dialog.show();
			} else {
				addToOrderedDishes();
				tuijian_btn.setText("取消\n推荐");
				selectSetDish = true;
				refresh2List();
			}
		}
	}
	
	private void submit() {
		if(orderedDishes.size() != 0) {
			Intent i = new Intent(context, OrderActivity.class);
			i.putParcelableArrayListExtra("dishes", orderedDishes);
			i.putExtra("people", getIntent().getStringExtra("people"));
			startActivity(i);
		} else {
			Toast.makeText(context, "您还没有点菜！", Toast.LENGTH_LONG).show();
		}
	}
	
	private void addDish(Dish dish,final boolean refreshList, final View view) {
		int pos = -1;
		int addCountPos = 0;
		for (int i = 0; i < dishes.size(); i++) {
			pos = dishes.get(i).indexOf(dish);
			if(pos>=0) {
				addCountPos = i;
				break;
			}
		}
		if(view != null && !ifDishesOrdered.get(addCountPos)[pos]) {
			final OnlineImageView imgv = (OnlineImageView) view.findViewById(R.id.dish_imgv);
			
			Rect fromRect = new Rect();
			imgv.getGlobalVisibleRect(fromRect);
			Rect toRect = new Rect();
			leftListView.getChildAt(addCountPos).getGlobalVisibleRect(toRect);
			
			final ImageView _img = (ImageView) findViewById(R.id.menu_anim_imgv);
			_img.setImageDrawable(imgv.getDrawable());
			
			AnimationSet animationSet = new AnimationSet(true);
			TranslateAnimation translateAnimation = 
					new TranslateAnimation(fromRect.left, toRect.exactCenterX()
							, fromRect.top, toRect.top);
			ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 0.3f, 0.8f, 0.3f); 
			animationSet.setDuration(300);
			animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
			animationSet.addAnimation(scaleAnimation); 
			animationSet.addAnimation(translateAnimation); 
			animationSet.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					_img.setVisibility(View.VISIBLE);
//					imgv.setVisibility(View.GONE);
//					view.findViewById(R.id.dish_item_edt).setVisibility(View.VISIBLE);
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					_img.setVisibility(View.GONE);
//					if(refreshList) {
//						refresh2List();
//					}
				}
			});
			_img.startAnimation(animationSet);
		} 
		if(orderedDishes.contains(dish)) {//已经点过了，取消
			orderedDishes.remove(dish);
			orderedDishesCount[addCountPos]--;
			ifDishesOrdered.get(addCountPos)[pos]=false;
		} else {
			if(dish.getOnep() == 1) {//默认每人一份
				dish.setCount(Integer.valueOf(getIntent().getStringExtra("people")));
			}
			orderedDishes.add(dish);
			orderedDishesCount[addCountPos]++;
			ifDishesOrdered.get(addCountPos)[pos]=true;
		}
		if(refreshList) {
			refresh2List();
		}
	}
	
	private void refreshRightList() {
		total_tv.setText("已点 "+orderedDishes.size()+" 个菜");
		int price = 0;
		for (int j = 0; j < orderedDishes.size(); j++) {
			price+=orderedDishes.get(j).getPrice()*orderedDishes.get(j).getCount();
		}
		price_tv.setText("￥"+price);
		int people = Integer.valueOf(getIntent().getStringExtra("people"));
		avg_price_tv.setText("，"+people
				+"人，人均￥"+(int)price/people);
		((RightAdapter)rightAdapter.sections.get(dishTypes.get(selectPos).getName())).notifyDataSetChanged();
		rightAdapter.notifyDataSetInvalidated();
	}
	private void refresh2List() {
		
		leftAdapter.notifyDataSetInvalidated();
		refreshRightList();
	}
	
	private void startTimer() {//开始头图轮播
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(showNextHeader) {
					changeHeader.sendMessage(new Message());
				}
			}
		}, 5000,5000);
	}
	
	@Override
	protected void onResume() {
		if(timer != null) {
			showNextHeader = true;
			startTimer();
		}
		super.onResume();
	}
	
	protected void onPause() {
		showNextHeader = false;
		timer.cancel();
		super.onPause();
	};
	
	public class AdvGalleryAdapter extends BaseAdapter{
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
			return arg0 ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = new OnlineImageView(context);
			}
			OnlineImageView imageView = (OnlineImageView)convertView;
			imageView.setImageDrawable(imgs.get(position),parent,position);
			imageView.setAdjustViewBounds(true);
			imageView.setLayoutParams(new Gallery.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			return convertView;
		}
		
	}
	
	class LeftAdapter extends BaseAdapter implements ListAdapter{
		LayoutInflater inflater;
		public LeftAdapter(Context context) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return dishTypes.size();
		}

		@Override
		public Object getItem(int arg0) {
			return dishTypes.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			if(convertView == null) {
				convertView = inflater.inflate(R.layout.dishtype_item, null);
				viewHolder = new ViewHolder();
				viewHolder.count = (TextView) convertView.findViewById(R.id.dishtype_count);
				viewHolder.name = (TextView) convertView.findViewById(R.id.dishtype_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(position == selectPos) {
				convertView.setBackgroundColor(Color.WHITE);
				viewHolder.name.setTextColor(Color.rgb(100, 60, 50));
				viewHolder.count.setBackgroundResource(R.drawable.dishtype_ordernumber_bg);
				viewHolder.count.setTextColor(Color.WHITE);
			} else {
				convertView.setBackgroundResource(R.drawable.dishtype_item_bg);
				viewHolder.name.setTextColor(Color.rgb(250, 235, 100));
				viewHolder.count.setTextColor(Color.rgb(195, 10, 10));
				viewHolder.count.setBackgroundResource(R.drawable.dishtype_ordernumber_bg2);
			}
			if(orderedDishesCount[position] > 0) {
				viewHolder.count.setText("" + orderedDishesCount[position]);
				viewHolder.count.setVisibility(View.VISIBLE);
			} else {
				viewHolder.count.setVisibility(View.GONE);
			}
			viewHolder.name.setText(dishTypes.get(position).getName());
			return convertView;
		}
		
		class ViewHolder{
			TextView name;
			TextView count;
		}
		
	}
	
	//OrderActivity中的listview复用的这个adapter，若修改，记得改另外一个
	class RightAdapter extends BaseAdapter{
		LayoutInflater inflater;
		ArrayList<Dish> dishes;
		int dishTypes_number;
		public RightAdapter(Context context, ArrayList<Dish> dishes, int dishTypes_number) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.dishes = dishes;
			this.dishTypes_number = dishTypes_number;
		}
		
		public ArrayList<Dish> getDishes() {
			return dishes;
		}

		@Override
		public int getCount() {
			return this.dishes.size();
		}

		@Override
		public Object getItem(int position) {
			return this.dishes.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			ViewHolder viewHolder = null;
			final Dish dish = this.dishes.get(position);
			if(convertView == null) {
				convertView = inflater.inflate(R.layout.dish_item, null);
				viewHolder = new ViewHolder();
				viewHolder.price = (TextView) convertView.findViewById(R.id.dish_price);
				viewHolder.name = (TextView) convertView.findViewById(R.id.dish_name);
				viewHolder.weight = (TextView) convertView.findViewById(R.id.dish_weight);
				viewHolder.mpic = (OnlineImageView) convertView.findViewById(R.id.dish_imgv);
				viewHolder.count = (EditTextWithIcon) convertView.findViewById(R.id.dish_item_edt);
				viewHolder.tuijian = (ImageView) convertView.findViewById(R.id.dish_tuijian_imgv);
				viewHolder.eat_count = (TextView) convertView.findViewById(R.id.dish_eat_count);
				viewHolder.like_count = (TextView) convertView.findViewById(R.id.dish_like_count);
				viewHolder.taste = (TextView) convertView.findViewById(R.id.dish_detail_taste);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			OnClickListener onClickListener = new OnClickListener() {
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
					add_btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							addDish(dish,true,null);
							dialog.dismiss();
						}
					});
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
			};
			viewHolder.count.setText(dish.getCount()+ "");
			viewHolder.count.setOnTopDrawableClickedListener(new OnTopDrawableClickedListener() {
				@Override
				public void onClick(EditTextWithIcon edt) {
					System.out.println("on top");
					int i = Integer.valueOf(edt.getText().toString())+1;
					edt.setText(i+"");
					orderedDishes.remove(dish);
					dish.setCount(i);
					orderedDishes.add(dish);
					refreshRightList();
				}
			});
			viewHolder.count.setOnBottomDrawableClickedListener(new OnBottomDrawableClickedListener() {
				@Override
				public void onClick(EditTextWithIcon edt) {
					System.out.println("on bottom");
					int i = Integer.valueOf(edt.getText().toString())-1;
					if(i>0) {
						edt.setText(i+"");
						orderedDishes.remove(dish);
						dish.setCount(i);
						orderedDishes.add(dish);
						refreshRightList();
					} else {//小于=0，即取消，相当于点了一次item
						addDish(dish,true,null);
					}
				}
			});
			viewHolder.mpic.setImageDrawable(dish.getImage_small(), parent, position);
			viewHolder.mpic.setAdjustViewBounds(true);
			viewHolder.mpic.setScaleType(ImageView.ScaleType.FIT_XY);
			viewHolder.mpic.setOnClickListener(onClickListener);
			viewHolder.name.setText(dish.getName());
			viewHolder.name.setOnClickListener(onClickListener);
			viewHolder.price.setText("￥" + dish.getPrice());
			viewHolder.weight.setText("/" + dish.getWeight() + "g");
			viewHolder.eat_count.setText(dish.getOrder_number()+ "人点过");
			viewHolder.like_count.setText(dish.getZan()+"人喜欢");
			//推荐菜品，且未点过，才显示推荐标志
			if(dish.getRecommend() == 1) {
				viewHolder.tuijian.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tuijian.setVisibility(View.GONE);
			}
			if(dish.getDishStatus() != null) {//不能点的菜设置半透明
				viewHolder.mpic.setAlpha(100);
			} else {
				viewHolder.mpic.setAlpha(255);
				if(ifDishesOrdered.get(this.dishTypes_number)[position]) {//已经点过的设置底色
					viewHolder.mpic.setVisibility(View.GONE);
					viewHolder.count.setVisibility(View.VISIBLE);
					convertView.setBackgroundColor(Color.rgb(255, 250, 230));
				} else {
					viewHolder.mpic.setVisibility(View.VISIBLE);
					viewHolder.count.setVisibility(View.GONE);
					convertView.setBackgroundColor(Color.WHITE);
				}
			}
			if(selectPos != this.dishTypes_number) {
				selectPos = this.dishTypes_number;
				leftAdapter.notifyDataSetChanged();
			}
			return convertView;
		}
		
		class ViewHolder{
			TextView name;
			TextView price;
			TextView weight;
			TextView eat_count;
			TextView like_count;
			OnlineImageView mpic;
			ImageView tuijian;
			EditTextWithIcon count;
			TextView taste;
		}
		
	}

	private void initView() {
		layout = (FrameLayout) findViewById(R.id.menu_fl);
		gallery = (AdvGallery) findViewById(R.id.main_gallery);
		gallery.setAdapter(new AdvGalleryAdapter(headerImgs));
		gallery.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				showNextHeader = false;
				timer.cancel();
				if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					showNextHeader = true;
					startTimer();
				}
				return false;
			}
		});
		back_btn = (Button) findViewById(R.id.menu_back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tuijian_btn = (Button) findViewById(R.id.menu_tuijian_btn);
		tuijian_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tuijian();
			}
		});
		submit_btn = (Button) findViewById(R.id.menu_submit_btn);
		submit_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
			}
		});
		total_tv = (TextView) findViewById(R.id.menu_total_tv);
		avg_price_tv = (TextView) findViewById(R.id.menu_avg_price_tv);
		price_tv = (TextView) findViewById(R.id.menu_price_tv);
		leftListView = (ListView) findViewById(R.id.menu_list_left);
		rightListView = (ListView) findViewById(R.id.menu_list_right);
		leftAdapter = new LeftAdapter(context);
		
		rightAdapter  = new SeparatedListAdapter(this);  
		for (int i = 0; i < dishTypes.size(); i++) {
			RightAdapter adapter = new RightAdapter(context, dishTypes.get(i).getDishes(),i);
			rightAdapter.addSection(dishTypes.get(i).getName(), adapter);
		}
		leftListView.setAdapter(leftAdapter);
		rightListView.setAdapter(rightAdapter);
		leftListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int pos = 0;
				selectPos = arg2;
				for(int i=0;i<selectPos;i++) {
					pos=pos+dishTypes.get(i).getNumber();
				}
				rightListView.setSelection(pos+selectPos);
				leftAdapter.notifyDataSetChanged();
			}
		});
		rightListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				Dish dish = allDishes.get(arg2-1);
				if(dish.getDishStatus() == null) {
					addDish(dish, true, view);
				}
			}
		});
//		rightListView.setOnScrollListener(new OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				
//			}
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				System.out.println("onScroll");
//				int cur_sum = 0;
//				for (int i = 0; i < dishesCount.length; i++) {
//					cur_sum+=dishesCount[i];
//					if(cur_sum>=firstVisibleItem+1) {
//						selectPos = i;
//						leftAdapter.notifyDataSetChanged();
//						break;
//					}
//				}
//			}
//		});
		startTimer();
	}
}
