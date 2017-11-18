package com.h2603953.littleyun.widget;

import java.util.ArrayList;
import java.util.List;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.bean.LrcBean;
import com.h2603953.littleyun.util.ThemeManager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.widget.Scroller;

public class LrcView extends View{
	private int themeColor,defaultwidth,defaultheight,width,height;
	private Context context;
	private List<LrcBean> currentLrc;
	private int currentLine,centerLine;
	private Paint mDrawablePaint;
	private TextPaint mContentPaint;
	private List<StaticLayout> staticLayouts;
	private StaticLayout staticLayout;
	private boolean IS_SEEKING = false,isFling = false;
	private static final int MIN_SEEK = 10;
	private int textSize  = 60,totalContentHeight;
	private float offset,y,mOffset;
	private boolean isTop = false;
    private GestureDetector.SimpleOnGestureListener mOnGestureListener;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;

	public LrcView(Context context) {

		this(context, null);
		// TODO Auto-generated constructor stub
	}
	public LrcView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}
	public void setData(List<LrcBean> lrcRows){
		if(lrcRows!=null){
			currentLrc = lrcRows;			
		}
		initStaticLayout();
	}
	private void init(){
		themeColor = ThemeManager.getCurrentColor(context);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        defaultwidth = wm.getDefaultDisplay().getWidth();
        defaultheight = wm.getDefaultDisplay().getHeight()*2/3;
        mContentPaint = new TextPaint();
        
        currentLrc = new ArrayList<>();
        staticLayouts = new ArrayList<>();
        mScroller = new Scroller(context);
        mOnGestureListener = new LrcViewGestureListener();
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);
	}
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		super.computeScroll();
		if(mScroller.computeScrollOffset()){
			offset =Math.abs(mScroller.getCurrY()) ;
			invalidate();
		}
		if(isFling&&mScroller.isFinished()){
			//发送消息隐藏line
		}
	}
	private void initStaticLayout(){
		if(currentLrc!=null){
			int len = currentLrc.size();
			Log.i("initStaticLayout",width+"");
			for(int i = 0;i<len;i++){								
				staticLayouts.add(new StaticLayout(currentLrc.get(i).getContent(), mContentPaint, (int) (defaultwidth*0.8),
						Alignment.ALIGN_CENTER, 1f, 0f, false));
				totalContentHeight +=staticLayouts.get(i).getHeight();
			}			
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
	float m = 0;
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.translate(getPaddingLeft(), height/2);
		if(isLrc()){			
				//画中间的 y为正上移,y为负,下移			
				canvas.save();
				canvas.translate(0, -y);
				mContentPaint.setTextSize((float)(textSize*1.1));	
				mContentPaint.setColor(themeColor);
				staticLayouts.get(centerLine).draw(canvas);
				canvas.restore();			
				mContentPaint.setTextSize(textSize);	
				mContentPaint.setColor(getResources().getColor(R.color.toolbarTextColor));
				//画上面的
				if(centerLine>0){
					int num = centerLine -1;
					int top = 0;
					while(top<height/2&&num>=0){
						canvas.save();
						top += 120+staticLayouts.get(num).getHeight();
						canvas.translate(0, -top-y);
						staticLayouts.get(num--).draw(canvas);
						canvas.restore();
					}					
				}
				//画下面的
				if(centerLine<currentLrc.size()-1){
					Log.i("下面的centerLine", centerLine+"");
					int num1 = centerLine;
					int bottom = 0;
					while(bottom<height/2&&num1<currentLrc.size()-1){
						canvas.save();
						bottom += 120+staticLayouts.get(num1).getHeight();
						canvas.translate(0, bottom-y);
						staticLayouts.get(++num1).draw(canvas);
						canvas.restore();
					}
				}

		}else{
			textSize  = 70;
			mContentPaint.setTextSize(textSize);	
			mContentPaint.setTextAlign(Align.CENTER);
			mContentPaint.setColor(themeColor);
			canvas.drawText("暂无歌词", 0, 0, mContentPaint);
		}
		
	
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		return mGestureDetector.onTouchEvent(event);
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
	}
	private boolean isLrc(){
		return currentLrc!=null&&currentLrc.size()>0;
	}
	private OnClickListener listener;
	public void setOnClickListener(OnClickListener listener){
		this.listener = listener;
	}
    class LrcViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {
    	private boolean isFirst = true;

        @Override
		public boolean onDoubleTap(MotionEvent e) {
			// TODO Auto-generated method stub
        	Log.i("onDoubleTap", "onDoubleTap");
        	if(textSize<70&&textSize>=60){
        		textSize = (int) (textSize*1.2);
        	}else if(textSize>=70){
        		textSize = 60;
        	}else{
        		textSize = 40;
        	}
        	invalidate();
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("onDoubleTapEvent", "onDoubleTapEvent");

			return super.onDoubleTapEvent(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("onSingleTapConfirmed", "onSingleTapConfirmed");
			listener.onClick(LrcView.this);
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("onSingleTapUp", "onSingleTapUp");						
			return super.onSingleTapUp(e);
		}

		@Override
        public final boolean onDown(MotionEvent motionevent) {
        	//取消获取时间;
//        	mScroller.forceFinished(true);
//        	//removeCallbacks(action);
//        	//显示line
//        	invalidate();
			 Log.i("onDown", "motionevent.getY"+motionevent.getY());
            return true;
        }

        @Override
        public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
           Log.i("onFling", "e1.getY"+e1.getY()+" e2.getY"+e2.getY());
           Log.i("onFling", "velocityX"+velocityX+" velocityY"+velocityY);
           mScroller.fling(0, (int) (e2.getY()-e1.getY()),(int) velocityX/20, (int)velocityY/20, 0, (int)velocityX/20, 0, (int)velocityY/20);        	
           return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
           // Log.i(TAG, "LoopViewGestureListener->onScroll");
        	if(isFirst){
        		distanceY = 0;
        		isFirst = false;
        	}
        	if(isLrc()){
        		if(distanceY>0){
        			isTop = true;
            		if(centerLine ==  currentLrc.size()-1){
            			return false;
            		}        			
        		}else if(distanceY<0){
        			isTop = false;
        			if(centerLine == 0){
        				offset = 0;
        				m = 0;
            			return false;
        			}
        		}
           		mOffset += distanceY;      		 
        		offset = Math.abs(mOffset);
    			int x = (int) staticLayouts.get(centerLine).getHeight();			
    			y =  Math.abs(offset-m);
    			if(y -(x+textSize)>=0){    				   				
    				if(isTop){
    					m = offset;
    					centerLine = centerLine+1 >= currentLrc.size()-1 ? currentLrc.size()-1:centerLine+1;
    				}else{
    					m = offset;
    					centerLine = centerLine-1 <= 0 ? 0:centerLine-1;
    				}
    				y = 0; 
    			}else{
    				if(!isTop){
    					y=-y;
    				}
    			}
            	Log.i("onScroll", "distanceY"+distanceY);
            	Log.i("onScroll", "offset"+offset);
    			Log.i("onDraw", "m"+m);
    			Log.i("onDraw", "x"+x);
    			Log.i("onDraw", "y"+y);
    			Log.i("onDraw", "centerLine"+centerLine);
        		invalidate();
        	}        	

            return true;
        }
    }
	

}
