package com.h2603953.littleyun.util;

import java.io.File;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.crop.CropUtil;
import com.h2603953.littleyun.service.MusicControl;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class SharePreferenceManager {
	private static SharePreferenceManager mPreferenceManager;
	private static SharedPreferences mPreferences;
	private static SharedPreferences.Editor editor;
	private static Context context;
	private SharePreferenceManager(Context context){
		this.context = context;
		mPreferences =context.getSharedPreferences("Service", 0);	
		editor = mPreferences.edit();
	}
	public static SharePreferenceManager getInstance(Context context){
		if(mPreferenceManager == null){
			synchronized (SharePreferenceManager.class) {
				if(mPreferenceManager == null){
					mPreferenceManager = new SharePreferenceManager(context);
				}
			}
		}
		return mPreferenceManager;
	}
	
	public void setCurrentTheme(int theme){
	    editor.putInt("last_theme", theme);
	    editor.commit();
	}
	public int getlastTheme(){
	    return mPreferences.getInt("last_theme", R.style.AppTheme);
	}

	public void setCurrentSongId(long mCurrentSongId){
	    editor.putLong("song_id", mCurrentSongId);
	    editor.commit();
	}
	public long getCurrentSongId(){
		return mPreferences.getLong("song_id", -1);
	}
	
	public void setCurrentSongPosition(int position){
	    editor.putInt("song_position", position);
	    editor.commit();
	}
	public int getCurrentSongPosition(){
		return mPreferences.getInt("song_position", 0);
	}
	public void setMusicMode(int mode){
	    editor.putInt("music_mode", mode);
	    editor.commit();
	}
	public int getMusicMode(){
		return mPreferences.getInt("music_mode", MusicControl.MODE_NONE);
	}
	public void setCurrentSongBean(String value){
		 editor.putString("song_bean",value);
		 editor.commit();
	}
	public String getCurrentSongBean(){
		return mPreferences.getString("song_bean", null);
	}
	public void setCurrentMusicList(String value){
		 editor.putString("music_list",value);
		 editor.commit();
	}
	public String getCurrentMusicList(){
		return mPreferences.getString("music_list", null);
	}
	public void setPositionNumber(int position){
		 editor.putInt("position_number",position);
		 editor.commit();
	}
	public int getPositionNumber(){
		return mPreferences.getInt("position_number", 0);
	}
}
