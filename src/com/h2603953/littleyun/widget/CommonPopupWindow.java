package com.h2603953.littleyun.widget;

import com.h2603953.littleyun.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class CommonPopupWindow extends PopupWindow{
	
	private Builder mBuilder;
	private Context context; 
	private Window mWindow;
	private int layoutId;
	private int width;
	private int height;
	private float level;
	private Drawable drawable;
	private int animaStyle;
	private boolean touch;
	private View popupView;
	private OnDismissListener mOnDismissListener;
	
	private CommonPopupWindow(Builder builder){
		this.mBuilder = builder;
		this.context = builder.context;
		this.layoutId = builder.layoutId;
		this.width = builder.width;
		this.height = builder.height;
		this.level = builder.level;
		this.drawable = builder.drawable;
		this.animaStyle = builder.animaStyle;
		this.touch = builder.touch;
		this.mOnDismissListener = builder.mOnDismissListener;
		Log.i("CommonPopupWindow", drawable+"");
		initPopuWindow();
		setBackGroundLevel(level);
		setAnimationStyle(animaStyle);
		setOutSideTouch(touch);
	}
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		WindowManager.LayoutParams params = mWindow.getAttributes();
		params.alpha = 1.0f;
		mWindow.setAttributes(params);	
		if(mOnDismissListener != null){
			mOnDismissListener.onDismiss();
		}
	}
	private void initPopuWindow(){
		popupView = LayoutInflater.from(context).inflate(layoutId, null);
		setContentView(popupView);
		setWidth(width);
		setHeight(height);
		setSoftInputMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	private void setBackGroundLevel(float level){
		mWindow = ((Activity)context).getWindow();
		WindowManager.LayoutParams params = mWindow.getAttributes();
		params.alpha = level;
		mWindow.setAttributes(params);		
	}
	private void setOutSideTouch(boolean touch){
		setBackgroundDrawable(drawable);
		Log.i("outsidetouch", drawable+"");
		Log.i("touch", touch+"");
		setOutsideTouchable(touch);
		setFocusable(touch);
	}
	public void show(int position){
		showAtLocation(((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content), position, 0, 0);
	}
	
	public static class Builder{
		private int layoutId;
		
		private int width;
		private int height = ViewGroup.LayoutParams.MATCH_PARENT;
		private Context context;
		private Drawable drawable = new ColorDrawable(Color.TRANSPARENT);
		
		private float level = 0.7f;
		private int animaStyle =R.style.PopuDefaultFade;
		private boolean touch = true;
		private OnDismissListener mOnDismissListener;
		
		public Builder(Context context){
			this.context = context;
			WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
			width = (wm.getDefaultDisplay().getWidth())*2/3;
		}
		public Builder setView(int layoutId){
			this.layoutId = layoutId;
			return this;
		}
		public Builder setWidth(int width){
			this.width = width;
			return this;
		}
		public Builder setHeight(int height){
			this.height = height;
			return this;
		}
		public Builder setBackground(Drawable drawable){
			this.drawable = drawable;
			return this;
		}
		public Builder setBackGroundLevel(float level){
			this.level = level;
			return this;
		}
		public Builder setAnimation(int animaStyle){
			this.animaStyle= animaStyle;
			return this;
		}
		public Builder setOutsideTouchable(boolean touch){
			this.touch = touch;
			return this;
		}
		public Builder setOnDismissListener(OnDismissListener mOnDismissListener){
			this.mOnDismissListener = mOnDismissListener;
			return this;
		}
		public CommonPopupWindow build(){
			return new CommonPopupWindow(this);
		}
		
	}
	public interface OnDismissListener{
		public void onDismiss();
	}

}
