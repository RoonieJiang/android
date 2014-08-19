package com.jiaodong.hxl.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * 带有右侧drawable的edittext，可以实现右侧drawable的点击事件
 * 需要添加OnRightDrawableClickedListener来监听
 * @author 刘斌
 *
 */
public class EditTextWithIcon extends EditText {
	public Drawable dRight;
	public Drawable dLeft;
	public Drawable dTop;
	public Drawable dBottom;
	public Rect rBounds;
	OnRightDrawableClickedListener rightListener;
	OnLeftDrawableClickedListener leftListener;
	OnTopDrawableClickedListener topListener;
	OnBottomDrawableClickedListener bottomListener;

	public EditTextWithIcon(Context context) {
		super(context);
	}
	public EditTextWithIcon(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }
	public EditTextWithIcon(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }
	
	@Override  
    public void setCompoundDrawables(Drawable left, Drawable top,  
            Drawable right, Drawable bottom) {  
        if(right !=null) {  
            dRight = right;  
        }
        if(left != null) {
        	dLeft = left;
        }
        if(top != null){
        	dTop = top;
        }
        if(bottom != null) {
        	dBottom = bottom;
        }
        super.setCompoundDrawables(left, top, right, bottom);  
    } 
	public OnRightDrawableClickedListener getListener() {
		return rightListener;
	}

	public void setOnRightDrawableClickedListener(OnRightDrawableClickedListener listener) {
		this.rightListener = listener;
	}
	
	public void setOnLeftDrawableClickedListener(OnLeftDrawableClickedListener listener) {
		this.leftListener = listener;
	}
	
	public void setOnTopDrawableClickedListener(OnTopDrawableClickedListener listener) {
		this.topListener = listener;
	}
	
	public void setOnBottomDrawableClickedListener(OnBottomDrawableClickedListener listener) {
		this.bottomListener = listener;
	}
	/*
     * 获取控件的屏幕区域
     */
    public Rect getRectOnScreen(View view){
        Rect rect = new Rect();
        int[] location = new int[2];
        View parent = view;
        if(view.getParent() instanceof View){
            parent = (View)view.getParent();
        }
        parent.getLocationOnScreen(location);
        view.getHitRect(rect);
        rect.offset(location[0], location[1]);
         
        return rect;
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP && (dRight != null || dLeft != null || dTop != null || dBottom != null)) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			int[] location = new int[2];
	        this.getLocationOnScreen(location);
	        Rect viewRect = this.getRectOnScreen(this);
	        x += location[0];
	        y += location[1];
			if(dRight != null) {
				rBounds = dRight.getBounds();
				if (x >= (viewRect.right - rBounds.width())
						&& x <= (viewRect.right)
						&& y >= viewRect.top
						&& y <= viewRect.bottom) {
					if(rightListener != null) {
						rightListener.onClick(this);
					}
					event.setAction(MotionEvent.ACTION_CANCEL);
					return super.onTouchEvent(event);
				}
			}
			if(dLeft != null) {
				rBounds = dLeft.getBounds();
				if (x >= viewRect.left
						&& x <= (viewRect.left + rBounds.width())
						&& y >= viewRect.top
						&& y <= viewRect.bottom) {
					if(leftListener != null) {
						leftListener.onClick(this);
					}
					event.setAction(MotionEvent.ACTION_CANCEL);
					return super.onTouchEvent(event);
				}
			}
			if(dTop != null) {
				rBounds = dTop.getBounds();
				if (x >= viewRect.left
						&& x <= viewRect.right
						&& y >= viewRect.top
						&& y <= (viewRect.top+rBounds.height())) {
					if(topListener != null) {
						topListener.onClick(this);
					}
					event.setAction(MotionEvent.ACTION_CANCEL);
					return super.onTouchEvent(event);
				}
			}
			if(dBottom != null) {
				rBounds = dBottom.getBounds();
				if (x >= viewRect.left
						&& x <= viewRect.right
						&& y >= (viewRect.bottom-rBounds.height())
						&& y <= viewRect.bottom) {
					if(bottomListener != null) {
						bottomListener.onClick(this);
					}
					event.setAction(MotionEvent.ACTION_CANCEL);
					return super.onTouchEvent(event);
				}
			}
		}
		return super.onTouchEvent(event);
	}
	public interface OnTopDrawableClickedListener {
		void onClick(EditTextWithIcon edt);
	}
	public interface OnBottomDrawableClickedListener {
		void onClick(EditTextWithIcon edt);
	}
	public interface OnRightDrawableClickedListener {
		void onClick(EditTextWithIcon edt);
	}
	public interface OnLeftDrawableClickedListener {
		void onClick(EditTextWithIcon edt);
	}
	@Override
	protected void finalize() throws Throwable {
		dRight = null;
		dLeft = null;
		dTop = null;
		dBottom = null;
		rBounds = null;
		super.finalize();
	}
}
