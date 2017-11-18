package com.h2603953.littleyun.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.animator.BaseAnimator;
import com.h2603953.littleyun.fragment.PlayingFragment;
import com.h2603953.littleyun.service.PlayerProxy;
import com.h2603953.littleyun.service.SingleSongBean;
import com.h2603953.littleyun.widget.CircleImageView;
import com.h2603953.littleyun.widget.PlayRoundView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView.FindListener;

public class RoundMusicViewPagerAdapter extends PagerAdapter{
	private Context context;
	private List<SingleSongBean> list;
	private LayoutInflater inflater;
	private CircleImageView musicImg;
	private PlayRoundView roundView;
	private PlayingFragment pf;

	public RoundMusicViewPagerAdapter(Context context,List<SingleSongBean> list){
		Log.i("RoundMusicViewPagerAdapter", "RoundMusicViewPagerAdapter 初始化");
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}
	public void setList(List<SingleSongBean> list){
		if(list!=null){
			this.list = list;
			Log.i("RoundMusicViewPagerAdapter", "setList");
			notifyDataSetChanged();
		}
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	public void setFragment(PlayingFragment pf){
		this.pf = pf;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		Log.i("destroyItem", "destroyItem"+position);
		container.removeView((View)object);
	}
private int firstposition = -1;

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		super.setPrimaryItem(container, position, object);	
		//待优化
		Log.i("setPrimaryItem", position+"");
		musicImg = (CircleImageView) ((View) object).findViewById(R.id.music_img);
		roundView =(PlayRoundView)((View) object).findViewById(R.id.triangleview);
		SingleSongBean data = list.get(position);
		String imgurl = data.album_art;
		if(data.islocal == 0){
			Log.i("musicImg==islocal", "setPrimaryItem");
			Glide.with(context).load(Uri.parse(imgurl)).placeholder(R.drawable.album).fitCenter().into(musicImg);
		}else{
			Log.i("musicImg==notlocal", "setPrimaryItem");
			Glide.with(context).load(imgurl).placeholder(R.drawable.album).fitCenter().into(musicImg);
		}
		roundView.setVisibility(View.VISIBLE);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_roundview, container,false);
		view.setTag(position);	
		Log.i("instantiateItem",position+"");
		container.addView(view);
		try {
			SingleSongBean data = list.get(position);
		if((PlayerProxy.getIntance().getCurrentSongId() == data.songId)&&PlayerProxy.getIntance().isPlaying()){
			Log.i("RoundMusicViewPagerAdapter","startRoundViewAnimator");
			pf.startRoundViewAnimator();
		}
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return view;
	}	

}
