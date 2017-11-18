package com.h2603953.littleyun.service;
import com.h2603953.littleyun.service.SingleSongBean;
interface UIChangedListener {
	
	void onBufferingUpdate(int percent);
	
	void onMusicStart();
	
	void onMusicPause();
	
	void onDeleteAll();

}