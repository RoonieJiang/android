package com.jiaodong.hxl.ImageService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.WeakHashMap;

import com.jiaodong.hxl.HXLApplication;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

/**
 * 将网络上的图片加载到本地临时文件夹里
 * @author 陈鹏
 *
 */
public class ImageCache extends WeakHashMap<String, Bitmap> {

	private static ImageCache mNetImageViewCache = new ImageCache();
	
	

	private ImageCache() {

	}

	public static ImageCache getInstance() {
		return mNetImageViewCache;
	}
	
	/**
	 * 获取图片
	 */
	@Override
	public Bitmap get(Object key) {
		isBitmapExit((String) key);
		return super.get(key);
	}

	/**
	 * 判断图片是否存在
	 * 首先判断内存中是否存在
	 * 然后判断本地是否存在,若存在，则缓存到内存中
	 * 
	 * @param url
	 * @return
	 */
	public boolean isBitmapExit(String url) {
		boolean isExit = containsKey(url);
		if (false == isExit) {
			isExit = isLocalHasBmp(url);
			if(isExit){
				String name = changeUrlToName(url);
				String filePath = isCacheFileIsExit();
				File file = new File(filePath, name);
				cacheBmpToMemory(file, url);
			}
		}
		return isExit;
	}

	/*
	 * 判断本地有没有
	 */
	public boolean isLocalHasBmp(String url) {
		String name = changeUrlToName(url);
		String filePath = isCacheFileIsExit();
		File file = new File(filePath, name);
		return file.exists();
	}

	/*
	 * 将本地图片缓存到内存中
	 */
	private boolean cacheBmpToMemory(File file, String url) {
		boolean sucessed = true;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			sucessed = false;
		}
		byte[] bs = getBytesFromStream(inputStream);
		Bitmap bitmap = BitmapFactory.decodeByteArray(bs, 0, bs.length);
		if (bitmap == null) {
			return false;
		}
		this.put(url, bitmap, false);
		return sucessed;
	}

	private byte[] getBytesFromStream(InputStream inputStream) {
		boolean b2 = true;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		while (len != -1 && b2) {
			try {
				len = inputStream.read(b);
				if (len != -1) {
					baos.write(b, 0, len);
				}
			} catch (IOException e) {
				b2 = false;
				try {
					inputStream.close();
				} catch (IOException e1) {
				}
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

	/*
	 * 判断缓存文件夹是否存在如果存在怎返回文件夹路径，如果不存在新建文件夹并返回文件夹路径
	 */
	private String isCacheFileIsExit() {
		String filePath = HXLApplication.getInstance().getTmpImagePath();
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return filePath;
	}
	
	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	public String changeToLocalName(String url) {
		String local_url = HXLApplication.getInstance().getTmpImagePath()
				+ File.separator
				+ ImageCache.getInstance().changeUrlToName(url);
		return local_url;
	}

	/*
	 * 将url变成图片的地址
	 */
	public String changeUrlToName(String url) {
		String name = url.replaceAll(":", "_");
		name = name.replaceAll("//", "_");
		name = name.replaceAll("/", "_");
		name = name.replaceAll("=", "_");
		name = name.replaceAll(",", "_");
		name = name.replaceAll("&", "_");
		name = name + "jiaodong_img";
		return name;
	}
	@Override
	public Bitmap remove(Object key) {
		if(key != null) {
			Bitmap bm = get(key);
			if(bm != null && !bm.isRecycled()) {
				bm.recycle();
			}
		}
		return super.remove(key);
	}
	
	/**
	 * 将图片放入文件中
	 */
	public Bitmap put(String key, Bitmap value) {
		String filePath = isCacheFileIsExit();
		String name = changeUrlToName(key);
		File file = new File(filePath, name);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			value.compress(CompressFormat.JPEG, 100, outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			if (null != outputStream)
				outputStream.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			if (null != outputStream)
				outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (null != outputStream) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outputStream = null;
		}

		return super.put(key, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @param isCacheToLocal
	 *            是否缓存到本地
	 * @return
	 */
	public Bitmap put(String key, Bitmap value, boolean isCacheToLocal) {
		if (isCacheToLocal) {
			return this.put(key, value);
		} else {
			return super.put(key, value);
		}
	}
}
