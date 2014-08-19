package com.jiaodong.hxl.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class AdvGallery extends Gallery {
	
	OnSelectedChangedListener listener;
	int position;
	
	public AdvGallery(Context context) {
		super(context);
	}

	public AdvGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnSelectedChangedListener(OnSelectedChangedListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		//解决Gallery拖拽滑动过快
		if(e1 != null && e2 != null) {//有些手机e1会为空，不解。。先加判断防止
			int keyCode; 
			position = this.getSelectedItemPosition();
			if (e2.getX() > e1.getX()) {  
				if(position >= 0) {
					position--;
				}
				keyCode = KeyEvent.KEYCODE_DPAD_LEFT; 
			} else {
				if(position <= this.getCount() - 1) {
					position++;
				}
				keyCode = KeyEvent.KEYCODE_DPAD_RIGHT; 
			} 
			if(listener != null) {
				listener.onChange(position);
			}
			onKeyDown(keyCode, null); 
		}
	    return true; 
	}
	
	@Override
	public void setUnselectedAlpha(float unselectedAlpha) {
		unselectedAlpha = 1.0f;
		super.setUnselectedAlpha(unselectedAlpha);
	}
	
	/**
	 * 监听下一个即将显示的view的position
	 * position=-1   表示已经到了第一张没法再切换了
	 * position=this.getCount() 表示已经到了最后一张没法再切换了
	 * @author 刘斌
	 * @param nextPosition 下一个即将显示的view的位置
	 */
	public interface OnSelectedChangedListener {
		void onChange(int nextPosition);
	}

}
