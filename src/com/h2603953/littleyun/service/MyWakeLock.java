package com.h2603953.littleyun.service;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class MyWakeLock{
	
	private WakeLock mWakeLock;
	private Context context;
	public MyWakeLock(Context context){
		this.context = context;
	}
	public void acquireWakeLock(){
		if(mWakeLock == null){
			PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
		}else{
			mWakeLock.acquire();
		}
	}
	public void releaseWakeLock(){
		if(mWakeLock!=null){
			mWakeLock.release();
		}
	}

}
