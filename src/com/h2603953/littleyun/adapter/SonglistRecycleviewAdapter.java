package com.h2603953.littleyun.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.activity.BaseActivity;
import com.h2603953.littleyun.activity.LocalMusic;
import com.h2603953.littleyun.activity.MainActivity;
import com.h2603953.littleyun.activity.PlayActivity;
import com.h2603953.littleyun.service.PlayerProxy;
import com.h2603953.littleyun.service.SingleSongBean;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SonglistRecycleviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	private static final int ITEM_SINGLE_SONG = 0;
	private static final int ITEM_BOTTOM = 1;
	private Context context;
	private List<SingleSongBean> song;
	private long mCurrentSongId = -1;
	private boolean isFirst = true,isMsg = true;
	public SonglistRecycleviewAdapter(Context context,List<SingleSongBean> song,boolean isMsg){
		this.context = context;
		this.song = song;
		this.isMsg = isMsg;
		try {
			mCurrentSongId = PlayerProxy.getIntance().getCurrentSongId();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setList(List<SingleSongBean> song){
		if(song == null){
			this.song = new ArrayList<>();
		}else{
			this.song = song;			
		}
		
		notifyDataSetChanged();
	}
	public void setSongId(long id){
		mCurrentSongId = id;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		if(song!=null){
			return song.size()+1;
		}
		return 1;
		
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub		
		if(song!=null&&song.size()>0 &&position<song.size()&& song.get(position) instanceof SingleSongBean){
			return ITEM_SINGLE_SONG;
		}
		if(song!=null&&position == song.size()){
			return ITEM_BOTTOM;
		}
		return super.getItemViewType(position);
	}

	@Override
	public void onBindViewHolder(ViewHolder arg0, final int arg1) {
		// TODO Auto-generated method stub
		if(getItemViewType(arg1)==ITEM_SINGLE_SONG && song!=null){
			SingleSongBean singleSong = song.get(arg1);
			((ItemHolder)arg0).title.setText(singleSong.songName);
			((ItemHolder)arg0).songDetail.setText(singleSong.artistName + "__"+singleSong.albumName);
			if(isMsg){
				if(singleSong.islocal == 0){
					Glide.with(context).load(Uri.parse(singleSong.album_art)).placeholder(R.drawable.album).fitCenter().into(((ItemHolder)arg0).songImg);	
				}
				((ItemHolder)arg0).songMore.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}else{
				((ItemHolder)arg0).songMore.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						song.remove(arg1);						
						notifyDataSetChanged();
						Log.i("移除項目", "移除項目"+arg1);
						try {
							PlayerProxy.getIntance().delete(arg1);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			if(mCurrentSongId == singleSong.songId){
				((ItemHolder)arg0).songCurrent.setVisibility(View.VISIBLE);
			}else{
				((ItemHolder)arg0).songCurrent.setVisibility(View.GONE);
			}
			
		}
		
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub		
		if(arg1 == ITEM_SINGLE_SONG)
		return new ItemHolder(LayoutInflater.from(arg0.getContext()).inflate(R.layout.recyclerview_songlist, arg0, false));
		else if(arg1 == ITEM_BOTTOM) return new Bottom(LayoutInflater.from(arg0.getContext()).inflate(R.layout.recyclerview_singlesongbottom, arg0, false));
		else return null;
	}
	class ItemHolder extends RecyclerView.ViewHolder implements OnClickListener{
		public ImageView songImg,songMore,songCurrent;
		public TextView title,songDetail;

		public ItemHolder(View view) {
			super(view);
			// TODO Auto-generated constructor stub
			songImg = (ImageView)view.findViewById(R.id.song_img);
			songMore = (ImageView)view.findViewById(R.id.song_more);
			songCurrent = (ImageView)view.findViewById(R.id.song_current);
			title = (TextView)view.findViewById(R.id.song_title);
			songDetail =(TextView)view.findViewById(R.id.song_detail);
			if(!isMsg){
				songImg.setVisibility(View.GONE);
				songMore.setImageResource(R.drawable.delete);
			}
			view.setOnClickListener(this);
			
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub			

			int position= getAdapterPosition();
			try {			
				List<SingleSongBean> list = PlayerProxy.getIntance().getPlayList();
			if(isFirst ||list==null||list.size()<=0||list.size()!=song.size()){
				Log.i("第一次設置列表", "第一次設置列表");
					BaseActivity.isNow = true;
					PlayerProxy.getIntance().setSongs(song, position);
					isFirst = false;
				}else{
					if(mCurrentSongId == song.get(position).songId){
						if(context instanceof LocalMusic){
							PlayActivity.startPlayActivity(context,"localMusic");
							((LocalMusic) context).finish();
						}
						if(context instanceof MainActivity){
							PlayActivity.startPlayActivity(context,"mainActivity");
							((MainActivity) context).finish();
						}
				}else{
					Log.i("不是第一次設置列表", "不是第一次設置列表");
					PlayerProxy.getIntance().play(position,true);
				}
					 }
			}
			catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
	class Bottom  extends RecyclerView.ViewHolder{

		public Bottom(View arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}
		
	}

}
