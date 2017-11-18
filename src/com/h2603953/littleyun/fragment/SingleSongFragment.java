package com.h2603953.littleyun.fragment;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.SortOrder;

import com.bumptech.glide.Glide;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.activity.BaseActivity;
import com.h2603953.littleyun.adapter.PlaylistRecycleviewAdapter;
import com.h2603953.littleyun.adapter.SonglistRecycleviewAdapter;
import com.h2603953.littleyun.bean.EventBusAdapter;
import com.h2603953.littleyun.bean.EventBusStop;
import com.h2603953.littleyun.bean.PlayListBean;
import com.h2603953.littleyun.db.task.PlayListInfoTask;
import com.h2603953.littleyun.provider.MusicProvider;
import com.h2603953.littleyun.service.PlayerProxy;
import com.h2603953.littleyun.service.SingleSongBean;
import com.h2603953.littleyun.util.PermissionUtil;
import com.h2603953.littleyun.util.PermissionUtil.PermissionListener;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SingleSongFragment extends BaseFragment{
	private ArrayList<SingleSongBean> mList,recentList;
	private RecyclerView ry;
	private LinearLayoutManager layoutManager;
	private AppBarLayout mAppBarLy;
	private RelativeLayout recentAddBar;
	private Toolbar playAllBar;
	private ImageView recentImg;
	private TextView recentTitle;
	private SonglistRecycleviewAdapter songListAdapter;
	

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 View view = inflater.inflate(R.layout.fragment_singlesong, container, false);
		 ry = (RecyclerView)view.findViewById(R.id.single_recyclerview);
		 recentImg=(ImageView)view.findViewById(R.id.recent_songimg);
		 mAppBarLy = (AppBarLayout)view.findViewById(R.id.appbar);
		 recentAddBar = (RelativeLayout)view.findViewById(R.id.recentadd_bar);
		 playAllBar = (Toolbar)view.findViewById(R.id.playall_bar);
		 recentTitle = (TextView)view.findViewById(R.id.recent_songtitle);
		 mList = new ArrayList<>();
		 recentList =new ArrayList<>();
		 layoutManager = new LinearLayoutManager(getActivity());
		 songListAdapter = new SonglistRecycleviewAdapter(getActivity(), mList,true);
		 ry.setLayoutManager(layoutManager);
		 ry.setAdapter(songListAdapter);
		 mAppBarLy.addOnOffsetChangedListener(new OnOffsetChangedListener() {
			 float height;
			 boolean isFirst = true;
			
			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
				// TODO Auto-generated method stub
				if(isFirst){
					height = mAppBarLy.getTotalScrollRange();
					isFirst = false;
				}
				float offset = Math.abs(verticalOffset);				
				float alpha = (offset/height)>=1.0f?1.0f:offset/height;
				float downalpha = (1-2*alpha) <=0.0f?0.0f:(1-2*alpha);
				playAllBar.setAlpha(alpha);
				recentAddBar.setAlpha(downalpha);				
			}
		});
		 reloadAdapter();		 
		// EventBus.getDefault().register(this);
		return view;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 //EventBus.getDefault().unregister(this);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void reloadAdapter(){
		new AsyncTask<Void, Void, Void>(){
			

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();				
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
//				if(isLoadData){
					recentList = MusicProvider.getSingleSong(getActivity(), null, MusicProvider.DEPEND_ALL,0);
					mList = MusicProvider.getSingleSong(getActivity(), null, MusicProvider.DEPEND_ALL,0);
					Collections.sort(recentList,new MusicProvider.MusicComparator());
//				}				
				return null;
			}
			@Override
	        protected void onPostExecute(Void aVoid) {
				if(recentList!=null&&recentList.size()>0){recentTitle.setText(recentList.get(0).songName+"__"+recentList.get(0).artistName);				
				Glide.with(getActivity()).load(Uri.parse(recentList.get(0).album_art)).fitCenter().placeholder(R.drawable.default_background).into(recentImg);}
				if(mList!=null)
				songListAdapter.setList(mList);	
				//ry.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
	        }
			
		}.execute();
	}
	public void onEventMainThread(EventBusStop stop){
		songListAdapter.setSongId(-1);
	}

	public void setSongId(long id){
		songListAdapter.setSongId(id);
	}

}
