package com.jiaodong.hxl.ImageService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jiaodong.hxl.HXLApplication;
import com.jiaodong.hxl.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * @author jingche.yhq 使用说明： 1.先增加权限： <uses-permission
 *         android:name="android.permission.INTERNET" /> <uses-permission
 *         android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *         2.图片URL和ImageView直接输入函数 EX： ImageService imageService = new
 *         ImageService(this); ImageView imageView;
 *         imageService.setBitmapByURL2(
 *         "20120801/a75_b0e447db_3573_ab5c_2058_5845d13545b9_1.jpg",
 *         imageView,defaultBitmap);
 */
public class ImageService {
	// *********************************************线程池获取图片资源******************************************************
	// 本地数据缓存
	// public Map<String, SoftReference<Bitmap>> imageCacheMap = new
	// HashMap<String, SoftReference<Bitmap>>();
	// 固定五个线程来执行任务
	private ExecutorService executorService = Executors.newFixedThreadPool(5);
	static int imageIndex = 1;
	public final static int GET_IMAGE_FORCE_LOAD = 1;
	public final static int GET_IMAGE_TAP_TO_LOAD = 0;
	public final static int GET_IMAGE_TAP_NOT_LOAD = -1;
	int mForce = 0;
	// 回调函数使用

	final static ImageService imageService = new ImageService();

	public static ImageService getInstance() {
		return imageService;
	}

	/**
	 * 将图片放进缓存,图片名称是由URL去掉一些符号得到
	 * 
	 * @param url
	 *            地址
	 * @param bitmap
	 *            图片
	 */
	public void putImageCache(String url, Bitmap bitmap) {
		try {
			if (!ImageCache.getInstance().isBitmapExit(url))
				;
			ImageCache.getInstance().put(url, bitmap, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param url图片URL
	 * 
	 * @param imageView
	 *            imageView
	 * 
	 * @param defaultBitmap
	 *            获取图片失败这显示默认图片
	 */
	public void setBitmapToView(String imageUrl, final ImageView imageView, final Bitmap defaultBitmap,
			boolean isCacheToLocal) {
		
		if (imageUrl == null || imageUrl.equals("")) {
			return ;
		}
	}

	
	private final Bitmap defaultPlaceholderBitmap = BitmapFactory.decodeFile("file:///android_asset/icon.png");
	private final Bitmap defaultTapToLoadBitmap = BitmapFactory.decodeResource(HXLApplication
			.getInstance().getResources(), R.drawable.base_imageholder);
	
	public final static int IMG_TYPE_PLACEHOLDER = 0;	// 占位图
	public final static int IMG_TYPE_TAP_TO_LOAD = 1;	// 点击加载图
	public final static int IMG_TYPE_LOCAL = 2;			// 本地图
	public final static int IMG_TYPE_NETWORK = 3;		// 网络图
	public final static int IMG_TYPE_TAP_RELOAD = 4;	// 点击重新加载，在从网络获取图片失败时显示，目前还没有使用
	
	/* 将返回值类型从Bitmap修改为void，统一以ImageCallback作为函数出口，这样能避免两个出口带来的混乱
	 * ImageCallback回调imageLoad方法中增加一个参数imageType来区分返回图片的类型。
	 * 此外，还将从网络加载的处理独立出来，在loadBitmapFromNetwork中处理
	 * 2013/4/25	zhangyi
	 */
	public void loadBitmap(String imageUrl, ImageView imageView,
			boolean isCacheToLocal, int force, ImageCallback imageCallback) {
		if (imageCallback == null){
			System.out.println("必须提供imageCallback!");
			return;
		}
		if (imageUrl == null || imageUrl.equals("")) {
			System.out.println("必须提供图片地址!");
			return ;
		}
		Bitmap bitmap = null;
		if (ImageCache.getInstance().isBitmapExit(imageUrl)) {	
			// 如果缓存过就从缓存中取出数据
			bitmap = ImageCache.getInstance().get(imageUrl);
			imageCallback.imageLoad(imageView,bitmap,IMG_TYPE_LOCAL);
		} else {	// 从网络上取出数据，并将取出的数据缓存到内存中，根据isCacheToLocal决定是否缓存到sd卡
			// 从网络获取则先使用占位图
			imageCallback.imageLoad(imageView,defaultPlaceholderBitmap,IMG_TYPE_PLACEHOLDER);
			if(!HXLApplication.getInstance().networkIsAvailable()){
				// 网络不可用则一直保留占位图
			}else if(HXLApplication.getInstance().getNetType() == ConnectivityManager.TYPE_MOBILE &&false
					/*HXLApplication.getInstance().disable3GLoadImage()*/){
				switch (force) {
				case GET_IMAGE_FORCE_LOAD:	//强制加载
					loadBitmapFromNetwork(imageUrl,imageView,isCacheToLocal,imageCallback);
					break;
				case GET_IMAGE_TAP_TO_LOAD:	//使用"点击加载"图片
					imageCallback.imageLoad(imageView,defaultTapToLoadBitmap,IMG_TYPE_TAP_TO_LOAD);
					break;
				case GET_IMAGE_TAP_NOT_LOAD://点击不可加载，一直保留占位图
					break;
				}
			}else{
				loadBitmapFromNetwork(imageUrl,imageView,isCacheToLocal,imageCallback);
			}
		}
	}
	// 从网路加载数据，如果确定需要从网络获取，可以直接调这个函数
	public void loadBitmapFromNetwork(final String imageUrl,final ImageView imageView,
			final boolean isCacheToLocal, final ImageCallback imageCallback) {
		if (imageCallback == null){
			System.out.println("必须提供imageCallback!");
			return;
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				imageCallback.imageLoad(imageView, (Bitmap) msg.obj, IMG_TYPE_NETWORK);
			}
		};
		executorService.submit(new Runnable() {
			public void run() {
				Bitmap bitmap = getNetBitmapByURL(imageUrl);
				if (bitmap != null) {
					ImageCache.getInstance().put(imageUrl, bitmap, isCacheToLocal);
				}
				handler.obtainMessage(0, bitmap).sendToTarget();
			}
		});
	}
	// 网络获取图片
	public Bitmap getNetBitmapByURL(String urlString) {
		URL url = null;
		InputStream inputStream = null;
		HttpURLConnection urlConnection = null;
		Bitmap bmp = null;
		String baseUrlString = "";
		try {
			if (urlString.startsWith("http:")) {
				url = new URL(urlString);
			} else {
				url = new URL(baseUrlString + urlString.substring(1, urlString.length()));
			}
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(10000);
			inputStream = urlConnection.getInputStream();
			bmp = BitmapFactory.decodeStream(inputStream);
			if (bmp == null) {
				System.out.println("BitmapFactory.decodeByteArray 返回null");
			}

			if (!(TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED)) {
				double receiveTotal = TrafficStats.getTotalRxBytes() / 1024.0;
				imageIndex++;
			}
		} catch (MalformedURLException e) {
			bmp = null;
			System.out.println("getNetBitmapByURL URL格式错误");
		} catch (IOException e) {
			bmp = null;
			System.out.println("getNetBitmapByURL IO异常");
		} catch (Exception e) {
			bmp = null;
			System.out.println("getNetBitmapByURL 未知异常");
		}finally {
			if (null != inputStream) {
				try {
					inputStream.close();
					inputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != urlConnection) {
				urlConnection.disconnect();
				urlConnection = null;
			}
		}
		return bmp;
	}

	// 数据流
	private byte[] getBytesFromStream(InputStream inputStream) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		while (len != -1) {
			try {
				len = inputStream.read(b);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (len != -1) {
				baos.write(b, 0, len);
			}
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return baos.toByteArray();
	}

	// 对外界开放的回调接口
	public interface ImageCallback {
		// 注意 此方法是用来设置目标对象的图像资源
		public void imageLoad(ImageView imageView, Bitmap bitmap,int imageType );
	}

}
