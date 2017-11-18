package com.h2603953.littleyun.application;
import com.h2603953.littleyun.util.SharePreferenceManager;
import com.h2603953.littleyun.util.ToastUtil;

import android.app.Application;
import android.content.Context;


public class MyApplication extends Application{
	public static Context context;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;
		SharePreferenceManager.getInstance(context);
		ToastUtil.init(context);
	}
	

}
