package com.jiaodong.hxl.widget;

import com.jiaodong.hxl.R;
import com.jiaodong.hxl.ImageService.ImageService;
import com.jiaodong.hxl.ImageService.ImageService.ImageCallback;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 根据基地址+文件夹+文件名拼接URL从服务端获取图片资源展现到控件上 如果网络不可用就显示默认图片 如果网络上没有该资源则显示默认图片
 * 
 * @author 陈鹏
 * 
 */
public class OnlineImageView extends ImageView {

	// 默认图片，可在配置文件中配置
	private Drawable defaultDrawable;

	public OnlineImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.OnlineImageView);
		defaultDrawable = a
				.getDrawable(R.styleable.OnlineImageView_defaultDrawable);
		setBackgroundDrawable(defaultDrawable);
		//setImageDrawable(defaultDrawable);
		//setScaleType(ImageView.ScaleType.FIT_XY);
		a.recycle();
	}

	public OnlineImageView(Context context) {
		super(context);
	}

	public Drawable getDefaultDrawable() {
		return defaultDrawable;
	}

	public void setDefaultDrawable(Drawable defaultDrawable) {
		this.defaultDrawable = defaultDrawable;
	}

	public void setDefaultDrawable(int drawAbleId) {
		this.defaultDrawable = getContext().getResources().getDrawable(
				drawAbleId);
	}

	public void setImageDrawable(String imageUrl,final ViewGroup parent,final int position) {
		ImageService.getInstance().loadBitmap(imageUrl, this, true, ImageService.GET_IMAGE_TAP_NOT_LOAD,new ImageCallback() {
			@Override
			public void imageLoad(ImageView imageView, Bitmap bitmap,int imageType) {
				Bitmap newBitmap;
				if(imageType == ImageService.IMG_TYPE_LOCAL || imageType == ImageService.IMG_TYPE_TAP_TO_LOAD){
					newBitmap = handleImageInterface.handleImage(imageView,bitmap);
					imageView.setImageBitmap(newBitmap);
					imageView.setScaleType(ScaleType.FIT_XY);
				}else if(imageType == ImageService.IMG_TYPE_PLACEHOLDER){
					imageView.setImageDrawable(defaultDrawable);
				}else if(imageType == ImageService.IMG_TYPE_NETWORK){
					if (parent instanceof ListView) {
						ListView listView = (ListView)parent;
						int firstPosition = listView.getFirstVisiblePosition();
						int lastPosition = firstPosition + listView.getChildCount();
						// 图片加载完成时，若启动加载线程的view依然在可视范围内，则填充图片.
						// 否则有可能view已经被复用，已被其他图片填充，则本地加载的图片只写入缓存，不做其他操作
						// firstPosition-1为实验性结果，按逻辑讲应该用firstPosition
						if( firstPosition-1 <= position && position < lastPosition ){	
							addBitmapToImageView(imageView,bitmap);
						}
					}else{
						addBitmapToImageView(imageView,bitmap);
					}
					
				}
			}
			
			private void addBitmapToImageView(ImageView imageView, Bitmap bitmap){
				if(bitmap != null){
					Bitmap newBitmap = handleImageInterface.handleImage(imageView,bitmap);
					imageView.setImageBitmap(newBitmap);
					imageView.setScaleType(ScaleType.FIT_XY);
				}else{
//					System.out.println("从网络获取图片失败");
					//TODO 显示获取网络图片失败示意图，若允许点击重新加载，可以在这里显示重新加载示意图
				}
			}
		});		
	}
	
	public interface HandleImageInterface{
		public Bitmap handleImage(ImageView imageView, Bitmap bitmap) ;
	}

	HandleImageInterface handleImageInterface = new HandleImageInterface() {
		
		@Override
		public Bitmap handleImage(ImageView imageView, Bitmap bitmap) {
			// TODO Auto-generated method stub
			return bitmap;
		}
	};

	public void setHandleImageInterface(HandleImageInterface handleImageInterface) {
		this.handleImageInterface = handleImageInterface;
	}
	
}
