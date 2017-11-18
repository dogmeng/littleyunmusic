package com.h2603953.littleyun.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.adapter.FragmentViewAdapter;
import com.h2603953.littleyun.fragment.FriendsFragment;
import com.h2603953.littleyun.fragment.MyMusicFragment;
import com.h2603953.littleyun.fragment.SingleSongFragment;
import com.h2603953.littleyun.fragment.TestFragment;
import com.h2603953.littleyun.service.PlayerProxy;
import com.h2603953.littleyun.widget.MyToolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class LocalMusic extends BaseActivity{
	private MyToolbar toolbar;
	private TabLayout tablayout;
	private ViewPager viewPager;
//	private String[] tabs={"單曲","歌手","專輯","文件夾","作曲家"};
	private String[] tabs={"單曲","歌手","專輯"};
	private SingleSongFragment singleSongFragment;
	private TestFragment testFragment;
	private FriendsFragment friendFragment;
	private FragmentViewAdapter mViewPagerAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_localmusic);
		initView();
		setToolBar();
		initFragment();
		initadapter();				
	}
	private void initView(){
		toolbar = (MyToolbar)findViewById(R.id.localmusic_toolbar);
		tablayout = (TabLayout)findViewById(R.id.tabs);
		viewPager = (ViewPager)findViewById(R.id.viewpager);		
		
	}
	private void setToolBar(){
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");	
	}
	private void initFragment(){
		singleSongFragment = new SingleSongFragment();
		friendFragment = new FriendsFragment();
		testFragment = new TestFragment();
	}
	private void initadapter(){
		mViewPagerAdapter = new FragmentViewAdapter(getSupportFragmentManager());
		mViewPagerAdapter.addFragment(singleSongFragment);
		mViewPagerAdapter.addFragment(friendFragment);
		mViewPagerAdapter.addFragment(testFragment);


		viewPager.setAdapter(mViewPagerAdapter);
		tablayout.setupWithViewPager(viewPager);
		//setupWithViewPager會清除掉tab,所以需在此方法后添加tab;
		//另可以在viewpager的adapter中設置tab,適配器為adapter包中的TitleFragmentviewadapter
		for(int i = 0;i<tabs.length;i++){
			tablayout.getTabAt(i).setText(tabs[i]);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.localmusic, menu);				
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        switch (item.getItemId()) {
        case android.R.id.home:
        	onBackPressed();
            return true;
        case R.id.search_localmusic:
        	//跳转页面
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
	@Override
	protected boolean onPrepareOptionsPanel(View view, Menu menu) {
		// TODO Auto-generated method stub
		if(menu!=null){
			if(menu.getClass().getSimpleName().equals("MenuBuilder")){
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
		return super.onPrepareOptionsPanel(view, menu);
	}
	@Override
	public void onMusicStart() {
		// TODO Auto-generated method stub
		super.onMusicStart();
		if(singleSongFragment == null)return;
		try {
			long id = PlayerProxy.getIntance().getCurrentSongId();			
			singleSongFragment.setSongId(id);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onMusicPause() {
		// TODO Auto-generated method stub
		super.onMusicPause();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent it = new Intent(LocalMusic.this,MainActivity.class);
		startActivity(it);
		finish();
		//super.onBackPressed();
	}
	

}
