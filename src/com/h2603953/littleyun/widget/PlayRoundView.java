package com.h2603953.littleyun.widget;

import java.util.ArrayList;
import java.util.Random;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.animator.BaseAnimator;
import com.h2603953.littleyun.util.ThemeManager;
import com.h2603953.littleyun.widget.TriangleView.DragAnimator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.animation.Interpolator;

public class PlayRoundView extends View{
	private int themeColor,defaultwidth,defaultheight,width,height,stroke;
	private Context context;
	private Paint mPaint,starPaint;
	private Path mPath;
	private final float bezFactor = 0.551915024494f;
	private PointF p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11;
    private int mRadius;
    private float rotate,move,drag;
    private Random random = new Random();
    private DragAnimator animator;
    private Xfermode clearXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private boolean showStar = false;
    private RectF starRect;
    private ArrayList<PointF> starList = new ArrayList<>();

	public PlayRoundView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	public PlayRoundView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	public PlayRoundView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		this.context = context;
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TriangleView);
		stroke = array.getDimensionPixelSize(R.styleable.TriangleView_strokewidth,0);
		Log.i("stroke", stroke+"");
		array.recycle();
		init();
	}
	private void init(){
		themeColor = ThemeManager.getCurrentColor(context);
		mPaint = new Paint();
		starPaint = new Paint();
		starPaint.setAntiAlias(true);
		starPaint.setStrokeWidth(10);
		starPaint.setStrokeCap(Cap.ROUND);
		starPaint.setColor(themeColor);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(themeColor);
		mPath = new Path();		
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        defaultwidth = wm.getDefaultDisplay().getWidth();
        defaultheight = wm.getDefaultDisplay().getHeight()*2/3;

        mRadius = defaultwidth/6;
        Log.i("mRadius", mRadius+"");
        p5 = new PointF(mRadius * bezFactor,-mRadius);
        p6 = new PointF(0, -mRadius);
        p7 = new PointF(-mRadius * bezFactor, -mRadius);
        
        p0 = new PointF(0, mRadius);
        p1 = new PointF(mRadius * bezFactor, mRadius);
        p11 = new PointF(-mRadius * bezFactor, mRadius);

        p2 = new PointF(mRadius, mRadius * bezFactor);
        p3 = new PointF(mRadius, 0);
        p4 = new PointF(mRadius, -mRadius * bezFactor);

        p8 = new PointF(-mRadius, -mRadius * bezFactor);
        p9 = new PointF(-mRadius, 0);
        p10 = new PointF(-mRadius, mRadius * bezFactor);        
        starRect = new RectF(mRadius, -mRadius, defaultwidth/4, mRadius);
		for(int i = 0;i<16;i++){
			starList.add(new PointF());
		}
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
        int lastheight = measureSize(2, defaultheight, heightMeasureSpec);
        setMeasuredDimension(lastwidth, lastheight);
	}
	public void setStartList(){
		for(int i=0;i<starList.size();i++){
			starList.get(i).x = mRadius+random.nextInt((int) (2*drag));
			starList.get(i).y = random.nextInt((int)(2*mRadius))-mRadius;
		}
		
	}
	public boolean finishDraw = false;
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);		
		canvas.translate(defaultwidth/2, width/2+defaultwidth/8);
		canvas.save();
		canvas.rotate(rotate);
    	if(showStar){
    		for(int i = 0;i<starList.size()/2;i++){
    			starPaint.setAlpha((int) (255*move/drag));
    			canvas.drawPoint(starList.get(i++).x, starList.get(i++).y, starPaint);
    			starPaint.setAlpha((int) (150*move/drag));
    			canvas.drawPoint(starList.get(i).x, starList.get(i).y, starPaint); 
    		}
    	}
		bounce2RightRound(canvas);
		canvas.restore();
	}
    private void bounce2RightRound(Canvas canvas) {
    	for(int i = 0;i<3;i++){   		
    		mPaint.setAlpha(100+50*i);
    		mPath.reset();
        	mPath.moveTo(p0.x, p0.y-stroke*i);
        	mPath.cubicTo(p1.x-stroke*i, p1.y-stroke*i, p2.x-stroke*i+move, p2.y-stroke*i, p3.x -stroke*i+move, p3.y);
        	mPath.cubicTo(p4.x-stroke*i+move, p4.y+stroke*i, p5.x-stroke*i, p5.y +stroke*i, p6.x, p6.y +stroke*i);
        	mPath.cubicTo(p7.x+stroke*i, p7.y+stroke*i, p8.x+stroke*i, p8.y+stroke*i, p9.x+stroke*i, p9.y);
        	mPath.cubicTo(p10.x+stroke*i, p10.y-stroke*i, p11.x+stroke*i, p11.y-stroke*i, p0.x, p0.y-stroke*i);
        	mPath.close();
        	canvas.drawPath(mPath, mPaint);
    	}
    }
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		width = w/2;
		height = h/2;		
       
	}

	public void setDragAnimator(float drag){
		this.drag = drag;		
		animator = new DragAnimator(this, 0, drag,0);
	}
	public void startAnimator(long duration){
		//showStar = true;
		rotate = 0;
		animator.start(duration);
	}
	public void setInterpolator(Interpolator value){
		animator.setInterpolator(value);
	}
	public boolean isAnimatorPlaying(){
		if(animator == null){
			return false;
		}
		return animator.isPlaying();
	}
	public void setAnimatorListener(AnimatorListenerAdapter listener){
		animator.setAnimationListener(listener);
	}
	public void cancel(){
		showStar = false;
		if(animator!=null)
		animator.cancel();
		animator =null;
		rotate = 0;
		move = 0;
		invalidate();
	}
	public void end(){
		showStar = false;
		animator.end();
	}
	public void updateRotate(){
		rotate = random.nextInt(360);;
	}
	class DragAnimator extends BaseAnimator{
		private float lastValue = 0;
		private int count = 0;
		public DragAnimator(View target, float startValue, float endValue,float thirdValue) {
			super(target, startValue, endValue,thirdValue);
			// TODO Auto-generated constructor stub
			Log.i("DragAnimator创建",""+(count++));
		}

		@Override
		protected void doAnim(float animatedValue) {
			// TODO Auto-generated method stub
			move = animatedValue;
			if(move-lastValue<0){
				showStar = true;
			}else{
				showStar = false;
			}
			lastValue = move;
			PlayRoundView.this.invalidate();
		}

	}
	

}
