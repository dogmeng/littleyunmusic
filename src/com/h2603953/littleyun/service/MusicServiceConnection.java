package com.h2603953.littleyun.service;

import java.util.ArrayList;
import java.util.List;

import com.h2603953.littleyun.activity.BaseActivity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

public class MusicServiceConnection implements ServiceConnection{
	private MusicControlInterface control;
	private UIChangedListener uiListener;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private BaseActivity activity;
	public MusicServiceConnection(final UIChangedListenerImpl listener,BaseActivity activity){
		this.activity = activity;
		uiListener = new UIChangedListener.Stub() {
			
			@Override
			public void onMusicStart() throws RemoteException {
				// TODO Auto-generated method stub
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						listener.onMusicStart();
					}
				});
			}			
			
			@Override
			public void onMusicPause() throws RemoteException {
				// TODO Auto-generated method stub
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						listener.onMusicPause();
					}
				});
			}
			
			@Override
			public void onBufferingUpdate(int percent) throws RemoteException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDeleteAll() throws RemoteException {
				// TODO Auto-generated method stub
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						listener.onDeleteAll();
					}
				});
			}
		};
	}

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		// TODO Auto-generated method stub
		control = MusicControlInterface.Stub.asInterface(arg1);
		PlayerProxy.getIntance().setService(control);
		try {
			//因为在线程里面获取list,有时候执行此方法时,线程并未获取到,故不显示
			List<SingleSongBean> song = PlayerProxy.getIntance().getPlayList();
			if(song!=null&&song.size()>0){
				Log.i("执行showcontrol", "执行showcontrol");
				activity.showQuickControl(true);
				activity.isNow = false;
			}else{
				Log.i("隐藏showcontrol", "隐藏showcontrol");
				activity.showQuickControl(false);
				activity.isNow = true;
			}
			
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			control.setUiListener(uiListener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		// TODO Auto-generated method stub
		PlayerProxy.getIntance().startAndBindPlayService(activity,this);
	}

}
