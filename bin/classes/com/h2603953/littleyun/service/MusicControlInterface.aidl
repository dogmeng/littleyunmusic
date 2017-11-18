package com.h2603953.littleyun.service;
import com.h2603953.littleyun.service.SingleSongBean;
import com.h2603953.littleyun.service.UIChangedListener;
interface MusicControlInterface {
	void setSongs(in List<SingleSongBean> list, int position);
	void play(int position,boolean isPlaying);
	void pause();
	void pre();
	void next();
	void setUiListener(UIChangedListener uiListener);
	void setPlayMode(int mode);
	int getPlayMode();
	long getCurrentSongId();	
	boolean isPlaying();
	void playOrPause();
	int getProgress();
	int getDuration();
	SingleSongBean getCurrentSong();
	List<SingleSongBean> getPlayList();
	void saveCurrentState();
	void deleteAll();
	void delete(int position);
	int getCurrentPosition();
	void seekTo(int position);

}