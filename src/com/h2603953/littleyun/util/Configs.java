package com.h2603953.littleyun.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class Configs {
	public static void setStatusBar(Activity activity, int color){
		//?ú®4.4‰ª•‰??,?è™?ÉΩÂÆûÁé∞?òæÁ§∫Â?åÈ?êË??
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
			return;
		}
		//5.0‰ª•‰?äÁõ¥?é•‰∏äËâ≤
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			setStatusBarUp21(activity,color);
		}
		//4.4?à∞5.0‰πãÈó¥??àÈ?èÊ?éÂ?çËÆæÁΩÆ‰?‰∏™Á?âÈ?òÁ?Ñview
		else{
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			ViewGroup decorView = (ViewGroup)activity.getWindow().getDecorView();
			int count = decorView.getChildCount();			
			if(count>0 && decorView.getChildAt(count-1) != null){
				decorView.getChildAt(count-1).setBackgroundColor(color);
			}else{
				View statusBarView = new View(activity);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getStatusBarHeight(activity));
				statusBarView.setLayoutParams(params);
				statusBarView.setBackgroundColor(color);
				decorView.addView(statusBarView);
			}
		}						
	}
	@TargetApi(Build.VERSION_CODES.LOLLIPOP) 
	public static void setStatusBarUp21(Activity activity, int color){
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		activity.getWindow().setStatusBarColor(color);
	}
	public static int getStatusBarHeight(Context context){
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
	}

}
