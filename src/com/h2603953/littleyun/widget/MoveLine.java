package com.h2603953.littleyun.widget;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.util.ThemeManager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class MoveLine extends View implements Skin{
	private Context context;
	private float startX=0,controlY=48,endX;
	private Paint mPaint;
	private Path mPath;
	private int themeColor,tabColor;
	private Shader mShader;
	private int height = 48;
	public MoveLine(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	public MoveLine(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public MoveLine(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub	
		this.context = context;
		init();
	}
	private void init(){
		//themeColor = ThemeManager.getCurrentColor(context);
		themeColor = Color.parseColor("#324762");
		tabColor = getResources().getColor(R.color.TabBgColor);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);		
		mPath = new Path();		
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		mPath.reset();
		mShader = new LinearGradient(startX, 0, startX, controlY/2, tabColor, themeColor, Shader.TileMode.CLAMP);
		mPaint.setShader(mShader);
		mPath.moveTo(startX, 0);
		mPath.quadTo((endX-startX)/2+startX, controlY, endX, 0);
		canvas.drawPath(mPath, mPaint);		
	}
	public void setPosition(float startX,float endX,float speed){
		this.startX = startX;
		this.endX = endX;
		this.controlY = height*speed;
		invalidate();
	}
	@Override
	public void changeSkin(int colorId) {
		// TODO Auto-generated method stub
		this.themeColor = colorId;
		invalidate();
	}
}
