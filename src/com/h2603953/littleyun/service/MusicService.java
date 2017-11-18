package com.h2603953.littleyun.service;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.print.attribute.standard.Finishings;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.activity.PlayActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

public class MusicService extends Service{
	private MusicControl mControl;
	private Notification mNotification;
	private int id = 110;
	private NotificationTarget notificationTarget,notificationTargetSmall;
	private static final String NOTIFICATION_PLAY_PAUSE = "notification_play_pause";
	private static final String NOTIFICATION_PRE = "notification_pre";
	private static final String NOTIFICATION_NEX = "notification_next";
	private static final String NOTIFICATION_CANCEL = "notification_cancel";
    private static NotificationManager notificationManager;
    private static final String NOTIFICATION_START = "notification_start";
    private static final String NOTIFICATION_STOP = "notification_stop";
    private String notifyMode = NOTIFICATION_STOP;
    private int currentactivityid;
    
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i("onCreat()","service oncreate");
		mControl = MusicControl.getInstance(this);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
        // Initialize the intent filter and each action
        IntentFilter filter = new IntentFilter();
        filter.addAction(NOTIFICATION_PLAY_PAUSE);
        filter.addAction(NOTIFICATION_PRE);
        filter.addAction(NOTIFICATION_NEX);
        filter.addAction(NOTIFICATION_CANCEL);
        // Attach the broadcast listener
        registerReceiver(mIntentReceiver, filter);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mControl.saveCurrentState();
		mControl.stop();
		stopForeground(true);
		stopSelf();
		unregisterReceiver(mIntentReceiver);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		currentactivityid = startId;
		return START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return new PlayBinder(mControl);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		mControl.saveCurrentState();
		Log.i("存儲了狀態", "存儲了狀態");
		return super.onUnbind(intent);
	}
	@SuppressWarnings("deprecation")
	public Notification creatNotification(SingleSongBean music,boolean isPlaying){
		RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);
		RemoteViews remoteViewsSmall = new RemoteViews(this.getPackageName(), R.layout.notification_small);
		
        String title = music.songName;
        String detail = music.albumName+"_"+music.artistName;
        
        remoteViews.setImageViewResource(R.id.notification_image,R.drawable.album);
        remoteViews.setTextViewText(R.id.music_title, title);
        remoteViews.setTextViewText(R.id.music_detail, detail);
        
        remoteViewsSmall.setImageViewResource(R.id.notification_image,R.drawable.album);
        remoteViewsSmall.setTextViewText(R.id.music_title, title);
        remoteViewsSmall.setTextViewText(R.id.music_detail, detail);        
        

        Intent playIntent = new Intent(NOTIFICATION_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        remoteViews.setImageViewResource(R.id.play, isPlaying? R.drawable.pause : R.drawable.play);       	       
        remoteViews.setOnClickPendingIntent(R.id.play, playPendingIntent);
        
        remoteViewsSmall.setImageViewResource(R.id.play, isPlaying? R.drawable.pause : R.drawable.play);       	       
        remoteViewsSmall.setOnClickPendingIntent(R.id.play, playPendingIntent);

        Intent preIntent = new Intent(NOTIFICATION_PRE);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.play_prev, nextPendingIntent);
        remoteViewsSmall.setOnClickPendingIntent(R.id.play_prev, nextPendingIntent);
        
        Intent nextIntent = new Intent(NOTIFICATION_NEX);
        PendingIntent nextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.play_next, nextPIntent);
        remoteViewsSmall.setOnClickPendingIntent(R.id.play_next, nextPIntent);
        
        Intent cancelIntent = new Intent(NOTIFICATION_CANCEL);
        PendingIntent cancelPIntent = PendingIntent.getBroadcast(this, 0, cancelIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.notification_close, cancelPIntent);
        remoteViewsSmall.setOnClickPendingIntent(R.id.notification_close, cancelPIntent);
				
		Intent intent = new Intent(this, PlayActivity.class);
        //intent.putExtra(Extras.EXTRA_NOTIFICATION, true);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		 if(mNotification == null){
	            NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext())
	            		.setContentIntent(pendingIntent)
	            		.setSmallIcon(R.drawable.album)
	            		.setWhen(System.currentTimeMillis());
	            mNotification = builder.build();
	            mNotification.bigContentView = remoteViews;
	            mNotification.contentView = remoteViewsSmall;
	        }else {
	            mNotification.bigContentView = remoteViews;
	            mNotification.contentView = remoteViewsSmall;
	        }
	        notificationTarget = new NotificationTarget(this, remoteViews, R.id.notification_image, mNotification, id);
	        notificationTargetSmall = new NotificationTarget(this, remoteViewsSmall, R.id.notification_image, mNotification, id);
	        if(music.islocal == 0){
	        	Glide.with(getApplicationContext()).load(Uri.parse(music.album_art)).asBitmap().placeholder(R.drawable.album).into(notificationTarget);
	        	Glide.with(getApplicationContext()).load(Uri.parse(music.album_art)).asBitmap().placeholder(R.drawable.album).into(notificationTargetSmall);
	        } else {
	        	Glide.with(getApplicationContext()).load(music.album_art).asBitmap().placeholder(R.drawable.album).into(notificationTarget);
	        	Glide.with(getApplicationContext()).load(music.album_art).asBitmap().placeholder(R.drawable.album).into(notificationTargetSmall);
	        }

	        return mNotification;
		
	}
	public void updateNotification(SingleSongBean music,boolean isPlaying){
		if(isPlaying){
			if(notifyMode == NOTIFICATION_STOP){
				startForeground(id, creatNotification(music,isPlaying));
				notifyMode = NOTIFICATION_START;
			}else{
				notificationManager.notify(id, creatNotification(music,true));
			}			
		}else{
			if(notifyMode == NOTIFICATION_STOP){
				
			}else{
		        stopForeground(false);
		        notificationManager.notify(id, creatNotification(music,false));				
			}
		}		 
	}
	
	public void cancelNotification() {
        stopForeground(true);
        //mNotificationManager.cancel(hashCode());
        notificationManager.cancel(id);
    }
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if (intent == null || TextUtils.isEmpty(action)) {
                return;
            }
            if(action.equals(NOTIFICATION_PLAY_PAUSE)){
            	mControl.playOrPause();
            	
            }else if(action.equals(NOTIFICATION_PRE)){
            	mControl.pre(mControl.isPlaying());
            }else if(action.equals(NOTIFICATION_NEX)){
            	mControl.next(mControl.isPlaying());
            }else if(action.equals(NOTIFICATION_CANCEL)){
            	notifyMode = NOTIFICATION_STOP;
            	stopForeground(true);
            	 notificationManager.cancel(id);
            	 
            	// notificationManager.cancelAll();
            }

        	
        }
    };
    public class PlayBinder extends MusicControlInterface.Stub {    	
    	private WeakReference<MusicControl> mWeakControl;
    	public PlayBinder(MusicControl control){
    		mWeakControl = new WeakReference<MusicControl>(control);
    	}
    	
		@Override
		public void setSongs(List<SingleSongBean> list, int position)
				throws RemoteException {
			// TODO Auto-generated method stub
			mWeakControl.get().setSongs(list, position);
		}

		@Override
		public void setUiListener(UIChangedListener uiListener)
				throws RemoteException {
			// TODO Auto-generated method stub
			mWeakControl.get().setUiListener(uiListener);
		}

		@Override
		public void setPlayMode(int mode) throws RemoteException {
			// TODO Auto-generated method stub
			mWeakControl.get().setMode(mode);
		}

		@Override
		public void play(int position,boolean isPlaying) throws RemoteException {
			// TODO Auto-generated method stub
			mWeakControl.get().play(position,isPlaying);
		}

		@Override
		public long getCurrentSongId() throws RemoteException {
			// TODO Auto-generated method stub
			return mWeakControl.get().getCurrentSongId();
		}

		@Override
		public void pause() throws RemoteException {
			// TODO Auto-generated method stub
			mWeakControl.get().pause();
		}

		@Override
		public boolean isPlaying() throws RemoteException {
			// TODO Auto-generated method stub
			return mWeakControl.get().isPlaying();
		}

		@Override
		public void playOrPause() throws RemoteException {
			// TODO Auto-generated method stub
			mWeakControl.get().playOrPause();
		}

		@Override
		public int getProgress() throws RemoteException {
			// TODO Auto-generated method stub
			return mWeakControl.get().getProgress();
		}

		@Override
		public int getDuration() throws RemoteException {
			// TODO Auto-generated method stub
			return mWeakControl.get().getDuration();
		}

		@Override
		public SingleSongBean getCurrentSong() throws RemoteException {
			// TODO Auto-generated method stub
			return mWeakControl.get().getCurrentSong();
		}

		@Override
		public void pre() throws RemoteException {
			// TODO Auto-generated method stub
			mWeakControl.get().pre(mControl.isPlaying());
		}

		@Override
		public void next() throws RemoteException {
			// TODO Auto-generated method stub
			mWeakControl.get().next(mControl.isPlaying());
		}

		@Override
		public int getPlayMode() throws RemoteException {
			// TODO Auto-generated method stub
			return mWeakControl.get().getMode();
		}

		@Override
		public List<SingleSongBean> getPlayList() throws RemoteException {
			// TODO Auto-generated method stub
			return mWeakControl.get().getPlayList();
		}

		@Override
		public void saveCurrentState() throws RemoteException {
			// TODO Auto-generated method stub
			 mWeakControl.get().saveCurrentState();
		}

		@Override
		public void deleteAll() throws RemoteException {
			// TODO Auto-generated method stub
			mWeakControl.get().deleteAll();
		}

		@Override
		public void delete(int position) throws RemoteException {
			// TODO Auto-generated method stub
			mWeakControl.get().delete(position);
		}

		@Override
		public int getCurrentPosition() throws RemoteException {
			// TODO Auto-generated method stub
			return mWeakControl.get().getCurrentPosition();
		}

		@Override
		public void seekTo(int position) throws RemoteException {
			// TODO Auto-generated method stub
			mWeakControl.get().seekTo(position);
		}

    }

}
