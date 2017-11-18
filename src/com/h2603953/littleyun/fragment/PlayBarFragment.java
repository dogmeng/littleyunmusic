package com.h2603953.littleyun.fragment;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.activity.BaseActivity;
import com.h2603953.littleyun.activity.PlayActivity;
import com.h2603953.littleyun.bean.EventBusMsg;
import com.h2603953.littleyun.service.MusicControl;
import com.h2603953.littleyun.service.PlayerProxy;
import com.h2603953.littleyun.service.SingleSongBean;
import com.h2603953.littleyun.util.SharePreferenceManager;
import com.h2603953.littleyun.util.ToastUtil;
import com.h2603953.littleyun.widget.MusicProgressBar;

import de.greenrobot.event.EventBus;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayBarFragment extends BaseFragment implements OnClickListener{
//    private static PlayBarFragment fragment;
	private ImageView pause,pre,nex,musiclist,albumImg;
	private TextView songName,singerName;
	private MusicProgressBar progressbar;
	private CurrentPlayListDialogFragment playListDialog;
	private ArrayList<SingleSongBean> mPlayList;
	private int mode;
	private int x,y,height;
	private SingleSongBean currentSong;

    public static PlayBarFragment newInstance() {
    	//这样会不显示内容
//    	if(fragment == null){
//    		fragment = new PlayBarFragment();
//    	}
//        return fragment;
    	return new PlayBarFragment();
    }

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final View view = inflater.inflate(R.layout.fragment_playbar, container, false);
		progressbar = (MusicProgressBar)view.findViewById(R.id.play_progressbar);
		songName = (TextView)view.findViewById(R.id.tv_songtitle);
		singerName = (TextView)view.findViewById(R.id.tv_singername);
		pre = (ImageView)view.findViewById(R.id.play_prev);
		pause = (ImageView)view.findViewById(R.id.play_pause);
		nex = (ImageView)view.findViewById(R.id.play_next);
		musiclist = (ImageView)view.findViewById(R.id.play_list);
		albumImg = (ImageView)view.findViewById(R.id.img_album);
		Log.i("playbarfragment的oncreateview", "playbarfragment的oncreateview");
		view.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				x = 0;
				height = (int) (((BaseActivity)getActivity()).mScreenHeight*0.7);
				Log.i("getActivity().mScreenHeight", ((BaseActivity)getActivity()).mScreenHeight+"");
				Log.i("view.getHeight()", view.getHeight()+"");
				y =((BaseActivity)getActivity()).mScreenHeight -(view.getHeight()+height);
			}
			
		});
		albumImg.setOnClickListener(this);
		pause.setOnClickListener(this);
		pre.setOnClickListener(this);
		nex.setOnClickListener(this);
		musiclist.setOnClickListener(this);		
		Log.i("重新创建barfragment","");
		return view;
	}
    @Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			if(PlayerProxy.getIntance().isPlaying()){
		    	setInitState();
		    	progressbar.post(mUpdateProgress);
		    	pause.setImageResource(R.drawable.pause);
			}else{
				setInitState();
				pause.setImageResource(R.drawable.play);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.play_pause:
			try {				
				PlayerProxy.getIntance().playOrPause();	
				pause.setImageResource(PlayerProxy.getIntance().isPlaying()? R.drawable.pause:R.drawable.play);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.play_prev:
			try {
				PlayerProxy.getIntance().pre();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			break;
		case R.id.play_next:
			try {
				PlayerProxy.getIntance().next();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.play_list:
			
			if(mPlayList!= null){
				playListDialog = new CurrentPlayListDialogFragment(getActivity(), x, y, LayoutParams.MATCH_PARENT, height,mPlayList,mode);
				playListDialog.show(getFragmentManager(), "playListDialogFragment");
			}else{
				ToastUtil.show("暫無播放列表");
			}	
			break;
		case R.id.img_album:
			Intent it = new Intent(getActivity(),PlayActivity.class);
			Bundle data = new Bundle();
			try {
				int currentPosition = PlayerProxy.getIntance().getCurrentPosition();
				data.putInt("currentSong", currentPosition);
				if(mPlayList != null){
					Log.i("存入list", "存入list");
					data.putParcelableArrayList("playlist", mPlayList);
				}				
				it.putExtras(data);
				getActivity().startActivity(it);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		reloadAdapter();
	}

	public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {
            try {
	            int position = PlayerProxy.getIntance().getProgress();
	            	progressbar.setProgress(position);
				if (PlayerProxy.getIntance().isPlaying()) {
					progressbar.postDelayed(mUpdateProgress, 1000);
				}else {
					progressbar.removeCallbacks(mUpdateProgress);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
    };
    public void startProgress() throws RemoteException{
    	if(progressbar!=null){
        	setInitState();
        	if(PlayerProxy.getIntance().isPlaying()){
            	progressbar.post(mUpdateProgress);
            	pause.setImageResource(R.drawable.pause);        		
        	}
    	}    	
    }
//    public  void onEvent(EventBusMsg msg) throws RemoteException{
//       	setInitState();
//    	progressbar.post(mUpdateProgress);
//    	pause.setImageResource(R.drawable.pause);
//    }
    public void setSongId(long id){
    	
    }
	public void stopProgress(){
		pause.setImageResource(R.drawable.play);
		progressbar.removeCallbacks(mUpdateProgress);
	}

	private void setInitState() throws RemoteException{
		currentSong = PlayerProxy.getIntance().getCurrentSong();
		if(currentSong!=null){
		progressbar.setMax((int) currentSong.duration);
		progressbar.setProgress(PlayerProxy.getIntance().getProgress());
		songName.setText(currentSong.songName);
		singerName.setText(currentSong.artistName);
		Glide.with(this).load(Uri.parse(currentSong.album_art)).placeholder(R.drawable.album).fitCenter().into(albumImg);
		reloadAdapter();
		}else{
			Log.i("存儲歌曲為空", "存儲歌曲為空");
		}
	}
	private void reloadAdapter(){
		new AsyncTask<Void, Void, Void>(){
			
			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				try {
					mPlayList = (ArrayList<SingleSongBean>) PlayerProxy.getIntance().getPlayList();
					mode = PlayerProxy.getIntance().getPlayMode();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				return null;
					
			}			
		}.execute();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	
	}
	

}
