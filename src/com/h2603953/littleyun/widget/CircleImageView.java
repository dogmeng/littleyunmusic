package com.h2603953.littleyun.widget;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.util.DrawableUtil;
import com.h2603953.littleyun.util.ThemeManager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class CircleImageView extends ImageView implements Skin{
	private Paint mPaint,mStrokePaint,mTextPaint,mBitmapPaint;
	private int mStrokeWidth,mStrokeColor,defaultColor,mBitmapHeight,mBitmapWidth,mContentView,alpha,padding,textSize;
	private float mStrokeRadius,mDrawableRadius,mRoundRadius;				
	private Bitmap mBitmap;
	private ScaleType defaultType = ScaleType.CENTER_CROP;
	private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
	private BitmapShader shader;
	private RectF mStrokeRect;
	private Matrix mShaderMatrix;
	private ColorFilter colorFilter;
	private boolean isRoundImageView = false,isColorFilter = true;
	private String text = null;
	private Rect textRounds;

	public CircleImageView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	public CircleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		defaultColor = ThemeManager.getCurrentColor(context);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);
		mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_stroke_width, 0);
		mStrokeColor = typedArray.getColor(R.styleable.CircleImageView_stroke_color, defaultColor);
		isRoundImageView = typedArray.getBoolean(R.styleable.CircleImageView_roundrect_imageview, false);
		isColorFilter = typedArray.getBoolean(R.styleable.CircleImageView_iscolorfilter, true);
		mRoundRadius = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_round_radius, 2);
		alpha = typedArray.getInteger(R.styleable.CircleImageView_alpha, 80);
		mContentView = typedArray.getResourceId(R.styleable.CircleImageView_content_imageview, 0);
		text = typedArray.getString(R.styleable.CircleImageView_text);
		padding = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_padding, 0);
		textSize = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_text_size,0);
		typedArray.recycle();
		init();
	}
	private void init(){
		super.setScaleType(defaultType);
		if(mBitmap == null){
			if(mContentView!=0){
				Drawable drawable = getResources().getDrawable(mContentView);
				mBitmap = getBitmapFromDrawable(drawable);
			}			
			if(mBitmap == null)return;
		}
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);		
		shader = new BitmapShader(mBitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);		
		mPaint.setShader(shader);
		
		mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mStrokePaint.setStyle(Paint.Style.STROKE);
		mStrokePaint.setColor(mStrokeColor);
		mStrokePaint.setStrokeWidth(mStrokeWidth);
		mStrokePaint.setAlpha(alpha);
		
		mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		if(isColorFilter){
			mBitmapPaint.setColorFilter(new PorterDuffColorFilter(mStrokeColor, PorterDuff.Mode.SRC_IN));
		}

	}
	private void intSize(){
		mBitmapHeight = mBitmap.getHeight();
		mBitmapWidth = mBitmap.getWidth();		
		mStrokeRect = new RectF();		
		if(text!=null){
			mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mTextPaint.setColor(mStrokeColor);
			mTextPaint.setTextSize(textSize);
			textRounds = new Rect();
			mTextPaint.getTextBounds(text, 0, text.length(), textRounds);
			mStrokeRect.set(mStrokeWidth, mStrokeWidth, getWidth()-mStrokeWidth, getHeight() - padding - textRounds.height()-mStrokeWidth);	
		}else{
			mStrokeRect.set(mStrokeWidth,mStrokeWidth,getWidth()-mStrokeWidth,getHeight()-mStrokeWidth);
		}		
		mStrokeRadius = Math.min(mStrokeRect.height()/2,mStrokeRect.width()/2);
		mStrokeRadius = Math.abs(mStrokeRadius);
		
		updateShaderMatrix();
	}
	
	private void updateShaderMatrix(){
		float scale;
		float dx=0,dy=0;
		mShaderMatrix = new Matrix();
		mShaderMatrix.set(null);
		if(mBitmapWidth*mStrokeRect.height()>mStrokeRect.width()*mBitmapHeight){
			scale = mStrokeRect.height()/(float)mBitmapHeight;
			dx = (mStrokeRect.width()-mBitmapWidth*scale)*0.5f;
		}else{
			scale = mStrokeRect.width()/(float)mBitmapWidth;
			dy = (mStrokeRect.height()-mBitmapHeight*scale)*0.5f;
		}
		mShaderMatrix.setScale(scale, scale);
		mShaderMatrix.postTranslate((int)(dx+0.5f)+mStrokeWidth, (int)(dy+0.5f)+mStrokeWidth);
		shader.setLocalMatrix(mShaderMatrix);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//super.onDraw(canvas);
		if(mBitmap == null) return;
		intSize();
		if(text != null){
			if(textRounds.width()>getWidth()){
				canvas.drawText(text,0,getHeight()-mStrokeWidth-10,mTextPaint);
			}else{
				canvas.drawText(text,(getWidth()-textRounds.width())/2,getHeight()-mStrokeWidth-10,mTextPaint);
			}			
		}
		if(isRoundImageView){			
			canvas.drawRoundRect(mStrokeRect, mRoundRadius, mRoundRadius, mStrokePaint);
			if(isColorFilter){
				canvas.drawBitmap(mBitmap, (mStrokeRect.width()-mBitmapWidth)/2+mStrokeWidth, (mStrokeRect.height()-mBitmapHeight)/2+mStrokeWidth, mBitmapPaint);
			}else{
				Rect rect = new Rect();
				mStrokeRect.round(rect);
				canvas.drawBitmap(mBitmap, rect, rect, mBitmapPaint);
			}			
		}else{
			if(mStrokeWidth != 0){				
				canvas.drawCircle(getWidth()/2,(mStrokeRect.height()+2*mStrokeWidth)/2, mStrokeRadius, mStrokePaint);
			}
			canvas.drawCircle(getWidth()/2, (mStrokeRect.height()+2*mStrokeWidth)/2, mStrokeRadius, mPaint);
		}		
		
	}
	public int getStrokeColor(){
		return mStrokeColor;
	}
	public void setStrokeColor(int color){
		if(mStrokeColor == color)return;
		mStrokeColor = color;
		init();
		invalidate();
	}
	public int getStrokeWidth(){
		return mStrokeWidth;
	}
	public void setStrokeWidth(int width){
		if(mStrokeWidth == width)return;
		mStrokeWidth = width;
		init();
		invalidate();
	}
	public void setText(String text){
		this.text = text;		
		invalidate();
	}
	public String getText(){
		return text;
	}
	public void setIsColorFilter(boolean isColorFilter){
		this.isColorFilter = isColorFilter;
		init();
		invalidate();
	}
	public boolean getIsColorFilter(){
		return isColorFilter;
	}
	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		super.setColorFilter(cf);
		colorFilter = cf;
		mPaint.setColorFilter(colorFilter);
	}
	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
		mBitmap = bm;
		init();
	}
	@Override
	public void setImageDrawable(Drawable drawable) {
		// TODO Auto-generated method stub
		super.setImageDrawable(drawable);
		mBitmap = getBitmapFromDrawable(drawable);
		init();
	}
	@Override
	public void setImageResource(int resId) {
		// TODO Auto-generated method stub
		super.setImageResource(resId);
		mBitmap = getBitmapFromDrawable(getDrawable());
		init();
	}
	@Override
	public void setImageURI(Uri uri) {
		// TODO Auto-generated method stub
		super.setImageURI(uri);
		mBitmap = getBitmapFromDrawable(getDrawable());
		init();
	}
	
	private Bitmap getBitmapFromDrawable(Drawable drawable){		
		if(drawable == null)return null;
//			drawable = DrawableUtil.tintDrawable(drawable, getResources().getColor(R.color.toolbarselectedColor));

		if(drawable instanceof BitmapDrawable){
			return ((BitmapDrawable)drawable).getBitmap();
		}
		Bitmap bitmap;
		if(drawable instanceof ColorDrawable){
			bitmap = Bitmap.createBitmap(2,2,bitmapConfig);			
		}else{
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),bitmapConfig);
		}
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
		}
	@Override
	public void changeSkin(int colorId) {
		// TODO Auto-generated method stub
		
	}	

}
