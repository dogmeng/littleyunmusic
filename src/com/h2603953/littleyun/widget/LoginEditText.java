package com.h2603953.littleyun.widget;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.util.DensityUtil;
import com.h2603953.littleyun.util.ThemeManager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;


public class LoginEditText extends EditText {
	
	private int drawableleftunfocused,drawableleftfocused;
	private Paint paint;
	private int color;
	private Drawable leftUnfocused,leftFocused,right;
	private Context context;
	private MarginLayoutParams lp;
	public LoginEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	public LoginEditText(Context context, AttributeSet attrs) {		
		super(context, attrs);		
		// TODO Auto-generated constructor stub
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LintEdt);
		drawableleftunfocused = array.getResourceId(R.styleable.LintEdt_drawableleftunfocused,0);
		drawableleftfocused = array.getResourceId(R.styleable.LintEdt_drawableleftfocused,0);		
		array.recycle();
		init();
	}

	public LoginEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LintEdt, defStyle, 0);
		drawableleftunfocused = array.getResourceId(R.styleable.LintEdt_drawableleftunfocused,0);
		drawableleftfocused = array.getResourceId(R.styleable.LintEdt_drawableleftfocused,0);		
		array.recycle();
		init();
	}
	protected void init(){		
		paint = new Paint();
		paint.setStrokeWidth(2.0f);
		color = Color.parseColor("#bec4d0");
		if(drawableleftunfocused!=0&&drawableleftfocused!=0){
			leftUnfocused = getResources().getDrawable(drawableleftunfocused);
			leftFocused = getResources().getDrawable(drawableleftfocused);
		}
		
		right = getResources().getDrawable(R.drawable.login_del);
		setDrawable(leftUnfocused);
		
	}
	
	@Override
	protected void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
		// TODO Auto-generated method stub
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		setDrawable(leftFocused);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		paint.setColor(color);
		int x = getScrollX();
		canvas.drawLine(0, getHeight()-2, getWidth()+x, getHeight()-2, paint);
	}
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		// TODO Auto-generated method stub
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		if(focused){
			setDrawable(leftFocused);			
			setCurrentColor(Color.parseColor("#385070"));
		}else{		
			setDrawable(leftUnfocused);
			setCurrentColor(Color.parseColor("#bec4d0"));
		}
	}
	protected void setDrawable(Drawable left){
		if(length()>0){
			if(btn!=null) btn.setEnabled(true);
			setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
		}else{
			if(btn!=null) btn.setEnabled(false);
			setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
		}
	}
	protected void setCurrentColor(int color){
		this.color = color;
		setTextColor(color);
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_UP:
			lp = (MarginLayoutParams)getLayoutParams();
			int getX = (int)event.getX();
			int getY = (int)event.getY();
			Rect rect = new Rect();
			getGlobalVisibleRect(rect);			
			rect.left = rect.right - lp.rightMargin-20;
			rect.bottom = rect.bottom - rect.top+lp.bottomMargin;
			rect.top = 0;	
			
			if(rect.contains(getX+lp.rightMargin, getY)){
				setText("");
			}
		default:
			break;
			}

		return super.onTouchEvent(event);
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}
	private View btn;
	public void setBtnEnable(View btn){
		this.btn = btn;
	}

}