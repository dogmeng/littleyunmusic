package com.h2603953.littleyun.animator;

import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public abstract class BaseAnimator {
	public ValueAnimator animator;
	protected View target;
	public BaseAnimator(View target,float startValue,float endValue,float thirdValue){
		this.target = target;
		animator = ValueAnimator.ofFloat(startValue,endValue,thirdValue);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				float animatedValue = (Float) animator.getAnimatedValue();
				doAnim(animatedValue);
			}
		});
	}
	public BaseAnimator(View target,float startValue,float endValue){
		this.target = target;
		animator = ValueAnimator.ofFloat(startValue,endValue);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				float animatedValue = (Float) animator.getAnimatedValue();
				doAnim(animatedValue);
			}
		});
	}
	
	public void start(long duration){
		animator.setDuration(duration);
		animator.setRepeatCount(ValueAnimator.INFINITE);
		Log.i("ValueAnimator", ValueAnimator.INFINITE+"");
		animator.setRepeatMode(ValueAnimator.RESTART);
		animator.start();
	}
	
	
	public void setAnimationListener(AnimatorListenerAdapter listener){
		animator.addListener(listener);
	}
	
	public void setInterpolator(Interpolator value){
		animator.setInterpolator(value);
	}
	
	public void cancel(){
			animator.setRepeatCount(0);
			animator.cancel();		
	}
	public void end(){
		animator.end();
	}
	public boolean isPlaying(){
		return animator.isRunning();
	}
	
	protected abstract void doAnim(float animatedValue);
	
	
}
