package com.h2603953.littleyun.service;

import static android.content.Context.AUDIO_SERVICE;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.h2603953.littleyun.util.GsonUntil;
import com.h2603953.littleyun.util.SharePreferenceManager;
import com.h2603953.littleyun.util.ToastUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.session.MediaSessionManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

public class MusicControl implements MediaPlayer.OnErrorListener {
	private static MusicControl musicControl;
	//这个类待查资料,暂不写
    private MediaSessionManager sessionManager;
    private AudioManager mAudioManager;
    private boolean isAlreadyGetList = false;
    private MyWakeLock mWakeLock;

    private long mCurrentSongId = -1;    
    private List<SingleSongBean> mPlayList = Collections.synchronizedList(new ArrayList<SingleSongBean>());
    private SingleSongBean mCurrentSong;
    private int mCurrentPosition = -1;
    private int mProgress = 0;
    private MediaPlayer mPlayer,mNextPlayer1,mNextPlayer2;
	private MusicService mContext;
	private boolean isInitialized = false,isNextInitialized = false;
	
	
	private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARED = 1;
	private static final int STATE_PLAYING = 2;
	private static final int STATE_PAUSED = 3;
	private static final int STATE_STOPED = 4;
	
	
	public static final int MODE_NONE = 5;
	public static final int MODE_REPEAT = 6;
	public static final int MODE_AUTO = 7;
	private int mMode = MODE_NONE;
	
	private static final int MAINHANDLER_UPEATE = 11;
	
	private List<Integer> mHistoryPositions = new ArrayList<>();
	private Random random = new Random();
	
	
	private int mPlayState = STATE_IDLE ;
	private AudioFocusManager audioFocusListener;
	private UIChangedListener mUiListener;
	private HandlerThread mHandlerThread;
	private MusicHandler mHandler;
	private static final int INIT_STATE = 8;
	private boolean isSave = false;
	private int nextPosition = -1;
	private MainHandler mainHandler;
	
	private boolean isNowPlaying = false;
       
	private MusicControl(Context context){
		if(context instanceof MusicService){
			mContext = (MusicService) context;			
		}
        mWakeLock = new MyWakeLock(mContext);
        mWakeLock.acquireWakeLock();
		mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
		mNextPlayer1 = new MediaPlayer();
		mNextPlayer2 = new MediaPlayer();
		initPlayer(mNextPlayer1,mNextPlayer2);
		initPlayer(mNextPlayer2,mNextPlayer1);
		mPlayer = mNextPlayer1;
		mainHandler = new MainHandler();
		audioFocusListener = new AudioFocusManager(this);
		mHandlerThread = new HandlerThread("musicThread");
		mHandlerThread.start();
		mHandler = new MusicHandler(mHandlerThread.getLooper());
		mHandler.sendEmptyMessage(INIT_STATE);
		
	}
	void initPlayer(MediaPlayer player,MediaPlayer nextPlayer){
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
		player.setOnBufferingUpdateListener(mBufferingUpdateListener);
		player.setOnCompletionListener(new CompletionListener(nextPlayer));
		player.setOnErrorListener(this);
	}
	public static MusicControl getInstance(Context context){
		if(musicControl == null){
			synchronized(MusicControl.class){
				if(musicControl == null){
					musicControl = new MusicControl(context);
				}
			}
		}
		return musicControl;
	}
	class MusicHandler extends Handler{
		public MusicHandler(Looper looper){
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case INIT_STATE:
				getCurrentState();
				break;
			}
		}
		
	}
	class CompletionListener implements OnCompletionListener{
		MediaPlayer nextPlayer;
		public CompletionListener(MediaPlayer next){
			nextPlayer = next;
		}

		@Override
		public void onCompletion(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			if(arg0 == mPlayer && nextPlayer!=null){						
			mPlayer = nextPlayer;
	    	mCurrentPosition = nextPosition;
	    	mHistoryPositions.add(mCurrentPosition);
	    	Log.i("改变后当前位置", mCurrentPosition+"");
	    	if(mHistoryPositions.size()>mPlayList.size())mHistoryPositions.remove(0);
			mCurrentSong = mPlayList.get(mCurrentPosition);
			mCurrentSongId = mCurrentSong.songId;
			 if (mPlayer.isPlaying() && mUiListener != null) {
	            	try {
	            		Log.i("nextPlayer播放时", "执行activity中的回调");
						mUiListener.onMusicStart();
						mainHandler.obtainMessage(MAINHANDLER_UPEATE, true).sendToTarget();					
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	            	
	            }
			setNextPlayer();						
		}else{
			Log.i("onCompletion中", "nextplayer==null");
			mContext.cancelNotification();
			mWakeLock.releaseWakeLock();
		}	
		}
	}

    public boolean requestAudioFocus() {
        return mAudioManager.requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }
    public void abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(audioFocusListener);
    }
    
    public void setUiListener(UIChangedListener uiListener){
    	mUiListener = uiListener;
    }

    public void setVolume(float vol) {
        mPlayer.setVolume(vol, vol);
    }
    public void setMode(int mode){
    	Log.i("设置了mode", mode+"");
    	mMode = mode;
    }
    public int getMode(){
    	return mMode;
    }
    public int getCurrentPosition(){
    	return mCurrentPosition;
    }
    public synchronized List<SingleSongBean> getPlayList(){
    	while(!isAlreadyGetList){
    		try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return mPlayList;
    }
    public void deleteAll(){
		if(isPlaying()){
			mPlayer.stop();	
			mPlayState = STATE_STOPED;
			}
	    	mPlayList = null;
	    	mCurrentSongId = -1;
	    	mProgress = 0;
	    	mCurrentSong = null;
	    	mPlayList = null;	
	    	mCurrentPosition = -1;
            if (mUiListener != null) {
            	try {
					mUiListener.onDeleteAll();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

    }
    public void delete(int position){
    	mPlayList.remove(position);
    	if(position!=mCurrentPosition){
    		Log.i("刪除position", position+"");
    		--mCurrentPosition;
    		if(mCurrentPosition<0)mCurrentPosition =0;
        	mHistoryPositions.add(mCurrentPosition);
        	if(mHistoryPositions.size()>mPlayList.size())mHistoryPositions.remove(0);
    		setNextPlayer();    		
    	}else if(position==mCurrentPosition){
    		if(isPlaying()){
    			mPlayer.stop();	
    			mPlayState = STATE_STOPED;
    			play(mCurrentPosition,true);
    		}else{
    			play(mCurrentPosition,false);
    		}    	
    		
    	}
    	Log.i("刪除后當前position", mCurrentPosition+"");
    }
    public void setSongs(List<SingleSongBean> list, int position){
    	mPlayList = list;
    	mHistoryPositions.clear();
    	play(position,true);
    }
    public void play(int position,boolean isPlaying){
    	if(mPlayList == null)return;
    	mCurrentPosition = position<0 ? 0: (position>mPlayList.size()-1 ? mPlayList.size()-1:position);
    	mHistoryPositions.add(mCurrentPosition);
    	if(mHistoryPositions.size()>mPlayList.size())mHistoryPositions.remove(0);
    	mCurrentSong = mPlayList.get(mCurrentPosition);
		mCurrentSongId = mCurrentSong.songId;
		play(mCurrentSong,isPlaying);
    }
    public void play(SingleSongBean song,boolean isPlaying){
    	isNowPlaying = isPlaying;
    	if(isPlaying()){
			mPlayer.stop();
		}
    	if(isInitialized){
    		mPlayer.setNextMediaPlayer(null);
    		mPlayer.reset();
    		}
    	try {
			mPlayer.setDataSource(song.songData);
			Log.i("当前位置", mCurrentPosition+"");			
	    	isInitialized = true;
		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	mPlayer.prepareAsync();
    	mPlayer.setOnPreparedListener(mPreparedListener);  	
    	setNextPlayer();
    }
    public void setNextPlayer(){
    	//mPlayer.setNextMediaPlayer(null);
    	if(mPlayer == mNextPlayer1){
    		Log.i("当前为mNextPlayer1", "先setmNextPlayer2");
    		setNextSong(mNextPlayer2);
    	}else{
    		Log.i("当前为mNextPlayer2", "先setmNextPlayer1");
    		setNextSong(mNextPlayer1);
    	}    			
    }
    public void setNextSong(MediaPlayer player){
    	try {
        	if(isNextInitialized){
//        		if(player.isPlaying()){
//        			player.stop();
//        		}
        		player.setNextMediaPlayer(null);
        		player.reset();        		
        		}
        	nextPosition = getNextPosition();
			player.setDataSource(mPlayList.get(nextPosition).songData);
			Log.i("设置下一首位置", nextPosition+"");
			isNextInitialized = true;
			player.prepareAsync();
			player.setOnPreparedListener(mNextPreparedListener);			
		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

//繼續播放
   public void continuePlay(MediaPlayer palyer) {
        if (mPlayState == STATE_PAUSED || mPlayState == STATE_PREPARED) {
        	if(requestAudioFocus()){
        		if(isSave){
        			palyer.seekTo(mProgress);
        			isSave = false;
        		} 
        		palyer.start();
        		mPlayState = STATE_PLAYING;
                if (mUiListener != null) {
                	try {
						mUiListener.onMusicStart();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
        		//更新notification,更新進度
        		//待定
        		mainHandler.obtainMessage(MAINHANDLER_UPEATE, true).sendToTarget();
        		
//                Notifier.showPlay(mPlayingMusic);
//                mMediaSessionManager.updatePlaybackState();
//                registerReceiver(mNoisyReceiver, mNoisyFilter);

        	}       	
        }else if(mPlayState == STATE_IDLE && mCurrentSong != null){
        	isSave = true;
        	play(mCurrentSong,true);
        	
//        	ToastUtil.show("暫無可播放歌曲");
        }
    }
	public void pause(){
		if(isPlaying()){
			mPlayer.pause();
			Log.i("mPlayer.pause()", "mPlayer.pause()");
			mPlayState = STATE_PAUSED;
			//更新notification,更新進度
			//待定
            if (mUiListener != null) {
            	try {
					mUiListener.onMusicPause();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            mainHandler.obtainMessage(MAINHANDLER_UPEATE, false).sendToTarget();
		}		
	}
	public void playOrPause(){
		if(isPlaying()){
			pause();
		}else{
			continuePlay(mPlayer);
		}
	}
	public void pre(boolean isPlaying){
		play(--mCurrentPosition,isPlaying);	
	}
	public void next(boolean isPlaying){
			play(++mCurrentPosition,isPlaying);
	}
	public void stop(){
		if(isPlaying()){
			mPlayer.stop();	
			mPlayState = STATE_STOPED;
            if (mUiListener != null) {
            	try {
					mUiListener.onMusicPause();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            mainHandler.obtainMessage(MAINHANDLER_UPEATE, false).sendToTarget();
		}
		mNextPlayer1.release();
		mNextPlayer2.release();
		mNextPlayer1 = null;
		mNextPlayer2 = null;
		mAudioManager.abandonAudioFocus(audioFocusListener);
		mWakeLock.releaseWakeLock();
	}
	public boolean isPlaying(){
		if(mPlayer == null) return false;
		return mPlayer.isPlaying();
	}
    public void seekTo(int msec) {
        if (isPlaying() || mPlayState == STATE_PAUSED) {
            if (msec < 0) {
            	msec = 0;
            } else if (msec > mPlayer.getDuration()) {
            	msec = mPlayer.getDuration();
            }
            mPlayer.seekTo(msec);
 //           mMediaSessionManager.updatePlaybackState();
        }
    }
    public int getNextPosition(){
    	int nextPosition = 0;
    	int listSize = mPlayList.size();
    	int i = 0;
    	switch(mMode){
    	case MODE_AUTO:    		
    		int historySize = mHistoryPositions.size();
    		if(historySize == listSize){   			
    		List<Integer> list = mHistoryPositions.subList(0,historySize/2);
    			while(i<10){
    				nextPosition = random.nextInt(listSize);
    				i++;
    				if(list.contains(nextPosition))break;
    			}
    		}else if(historySize < listSize){
    			while(i<10){
    				nextPosition = random.nextInt(listSize);
    				i++;
    				if(!mHistoryPositions.contains(nextPosition))break;
    			}	
    		}
    		Log.i("播放模式", "MODE_AUTO"+nextPosition);
    		break;
    	case MODE_NONE:
    		nextPosition = mCurrentPosition >= (listSize -1)?0:mCurrentPosition+1;
    		Log.i("播放模式", "MODE_NONE"+nextPosition);
    		break;
    	case MODE_REPEAT:
    		nextPosition = mCurrentPosition;
    		Log.i("播放模式", "MODE_REPEAT"+nextPosition);
    		break;
    	}
    	return nextPosition;
    }
    public long getCurrentSongId(){
    	Log.i("getCurrentSongId", mCurrentSongId+"");
    	return mCurrentSongId;
    }
    public int getProgress(){
    	if(mPlayer != null && isInitialized){
    		mProgress = mPlayer.getCurrentPosition();
    	}
    	return mProgress;
    }
    public int getDuration(){
    	return mPlayer.getDuration();
    }
    public SingleSongBean getCurrentSong(){
    	return mCurrentSong;
    }
    public void saveCurrentState(){
    	SharePreferenceManager.getInstance(mContext).setCurrentSongId(mCurrentSongId);
    	SharePreferenceManager.getInstance(mContext).setCurrentSongPosition(mProgress);
    	SharePreferenceManager.getInstance(mContext).setMusicMode(mMode);
    	SharePreferenceManager.getInstance(mContext).setCurrentSongBean(GsonUntil.getInstance().objectTojson(mCurrentSong));
    	SharePreferenceManager.getInstance(mContext).setCurrentMusicList(GsonUntil.getInstance().objectTojson(mPlayList));    	
    	SharePreferenceManager.getInstance(mContext).setPositionNumber(mCurrentPosition);
    	
    }
    public synchronized void getCurrentState(){
    	mCurrentSongId = SharePreferenceManager.getInstance(mContext).getCurrentSongId();
    	mProgress = SharePreferenceManager.getInstance(mContext).getCurrentSongPosition();
    	mMode = SharePreferenceManager.getInstance(mContext).getMusicMode();
    	String songBean = SharePreferenceManager.getInstance(mContext).getCurrentSongBean();
    	mCurrentSong = GsonUntil.getInstance().GsonToBean(songBean, SingleSongBean.class);
    	String musicList = SharePreferenceManager.getInstance(mContext).getCurrentMusicList();
    	mPlayList = GsonUntil.getInstance().GsonToList(musicList, SingleSongBean.class);
    	mCurrentPosition = SharePreferenceManager.getInstance(mContext).getPositionNumber();
    	Log.i("獲取存儲","獲取存儲");
    	isAlreadyGetList = true;
    	notifyAll();
    }
    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
//        	if(mPlayState !=STATE_PLAYING){
//        		Log.i("在prepared的时候暂停", "在prepared的时候暂停");
//        		return;
//        	}
        	Log.i("第一首开始播放", "");
        	mPlayState = STATE_PREPARED;
        	if(isNowPlaying){
            	continuePlay(mp);        		            	
        	}else{
              if (mUiListener != null) {
            	try {
					mUiListener.onMusicStart();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
              mainHandler.obtainMessage(MAINHANDLER_UPEATE, false).sendToTarget();
        	}
        }
    };
    private MediaPlayer.OnPreparedListener mNextPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {       	
        	try{
            	mPlayer.setNextMediaPlayer(mp);
            	Log.i("prepared下一首播放器", "prepared下一首播放器");       		
        	}catch(IllegalStateException ex){
        		Log.i("NextPrepared IllegalStateException", ex.toString());
        	}catch(IllegalArgumentException ex){
        		Log.i("NextPrepared IllegalArgumentException", ex.toString());
        	}

        }
    };
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mUiListener != null) {
            	try {
					mUiListener.onBufferingUpdate(percent);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }
        }
    };
	class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
	    private boolean isPausedByFocusLossTransient = false;
	    private int mVolumeWhenFocusLossTransientCanDuck;
    	private WeakReference<MusicControl> playerControl;

	    public AudioFocusManager(MusicControl control) {
	        playerControl = new WeakReference<MusicControl>(control);
	    }
	    @Override
	    public void onAudioFocusChange(int focusChange) {
	        int volume;
	        switch (focusChange) {
            // 暫時丢失焦点，如来电,應暫停播放,但不清除
	           case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
	        	   if(playerControl.get().isPlaying()){
	        		   isPausedByFocusLossTransient = true;
	        	   }
	            	   playerControl.get().pause();	                   
	               break;
	        	// 重新获得焦点
	            case AudioManager.AUDIOFOCUS_GAIN:
	            	if(isPausedByFocusLossTransient){
	            		isPausedByFocusLossTransient = false;
	            		playerControl.get().continuePlay(mPlayer);	            		
	            	}
	                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	                if (mVolumeWhenFocusLossTransientCanDuck > 0 && volume == mVolumeWhenFocusLossTransientCanDuck / 2) {
	                	 // 恢复音量
	                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck,
	                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	                }	                
	                mVolumeWhenFocusLossTransientCanDuck = 0;
	                break;
	             // 永久丢失焦点，如被其他播放器抢占,清理資源
	            case AudioManager.AUDIOFOCUS_LOSS:	            	
	            	playerControl.get().stop();
	            	isPausedByFocusLossTransient = true;
	                break;

	             // 瞬间丢失焦点，如通知,但是允許持續播放音樂(以很小的聲音)
	            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
	            	// 音量减小为一半
	                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	                if (playerControl.get().isPlaying() && volume > 0) {
	                    mVolumeWhenFocusLossTransientCanDuck = volume;
	                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck / 2,
	                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	                }
	                break;
	        }
	    }
	}
	class MainHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case MAINHANDLER_UPEATE:
			boolean isPlaying = (boolean) msg.obj;
			mContext.updateNotification(mCurrentSong, isPlaying);
			}
		}
		
	}

	@Override
	public boolean onError(MediaPlayer arg0, int what, int extra) {
		// TODO Auto-generated method stub
		Log.i(arg0.getClass().getName(), "Music Server Error what: " + what + " extra: " + extra);
//        switch (what) {
//        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
//        	Log.i("MediaPlayer.MEDIA_ERROR_SERVER_DIED",MediaPlayer.MEDIA_ERROR_SERVER_DIED+"");
//        	
//            return true;
//        case :
//        	
//        default:
//            break;
//    }
		return true;
	}

}
