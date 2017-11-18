package com.h2603953.littleyun.activity;

import java.util.ArrayList;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.adapter.FragmentViewAdapter;
import com.h2603953.littleyun.bean.EventBusAdapter;
import com.h2603953.littleyun.fragment.CloudMusicFragment;
import com.h2603953.littleyun.fragment.FriendsFragment;
import com.h2603953.littleyun.fragment.MyMusicFragment;
import com.h2603953.littleyun.widget.MoveLine;
import com.h2603953.littleyun.widget.MyToolbar;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;

public class MainActivity extends BaseActivity implements View.OnClickListener{
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		fininshAll();
	}
	private MyToolbar toolbar;
	private DrawerLayout drawerLayout;
	private MoveLine moveLine;
	private ViewPager viewPager;
	private FragmentViewAdapter mViewPagerAdapter;
	private MyMusicFragment myMusicFragment;
	private CloudMusicFragment cloudFragment;
	private FriendsFragment friendFragment;
	private MainViewPagerListener viewPagerScrolledListener;
	private ImageView myMusicTab,cloudTab,friendsTab;
	private ArrayList<ImageView> tabs;
	private int sideWidth,tabWidth,startX,endX,laststartX,lastendX;
	private MarginLayoutParams lp ;
	private int[] tabBgsNormal = {R.drawable.my_music_normal,R.drawable.cloud_music_normal,R.drawable.friends_normal};
	private int[] tabBgsPressed = {R.drawable.my_music_pressed,R.drawable.cloud_music_pressed,R.drawable.friends_pressed};
	public Uri userUri;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent it = getIntent();
		if(it!=null)
		userUri = it.getData();
		initView();		
		setToolBar();
		initFragment();
		initadapter();
		initListener();
	}
	private void initView(){
		toolbar = (MyToolbar)findViewById(R.id.main_toolbar);
		drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer);
		moveLine = (MoveLine)findViewById(R.id.tab_moveline);
		viewPager = (ViewPager)findViewById(R.id.main_viewpager);
		myMusicTab = (ImageView)findViewById(R.id.my_music_img);		
		cloudTab = (ImageView)findViewById(R.id.cloud_music_img);
		friendsTab = (ImageView)findViewById(R.id.friends_img);
		tabs = new ArrayList<>();
		tabs.add(myMusicTab);
		tabs.add(cloudTab);
		tabs.add(friendsTab);
		myMusicTab.setImageResource(R.drawable.my_music_pressed);
		myMusicTab.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				lp = (MarginLayoutParams) myMusicTab.getLayoutParams();
				tabWidth=myMusicTab.getWidth()+lp.leftMargin+lp.rightMargin+myMusicTab.getPaddingLeft()+myMusicTab.getPaddingRight();
				sideWidth = (mScreenWidth - tabWidth*tabs.size())/2;
				laststartX = startX = sideWidth;
				lastendX = endX = sideWidth+tabWidth;
				moveLine.setPosition(laststartX,lastendX, 1);
				setTabAnimator(myMusicTab,"translationY",18);
			}
			
		});
		
	}
	private void setToolBar(){
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");	
	}
	private void initFragment(){
		myMusicFragment = new MyMusicFragment();
		cloudFragment = new CloudMusicFragment();
		friendFragment = new FriendsFragment();
	}
	
	private void initadapter(){
		mViewPagerAdapter = new FragmentViewAdapter(getSupportFragmentManager());
		mViewPagerAdapter.addFragment(myMusicFragment);
		mViewPagerAdapter.addFragment(cloudFragment);
		mViewPagerAdapter.addFragment(friendFragment);
		viewPager.setAdapter(mViewPagerAdapter);
	}
	private void initListener(){
		viewPagerScrolledListener = new MainViewPagerListener();
		viewPager.setOnPageChangeListener(viewPagerScrolledListener);
		myMusicTab.setOnClickListener(this);
		cloudTab.setOnClickListener(this);
		friendsTab.setOnClickListener(this);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        switch (item.getItemId()) {
        case android.R.id.home: //Menu icon
        	drawerLayout.openDrawer(Gravity.LEFT);
            return true;
        case R.id.menu_search:
        	//跳转页面
        default:		
		}
        return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem home = menu.findItem(android.R.id.home);		
		return super.onCreateOptionsMenu(menu);
	}
	
	class MainViewPagerListener implements ViewPager.OnPageChangeListener{
		private int lastPosition = -1;
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			//0:挂起 1:正在滑动 2:滑动完毕
		}

		@Override
		public void onPageScrolled(int arg0, final float arg1, int arg2) {
			// TODO Auto-generated method stub
			//agr0:当前页面 arg1:当前页面偏移百分比 arg2:当前页面偏移的像素位置
			//右滑从1到0,position为小的
			if(arg2!=0&&arg2 < lastPosition){
				if(arg1>0.5f){
					startX = (int) (sideWidth+ tabWidth*(arg0+1)-(tabWidth*(1-arg1)*2));
					endX = (int) (sideWidth+ tabWidth*(arg0+2));
					moveLine.setPosition(startX,endX, (2*arg1-1));
				}else if(arg1<=0.5f){
					startX = (int) (sideWidth+ tabWidth*arg0);
					endX = (int) (sideWidth+ tabWidth*(arg0+2)-(tabWidth*(1-2*arg1)));
					moveLine.setPosition(startX,endX, (1-2*arg1));
				}
			}
			//左滑从0到1 突变为0,position为小的,突变为大的
			if(arg2!=0&&arg2 > lastPosition){
				if(arg1<0.5f){
					startX =(int) (sideWidth+ tabWidth*arg0);
					endX =(int) (sideWidth+tabWidth*(arg0+1)+(tabWidth*arg1*2));
					moveLine.setPosition(startX,endX, (1-2*arg1));
				}else if(arg1>=0.5f){
					endX = (int)(sideWidth+tabWidth*(arg0+2));
					startX =(int) (sideWidth+ tabWidth*arg0+(tabWidth*(2*arg1-1)));
					moveLine.setPosition(startX,endX, (2*arg1-1));
				}
			}
			lastPosition = arg2;
		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			for(int i = 0;i<tabs.size();i++){
				if(i == arg0){
					tabs.get(i).setImageResource(tabBgsPressed[i]);
					setTabAnimator(tabs.get(i),"translationY",18);										
				}else{
					setTabAnimator(tabs.get(i),"translationY",0);
					tabs.get(i).setImageResource(tabBgsNormal[i]);
				}
			}
		}
		
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		for(int i = 0;i<tabs.size();i++){
			if(arg0 == tabs.get(i)){
				viewPager.setCurrentItem(i);
				tabs.get(i).setImageResource(tabBgsPressed[i]);
				setTabAnimator(tabs.get(i),"translationY",18);
			}else{
				tabs.get(i).setImageResource(tabBgsNormal[i]);
				setTabAnimator(tabs.get(i),"translationY",0);
			}
		}
	}
	private void setTabAnimator(View view,String propertyname,float values){
		ObjectAnimator animator = ObjectAnimator.ofFloat(view, propertyname, values);
		animator.setInterpolator(new AnticipateOvershootInterpolator());
		animator.start();
	}
	public void onEvent(EventBusAdapter bus){
		myMusicFragment.changeAdapterList(bus.getData());
	}


}

