package com.h2603953.littleyun.fragment;

import java.util.List;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.activity.PlayActivity;
import com.h2603953.littleyun.adapter.SonglistRecycleviewAdapter;
import com.h2603953.littleyun.bean.EventBusMsg;
import com.h2603953.littleyun.bean.EventBusStop;
import com.h2603953.littleyun.service.MusicControl;
import com.h2603953.littleyun.service.PlayerProxy;
import com.h2603953.littleyun.service.SingleSongBean;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class CurrentPlayListDialogFragment extends DialogFragment implements OnClickListener{
	private int x,y,width,height;
	private PlayActivity context;
	private TextView playMode,playNums,deleteAll,addTofav;
	private List<SingleSongBean> mPlayList;

	private int mode;
	private Drawable left;
	private RecyclerView ryView;
	private LinearLayoutManager layoutManager;
	private SonglistRecycleviewAdapter songListAdapter;
	private AddToFavDialogFragment addToFavFragment;

	public CurrentPlayListDialogFragment(Context context,int x,int y,int width,int height,List<SingleSongBean> mPlayList,int mode){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.mPlayList = mPlayList;
		this.mode = mode;
	}
	

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if(activity instanceof PlayActivity){
			context = (PlayActivity) activity;
		}
		
	}


	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
     
		View view = inflater.inflate(R.layout.fragment_currentplaylist, null);
		playMode = (TextView)view.findViewById(R.id.tv_playmode);
		playNums = (TextView)view.findViewById(R.id.tv_playnumbers);
		deleteAll = (TextView)view.findViewById(R.id.tv_deleteall);
		addTofav = (TextView)view.findViewById(R.id.tv_addtofavourite);
		ryView = (RecyclerView)view.findViewById(R.id.current_playlist);
		layoutManager = new LinearLayoutManager(getActivity());		
		songListAdapter = new SonglistRecycleviewAdapter(getActivity(), mPlayList,false);
		ryView.setLayoutManager(layoutManager);
		ryView.setAdapter(songListAdapter);
		ryView.setHasFixedSize(true);	
		initView();
		playMode.setOnClickListener(this);
		deleteAll.setOnClickListener(this);
		addTofav.setOnClickListener(this);
		EventBus.getDefault().register(this);
		return view;
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getDialog().setCanceledOnTouchOutside(true);
		// getDialog().getWindow().setLayout(width, 600);
    	WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//    	params.x = x;
//    	params.y = 500;
    	params.height = height;
    	params.width = width;
    	Log.i("params.x",x+"");
    	Log.i("params.y",y+"");
    	Log.i("params.height",height+"");
    	Log.i("params.width",width+"");
      getDialog().getWindow().setAttributes(params);  
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDatePickerDialog);
	}
	private void initView(){
		playNums.setText("("+mPlayList.size()+")");
		switch(mode){
		case MusicControl.MODE_AUTO:
			setCurrentState(R.string.mode_random,R.drawable.mode_random);
			break;
		case MusicControl.MODE_NONE:
			setCurrentState(R.string.mode_none,R.drawable.mode_none);
			break;
		case MusicControl.MODE_REPEAT:
			setCurrentState(R.string.mode_repeat,R.drawable.mode_repeat);
			break;				
		}
	}
	private void setCurrentState(int mode,int drawable){
		playMode.setText(mode);
		left =getResources().getDrawable(drawable);
		//必須調用此方法,否則不顯示圖片
		left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
		Log.i("leftDrawable", left.toString());
		playMode.setCompoundDrawablePadding(10);
		playMode.setCompoundDrawables(left, null, null, null);
	}
	public  void onEventMainThread(EventBusMsg msg) throws RemoteException{
		songListAdapter.setSongId(PlayerProxy.getIntance().getCurrentSongId());
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(EventBusStop stop){
		songListAdapter.setList(null);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.tv_playmode:
			switch(mode){
			case MusicControl.MODE_AUTO:
				mode = MusicControl.MODE_REPEAT;
				setCurrentState(R.string.mode_repeat,R.drawable.mode_repeat);
				break;
			case MusicControl.MODE_NONE:
				mode = MusicControl.MODE_AUTO;
				setCurrentState(R.string.mode_random,R.drawable.mode_random);
				break;
			case MusicControl.MODE_REPEAT:
				mode = MusicControl.MODE_NONE;
				setCurrentState(R.string.mode_none,R.drawable.mode_none);
				break;				
			}
			if(context!=null){
				context.setSrcMode(mode);				
			}
			try {
				PlayerProxy.getIntance().setPlayMode(mode);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.tv_addtofavourite:
			addToFavFragment = new AddToFavDialogFragment();
			addToFavFragment.show(getFragmentManager(), "addToFavFragment");
			break;
		case R.id.tv_deleteall:
			try {
				Log.i("清空","清空");
				PlayerProxy.getIntance().deleteAll();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}

}
	
