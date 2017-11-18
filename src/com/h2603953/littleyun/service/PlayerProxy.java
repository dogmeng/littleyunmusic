package com.h2603953.littleyun.service;

import java.util.List;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class PlayerProxy {
	private MusicControlInterface mControl;
	private static PlayerProxy mPlayerProxy;
	public static synchronized PlayerProxy getIntance(){
		if(mPlayerProxy == null){
			mPlayerProxy = new PlayerProxy();
		}
		return mPlayerProxy;
	}
	Intent intent;
	Context context;
	public void startAndBindPlayService(Context context, ServiceConnection connection){
		intent = new Intent(context, MusicService.class);
    	if(mControl == null){
            context.startService(intent);    		
    	}
    	this.context = context;
        context.bindService(intent, connection, Service.BIND_AUTO_CREATE);        
	}
	public void stopService(){
		if(intent!=null&&context!=null)
		context.stopService(intent);
	}
	public void setService(MusicControlInterface control){
		if(mControl == null){
			mControl = control;
		}
	}
	public void setSongs(List<SingleSongBean> list, int position) throws RemoteException{
		mControl.setSongs(list, position);
	}
	public void play(int position,boolean isPlaying) throws RemoteException{
		mControl.play(position,isPlaying);
	}
	public long getCurrentSongId() throws RemoteException{
		return mControl.getCurrentSongId();
	}
	public void pause() throws RemoteException{
		mControl.pause();
	}
	public void playOrPause() throws RemoteException{
		mControl.playOrPause();
	}
	public void pre() throws RemoteException{
		mControl.pre();
	}
	public void next() throws RemoteException{
		mControl.next();
	}
	public boolean isPlaying() throws RemoteException{
		if(mControl == null){
			return false;
		}
		return mControl.isPlaying();
	}
	public int getProgress() throws RemoteException{
		return mControl.getProgress();
	}
	public int getDuration() throws RemoteException{
		return mControl.getDuration();
	}
	public SingleSongBean getCurrentSong() throws RemoteException{
		if(mControl!=null){
			return mControl.getCurrentSong();
		}
		return null;
		
	}
	public List<SingleSongBean> getPlayList() throws RemoteException{
		if(mControl!=null){
			return mControl.getPlayList();
		}
		return null;
		
	}
	public int getPlayMode() throws RemoteException{
		if(mControl!=null){
			return mControl.getPlayMode();			
		}
		return 0;
		
	}
	public void saveCurrentState() throws RemoteException{
		if(mControl!=null)
		 mControl.saveCurrentState();
	}
	public void setPlayMode(int mode) throws RemoteException{
		mControl.setPlayMode(mode);
	}
	public void deleteAll() throws RemoteException{
		mControl.deleteAll();
	}
	public void delete(int position) throws RemoteException{
		mControl.delete(position);
	}
	public int getCurrentPosition() throws RemoteException{
		return mControl.getCurrentPosition();
	}
	public void seekTo(int position) throws RemoteException{
		mControl.seekTo(position);
	}


}
