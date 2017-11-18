package com.h2603953.littleyun.activity;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.bean.EventBusMsg;
import com.h2603953.littleyun.bean.EventBusStop;
import com.h2603953.littleyun.fragment.BaseFragment;
import com.h2603953.littleyun.fragment.CurrentPlayListDialogFragment;
import com.h2603953.littleyun.fragment.LrcFragment;
import com.h2603953.littleyun.fragment.OnChangeListener;
import com.h2603953.littleyun.fragment.PlayingFragment;
import com.h2603953.littleyun.service.MusicControl;
import com.h2603953.littleyun.service.PlayerProxy;
import com.h2603953.littleyun.service.SingleSongBean;
import com.h2603953.littleyun.util.Configs;
import com.h2603953.littleyun.util.DrawableUtil;
import com.h2603953.littleyun.util.ThemeManager;
import com.h2603953.littleyun.util.ToastUtil;
import com.h2603953.littleyun.widget.MyToolbar;

import de.greenrobot.event.EventBus;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayActivity extends BaseActivity implements OnChangeListener,OnClickListener{
	private MyToolbar toolbar;
	private LinearLayout linearLayout;
	private PlayingFragment playFragment;
	private LrcFragment lrcFragment;
	private int currentPosition;
	private ArrayList<SingleSongBean> list;
	private ArrayList<BaseFragment> fragments;
	private BaseFragment currentFragment;
	private SeekBar seekbar;
	private TextView currentTime,totalTime,currentSongTitle;
	private SingleSongBean currentSong;
	private ImageView pause,pre,next,musiclist,currentMode;
	private CurrentPlayListDialogFragment playListDialog;
	private int mode;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);			
		setContentView(R.layout.activity_play);
		Bundle bd = getIntent().getExtras();
		if(bd!=null){
			currentPosition = bd.getInt("currentSong");
			list = bd.getParcelableArrayList("playlist");			
		}
		if(list == null){
			try {
				currentPosition = PlayerProxy.getIntance().getCurrentPosition();
				list = (ArrayList<SingleSongBean>) PlayerProxy.getIntance().getPlayList();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			mode = PlayerProxy.getIntance().getPlayMode();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initView();
		setFullScreen();
		setToolBar();
		initFragment();
		initListener();

	}
	private void setFullScreen(){
		if(VERSION.SDK_INT>=VERSION_CODES.LOLLIPOP){
			Window window = getWindow();
//			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|
//			View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//			View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);	
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);			
			linearLayout.setFitsSystemWindows(true);
		}
	}
	private void initView(){
		toolbar = (MyToolbar)findViewById(R.id.playmusic_toolbar);
		linearLayout = (LinearLayout)findViewById(R.id.music_bg);
		seekbar = (SeekBar)findViewById(R.id.seekbar);
		currentTime = (TextView)findViewById(R.id.currenttime);
		totalTime = (TextView)findViewById(R.id.totaltime);
		pre = (ImageView)findViewById(R.id.currentplay_prev);
		pause = (ImageView)findViewById(R.id.currentplay_pause);
		next = (ImageView)findViewById(R.id.currentplay_next);
		musiclist = (ImageView)findViewById(R.id.currentplay_list);
		currentMode = (ImageView)findViewById(R.id.currentplay_mode);
		currentSongTitle = (TextView)findViewById(R.id.currentsongtitle);
		setSrcMode(mode);
		initBackground();
	}
	public void setSrcMode(int mode){
		switch(mode){
		case MusicControl.MODE_AUTO:
			currentMode.setImageResource(R.drawable.mode_random1);
			break;
		case MusicControl.MODE_NONE:
			currentMode.setImageResource(R.drawable.mode_none1);
			break;
		case MusicControl.MODE_REPEAT:
			currentMode.setImageResource(R.drawable.mode_repeat1);
			break;				
		}
	}
	private void initBackground(){
		Bitmap mBitmap = null;
		if(list!=null){
			mBitmap = DrawableUtil.loadCover(this,list.get(currentPosition).album_art,list.get(currentPosition).islocal);
		}else{
			mBitmap = DrawableUtil.loadCover(this,"",2);
		}
		if(mBitmap!=null){
			linearLayout.setBackground(DrawableUtil.createBlurredImageFromBitmap(mBitmap, this, 9));
		}
	}
	
	private void initFragment(){
		fragments = new ArrayList<>();
        playFragment = PlayingFragment.newInstance(list,currentPosition);
        lrcFragment = LrcFragment.newInstance(list.get(currentPosition));
        playFragment.setOnChangeListener(this);
        lrcFragment.setOnChangeListener(this);
        fragments.add(0, playFragment);
        fragments.add(1, lrcFragment);
        currentFragment = fragments.get(0);
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.music_container, lrcFragment, "lrcFragment");
        transaction.add(R.id.music_container, playFragment, "playFragment");
        transaction.hide(lrcFragment);
        transaction.commit();
        
	}
	private void setToolBar(){
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");	
	}
	private void initListener(){
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				int progress = seekbar.getProgress();
				try {
					PlayerProxy.getIntance().seekTo(progress);		
					currentTime.setText(praseTime(progress));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
			}
		});
		pre.setOnClickListener(this);
		pause.setOnClickListener(this);
		next.setOnClickListener(this);
		musiclist.setOnClickListener(this);
		currentMode.setOnClickListener(this);
	}
	public void changePager(int position){
		currentPosition = position;
		currentSong = list.get(position);
		if(currentSong!=null){
			long duration = currentSong.duration;
			long progress;
			try {
				progress = PlayerProxy.getIntance().getProgress();
				currentSongTitle.setText(currentSong.songName);
				initBackground();
				seekbar.setMax((int) duration);
				seekbar.setProgress((int) progress);
				totalTime.setText(praseTime(duration));			
				currentTime.setText(praseTime(progress));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Log.i("存儲歌曲為空", "存儲歌曲為空");
		}
	}
	private void setInitState() throws RemoteException{
		currentSong = PlayerProxy.getIntance().getCurrentSong();
		list = (ArrayList<SingleSongBean>) PlayerProxy.getIntance().getPlayList();
		currentPosition = PlayerProxy.getIntance().getCurrentPosition();
		if(list!=null&&playFragment!=null){
			playFragment.setCurrentPager(list, currentPosition);
			initBackground();
		}
		
		if(currentSong!=null){
			long duration = currentSong.duration;
			long progress = PlayerProxy.getIntance().getProgress();
			currentSongTitle.setText(currentSong.songName);
			seekbar.setMax((int) duration);
			seekbar.setProgress((int) progress);
			totalTime.setText(praseTime(duration));			
			currentTime.setText(praseTime(progress));
			Log.i("currentTime",praseTime(duration));
			Log.i("totalTime",praseTime(progress));
		}else{
			Log.i("存儲歌曲為空", "存儲歌曲為空");
		}
	}
    public String praseTime(long milliSecs) {
        StringBuffer sb = new StringBuffer();
        long m = milliSecs / (60 * 1000);
        sb.append(m < 10 ? "0" + m : m);
        sb.append(":");
        long s = (milliSecs % (60 * 1000)) / 1000;
        sb.append(s < 10 ? "0" + s : s);
        return sb.toString();
    }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			setInitState();
			if(PlayerProxy.getIntance().isPlaying()){
		    	seekbar.post(mUpdateProgress);
		    	pause.setImageResource(R.drawable.pause);
//		    	if(playFragment!=null){
//		    		Log.i("playActivity", "onResume");
//		    		playFragment.endAnimator();
//		    		playFragment.startRoundViewAnimator();
//		    	}
			}else{				
				pause.setImageResource(R.drawable.play);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {
            try {
	            int position = PlayerProxy.getIntance().getProgress();
	            	seekbar.setProgress(position);
	    			currentTime.setText(praseTime(position));
				if (PlayerProxy.getIntance().isPlaying()) {
					seekbar.postDelayed(mUpdateProgress, 1000);
				}else {
					seekbar.removeCallbacks(mUpdateProgress);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
    };
	@Override
	public void onBufferingUpdate(int percent) {
		// TODO Auto-generated method stub
		super.onBufferingUpdate(percent);
		seekbar.setSecondaryProgress(percent);
	}
	@Override
	public void onMusicStart() {
		// TODO Auto-generated method stub
		EventBus.getDefault().post(new EventBusMsg());
    	if(seekbar!=null){
        	try {
				setInitState();
	        	if(PlayerProxy.getIntance().isPlaying()){
	            	seekbar.post(mUpdateProgress);
	            	pause.setImageResource(R.drawable.pause);   
	            	if(playFragment!=null){
	            		playFragment.endAnimator();
	           		playFragment.startRoundViewAnimator();
	            	}
	        	}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} 

	}
	@Override
	public void onMusicPause() {
		// TODO Auto-generated method stub
		stopProgress();
	}
	@Override
	public void onDeleteAll() {
		// TODO Auto-generated method stub
		EventBus.getDefault().post(new EventBusStop());
		stopProgress();
		Intent it = new Intent(this,MainActivity.class);
		startActivity(it);
		finish();

	}
	private void stopProgress(){
		pause.setImageResource(R.drawable.play);
		seekbar.removeCallbacks(mUpdateProgress);
    	if(playFragment!=null){
    		playFragment.endAnimator();
    	}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
    	if(playFragment!=null){
    		playFragment.endAnimator();
    	}
	}
	@Override
	public void changeFragment(){
		if(currentFragment == fragments.get(0)){
			Log.i("隐藏0-0", "隐藏0-0");
			currentFragment = fragments.get(1);
	    	FragmentManager fragmentManager = getSupportFragmentManager();
	    	FragmentTransaction transaction = fragmentManager.beginTransaction();
			if(fragments.get(1).isAdded()){
				Log.i("隐藏0-1", "隐藏0-1");
				transaction.hide(fragments.get(0)).show(fragments.get(1)).commitAllowingStateLoss();
			}else{
				Log.i("隐藏0-2", "隐藏0-2");
				transaction.hide(fragments.get(0)).add(R.id.music_container, fragments.get(1), "lrcFragment").commitAllowingStateLoss();
			}
		}else{
			Log.i("隐藏1-0", "隐藏1-0");
			currentFragment = fragments.get(0);
	    	FragmentManager fragmentManager = getSupportFragmentManager();
	    	FragmentTransaction transaction = fragmentManager.beginTransaction();
			if(fragments.get(0).isAdded()){
				Log.i("隐藏1-1", "隐藏1-1");
				transaction.hide(fragments.get(1)).show(fragments.get(0)).commitAllowingStateLoss();
			}else{
				Log.i("隐藏1-2", "隐藏1-2");
				transaction.hide(fragments.get(1)).add(R.id.music_container, fragments.get(0), "playFragment").commitAllowingStateLoss();
			}
		}		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.currentplay_mode:
			switch(mode){
			case MusicControl.MODE_AUTO:
				mode = MusicControl.MODE_REPEAT;
				currentMode.setImageResource(R.drawable.mode_repeat1);
				break;
			case MusicControl.MODE_NONE:
				mode = MusicControl.MODE_AUTO;
				currentMode.setImageResource(R.drawable.mode_random1);
				break;
			case MusicControl.MODE_REPEAT:
				mode = MusicControl.MODE_NONE;
				currentMode.setImageResource(R.drawable.mode_none1);
				break;				
			}
			try {
				PlayerProxy.getIntance().setPlayMode(mode);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case R.id.currentplay_pause:
			try {				
				PlayerProxy.getIntance().playOrPause();	
				pause.setImageResource(PlayerProxy.getIntance().isPlaying()? R.drawable.pause:R.drawable.play);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.currentplay_prev:
			try {
		    	if(playFragment!=null){
		    		playFragment.endAnimator();
		    	}
				PlayerProxy.getIntance().pre();
				//setInitState();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			break;
		case R.id.currentplay_next:
			try {
		    	if(playFragment!=null){
		    		playFragment.endAnimator();
		    	}
				PlayerProxy.getIntance().next();
				//setInitState();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.currentplay_list:
			
			if(list!= null){
				try {
					mode = PlayerProxy.getIntance().getPlayMode();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				playListDialog = new CurrentPlayListDialogFragment(this, 0, 500, LayoutParams.MATCH_PARENT, (int)(mScreenHeight*0.7),list,mode);
				playListDialog.show(getSupportFragmentManager(), "playListDialog");
			}else{
				ToastUtil.show("暫無播放列表");
			}	
			break;
	}		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.playmusic, menu);				
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        switch (item.getItemId()) {
        case android.R.id.home:
        	onBackPressed();
            return true;
        case R.id.scan_localmusic:
        	//掃描音樂
        case R.id.down_localmusicwords:
        	//下載歌詞和封面
        case R.id.improve_musicquatity:
        	//音質管理
        default:		
		}
        return super.onOptionsItemSelected(item);
	}
	public static void startPlayActivity(Context context,String str){
		Intent it = new Intent(context,PlayActivity.class);
		it.putExtra("fromActivity", str);
		context.startActivity(it);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		Intent it = getIntent();
		String str = it.getStringExtra("fromActivity");
		if(str!= null&&str.equals("localMusic")){
			Intent toLocalActivity = new Intent(PlayActivity.this,LocalMusic.class);
			startActivity(toLocalActivity);
			finish();
		}else{
			Intent toMainActivity = new Intent(PlayActivity.this,MainActivity.class);
			startActivity(toMainActivity);
			finish();
		}

	}
	
}
