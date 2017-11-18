package com.h2603953.littleyun.activity;

import java.util.ArrayList;
import java.util.List;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.bean.EventBusMsg;
import com.h2603953.littleyun.bean.EventBusStop;
import com.h2603953.littleyun.fragment.PlayBarFragment;
import com.h2603953.littleyun.service.MusicService;
import com.h2603953.littleyun.service.MusicServiceConnection;
import com.h2603953.littleyun.service.PlayerProxy;
import com.h2603953.littleyun.service.UIChangedListenerImpl;
import com.h2603953.littleyun.util.Configs;
import com.h2603953.littleyun.util.PermissionUtil;
import com.h2603953.littleyun.util.SharePreferenceManager;
import com.h2603953.littleyun.util.ThemeManager;

import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

public class BaseActivity extends AppCompatActivity implements UIChangedListenerImpl{	
	private SharePreferenceManager mSharePreference;
	public int mScreenHeight,mScreenWidth;
	public MusicService mMusicService;
	private PlayBarFragment playBarFragment;
	public static PermissionUtil permissionUtil;
	private MusicServiceConnection mConnection;
	public static boolean isNow = true;
	private Handler mBaseHandler = new Handler();
	private final List<Activity> mActivityStack = new ArrayList<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTheme(getCurrentTheme());
		Configs.setStatusBar(this,ThemeManager.getCurrentColor(this));
		getWindowDisplay();
	//	showQuickControl(true);
		permissionUtil = new PermissionUtil(this);
		mConnection = new MusicServiceConnection(this,this);
		PlayerProxy.getIntance().startAndBindPlayService(this,mConnection);
		EventBus.getDefault().register(this);
		mActivityStack.add(this);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(mConnection);
		EventBus.getDefault().unregister(this);
		mActivityStack.remove(this);
	}
	public int getCurrentTheme(){
		mSharePreference = SharePreferenceManager.getInstance(getApplicationContext());
		return mSharePreference.getlastTheme();		
	}
	public void getWindowDisplayPx(){
    	Display display = this.getWindowManager().getDefaultDisplay();
    	DisplayMetrics outMetrics = new DisplayMetrics();
    	display.getMetrics(outMetrics); 
    	mScreenHeight= outMetrics.widthPixels;
    	mScreenWidth = outMetrics.heightPixels; 
    }
	public void getWindowDisplay(){
    	Display display = this.getWindowManager().getDefaultDisplay();
    	mScreenWidth = display.getWidth();
    	mScreenHeight = display.getHeight();
    }
	public void showQuickControl(boolean show) {
	        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	        if(this.findViewById(R.id.bottom_container)!=null){
		        if (show) {
		        	this.findViewById(R.id.bottom_container).setVisibility(View.VISIBLE);
		            if (playBarFragment == null) {
		            	Log.i("播放条为null","播放条为null");
		            	playBarFragment = PlayBarFragment.newInstance();
		                ft.add(R.id.bottom_container, playBarFragment).commitAllowingStateLoss();
		            } else {
		            	Log.i("播放条直接显示","播放条直接显示");
		                ft.show(playBarFragment).commitAllowingStateLoss();
		            	
		            }
		        } else {
		            if (playBarFragment != null){
		            	//ft.hide(playBarFragment).commitAllowingStateLoss();
		            	this.findViewById(R.id.bottom_container).setVisibility(View.GONE);
		            	playBarFragment = null;
		            	 Log.i("播放条隱藏","播放条隱藏");
		            }
		               
		        }
	        }

	    }		
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.i("強制退出", "強制退出");
		try {
			PlayerProxy.getIntance().saveCurrentState();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		Log.i("BaseActivity設置回調", "設置回調");
		permissionUtil.onActivityResult(arg0, arg1, arg2);
	}
	@Override
	public void onRequestPermissionsResult(int arg0, @NonNull String[] arg1,
			@NonNull int[] arg2) {
		// TODO Auto-generated method stub		
		Log.i("BaseActivity權限回調", "權限回調");
		permissionUtil.onRequestPermissionsResult(arg0,arg1, arg2);
		super.onRequestPermissionsResult(arg0, arg1, arg2);
	}
	//作用于其他页面
	public void onEvent(EventBusMsg msg){
			showQuickControl(true);
			Log.i("隱藏后重新顯示bar", "重新顯示bar");
	}
	public void onEvent(EventBusStop stop){
		showQuickControl(false);
		Log.i("显示后隱藏bar", "显示后隱藏bar");
	}
	@Override
	public void onBufferingUpdate(int percent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMusicStart() {
		// TODO Auto-generated method stub
		//一種是方法嵌套,一種是消息機制
		//作用于当前页面
		if(isNow){
			showQuickControl(true);
			Log.i("隱藏后重新顯示bar", "重新顯示bar");
			isNow = false;
		}
		EventBus.getDefault().post(new EventBusMsg());
		if(playBarFragment != null){			
			try {
				playBarFragment.startProgress();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onMusicPause() {
		// TODO Auto-generated method stub
		if(playBarFragment != null){
			playBarFragment.stopProgress();
		}
	}
	@Override
	public void onDeleteAll() {
		// TODO Auto-generated method stub
		if(playBarFragment != null){
			playBarFragment.stopProgress();
		}
		Log.i("執行onChange", "執行onChange");
		if(!isNow){
			showQuickControl(false);
			Log.i("onchange隱藏后bar", "onchange隱藏后bar");
			isNow = true;
		}				
		EventBus.getDefault().post(new EventBusStop());
	}	
	public void fininshAll(){
		Log.i("finishall", "finishall");
		for(Activity activity:mActivityStack){
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
		mActivityStack.clear();
	}

}
