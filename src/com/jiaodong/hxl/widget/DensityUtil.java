package com.jiaodong.hxl.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.jiaodong.hxl.HXLApplication;

public class DensityUtil {    
    
    /**  
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)  
     */    
    public static int dip2px(float dpValue) {    
        final float scale = HXLApplication.getInstance().getResources().getDisplayMetrics().density;    
        return (int) (dpValue * scale + 0.5f);    
    }    
    
    /**  
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp  
     */    
    public static int px2dip( float pxValue) {    
        final float scale = HXLApplication.getInstance().getResources().getDisplayMetrics().density;    
        return (int) (pxValue / scale + 0.5f);    
    }    
    
    /**
     * 获取屏幕宽度、高度
     * @param context
     * @return
     */
    public static int[] screenWidth(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHight = dm.heightPixels;
		return new int[]{screenWidth,screenHight};
	}
    
    public static int screenWidth(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}
    
    public static boolean isTouchInView(MotionEvent event,View view) {
    	Rect rect = new Rect();
		view.getGlobalVisibleRect(rect);
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();
        if(rect.contains(x,y)){
            return true; 
        }
        return false;
    }
} 