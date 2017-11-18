package com.h2603953.littleyun.widget;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.animator.BaseAnimator;
import com.h2603953.littleyun.util.ThemeManager;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.animation.Interpolator;

public class TriangleView extends View{
	public static final int DRAG_LEFT = 0;
	public static final int DRAG_RIGHT = 1;
	public static final int DRAG_DOWN = 2;
	private int dragFlag = DRAG_DOWN;
	
	private Context context;
	private Paint mPaint;
	private Path mPath;
	private int themeColor,defaultwidth,defaultheight,width,height,stroke;
	private float drag,changeDrag;
	private DragAnimator animator;
	
	public void setDragAnimator(float drag){
		this.drag = drag;
		dragFlag = DRAG_DOWN;
		animator = new DragAnimator(this, 0, drag);
	}
	public void setAnimationListener(AnimatorListenerAdapter listener){
		animator.setAnimationListener(listener);
	}
	public void startAnimator(long duration){
		animator.start(duration);
	}
	public void setInterpolator(Interpolator value){
		animator.setInterpolator(value);
	}
	public void cancel(){
		animator.cancel();
	}
	public TriangleView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	public TriangleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub

	}
	public TriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		this.context = context;
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TriangleView);
		stroke = array.getDimensionPixelSize(R.styleable.TriangleView_strokewidth,20);	
		array.recycle();
		init();
	}
	private void init(){
		themeColor = ThemeManager.getCurrentColor(context);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(themeColor);
		mPath = new Path();		
		matrix = new Matrix();
        matrix.preScale(-1, 1);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        defaultwidth = wm.getDefaultDisplay().getWidth();
        defaultheight = wm.getDefaultDisplay().getHeight()*2/3;
	}
	private Matrix matrix;
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.translate(defaultwidth/2, 0);
			switch(dragFlag){
			case DRAG_DOWN:				
				for(int i = 0;i<3;i++){
					mPaint.setAlpha(100+50*i);
					mPath.reset();
					mPath.moveTo(0,0+stroke*i);
					mPath.quadTo(-width/2+stroke*i, 0+stroke*i, -width/2+stroke*i, height/3);
//					mPath.cubicTo(-width/3+stroke*i, 0+stroke*i, -width/2+stroke*i, height/3+stroke*i,-width/2+stroke*i,height/2);
					mPath.cubicTo(-width/2+stroke*i, height/2-stroke*i, -width/6+stroke*i, height+changeDrag-stroke*i, 0, height+changeDrag-stroke*i);
					canvas.drawPath(mPath, mPaint);
					mPath.transform(matrix);
					canvas.drawPath(mPath, mPaint);
				}
				break;
			case DRAG_LEFT:
//				drawDependPager(canvas);
				break;
			case DRAG_RIGHT:
				break;
			}
										
	}
//	private void drawDependPager(Canvas canvas){
//		for(int i = 0;i<3;i++){
//			mPaint.setAlpha(100+50*i);
//			mPath.reset();
//			mPath.moveTo(0,0+stroke*i);
//			mPath.quadTo(-width/2+stroke*i-changeDrag, 0+stroke*i, -width/2+stroke*i-changeDrag, height/3);
//			mPath.cubicTo(-width/2+stroke*i-changeDrag, height/2-stroke*i-changeDrag/3, -width/6+stroke*i-changeDrag, height-stroke*i-changeDrag/3, 0, height-stroke*i-changeDrag/3);
//			mPath.cubicTo(width/6-stroke*i, height-stroke*i-changeDrag/3,width/2-stroke*i, height/2-stroke*i, width/2-stroke*i, height/3);
//			mPath.quadTo(width/2-stroke*i, 0+stroke*i, 0, 0+stroke*i);
//			if(dragFlag == DRAG_LEFT){
//				canvas.drawPath(mPath, mPaint);
//			}else{
//				mPath.transform(matrix);
//				canvas.drawPath(mPath, mPaint);
//			}
//			
//		}
//	}
	private int value;
	public void movePager(int value,int drag){
		this.value = value;
		this.dragFlag = drag;
	}
    private int measureSize(int specType, int contentSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = Math.min(contentSize, specSize);
        } else {
            result = contentSize;
            if (specType == 1) {
                result += (getPaddingLeft() + getPaddingRight());
            } else {
                result += (getPaddingTop() + getPaddingBottom());
            }
        }
        return result;
    }
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int lastwidth = measureSize(1, defaultwidth, widthMeasureSpec);
        int lastheight = measureSize(1, defaultheight, heightMeasureSpec);
        setMeasuredDimension(lastwidth, lastheight);
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		width = w/2;
		height = h/3;
		
	}
	class DragAnimator extends BaseAnimator{

		public DragAnimator(View target, float startValue, float endValue) {
			super(target, startValue, endValue);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void doAnim(float animatedValue) {
			// TODO Auto-generated method stub
			changeDrag = animatedValue;
			dragFlag = DRAG_LEFT;
			TriangleView.this.invalidate();
		}

	}


}
