package com.h2603953.littleyun.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

public class StateDrawable extends BitmapDrawable{
	public StateDrawable(Resources arg0,Bitmap arg1){
		super(arg0, arg1);
	}
	public StateDrawable(Bitmap arg1){
		super(arg1);
	}

	@Override
	protected boolean onStateChange(int[] stateSet) {
		// TODO Auto-generated method stub
		boolean ret = super.onStateChange(stateSet);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
			if(ret){
				invalidateSelf();
			}
		}
		return ret;
	}

}
