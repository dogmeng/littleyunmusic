package com.h2603953.littleyun.fragment;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.activity.PlayActivity;
import com.h2603953.littleyun.adapter.RoundMusicViewPagerAdapter;
import com.h2603953.littleyun.animator.BaseAnimator;
import com.h2603953.littleyun.service.PlayerProxy;
import com.h2603953.littleyun.service.SingleSongBean;
import com.h2603953.littleyun.widget.CircleImageView;
import com.h2603953.littleyun.widget.PlayRoundView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import android.app.Activity;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.LinearInterpolator;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class PlayingFragment extends BaseFragment implements OnClickListener,OnTouchListener{

	private ViewPager roundViewPager;
	private LinearLayout bgLayout;
	private RoundMusicViewPagerAdapter viewPagerAdapter;
	private ArrayList<SingleSongBean> list;
	private View currentItem;
	private PlayRoundView triangle;
	private CircleImageView musicImg;
	private int currentPosition;
	private BaseAnimator rotateAnimator;
	private PlayActivity activity;
	
    public static PlayingFragment newInstance(ArrayList<SingleSongBean> list,int currentPosition) {
    	PlayingFragment playingFragment = new PlayingFragment();
    	 Bundle bdl = new Bundle();
         bdl.putParcelableArrayList("CURRENT_LIST", list);
         bdl.putInt("CURRENT_POSITION", currentPosition);
         playingFragment.setArguments(bdl);
    	return playingFragment;
    }

	@Override
	@Deprecated
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = (PlayActivity) activity;
		
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_playroundview, container, false);
		roundViewPager = (ViewPager) view.findViewById(R.id.roundview_viewpager);
		roundViewPager.setOffscreenPageLimit(1);
		bgLayout = (LinearLayout)view.findViewById(R.id.playfragment_bag);
		roundViewPager.setAdapter(viewPagerAdapter);
		//roundViewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics()));
		roundViewPager.setPageTransformer(false, new MyTransformer());
		roundViewPager.setOnTouchListener(this);
		roundViewPager.setCurrentItem(currentPosition);
//		getCurrentView();
		initListener();
		return view;
	}
	@SuppressWarnings("deprecation")
	private void initListener(){
		roundViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				try {
					if(PlayerProxy.getIntance().isPlaying()){
						PlayerProxy.getIntance().play(arg0,true);						
					}else{
						PlayerProxy.getIntance().play(arg0,false);	
					}
					activity.changePager(arg0);
					currentPosition = arg0;
					Log.i("setOnPageChangeListener","onPageSelected");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		bgLayout.setOnClickListener(this);
	}
	public void startRoundViewAnimator(){
		getCurrentView();
		startAnimator();		
	}

	private void getCurrentView(){
		if(viewPagerAdapter!=null){
			currentItem = roundViewPager.findViewWithTag(roundViewPager.getCurrentItem());
			if(currentItem!=null){
				triangle = (PlayRoundView)currentItem.findViewById(R.id.triangleview);
				Log.i("新建时triangle", triangle.toString());
				musicImg  = (CircleImageView)currentItem.findViewById(R.id.music_img);	
			}
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("playingfragment", "onresume");

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bd = getArguments();
		list = bd.getParcelableArrayList("CURRENT_LIST");
		currentPosition = bd.getInt("CURRENT_POSITION");
		if(list!=null){
			viewPagerAdapter = new RoundMusicViewPagerAdapter(getActivity(), list);	
			viewPagerAdapter.setFragment(this);
		}
	}
	
	class MyTransformer implements ViewPager.PageTransformer{
		private static final float MIN_SCALE = 0.7f;
		private static final float MIN_ALPHA = 0.5f;

		@Override
		public void transformPage(View page, float position) {
			// TODO Auto-generated method stub
			if(position<-1||position>1){
				page.setAlpha(MIN_ALPHA);
				page.setScaleX(MIN_SCALE);
				page.setScaleY(MIN_SCALE);
			}else if(position <=1){
				float scaleFactor = Math.max(MIN_SCALE, 1-Math.abs(position));
				if(position<0){
					float scale = 1+0.3f*position;
					page.setScaleX(scale);
					page.setScaleY(scale);
				}else{
					float scale = 1-0.3f*position;
					page.setScaleX(scale);
					page.setScaleY(scale);					
				}
				page.setAlpha(MIN_ALPHA+(scaleFactor-MIN_SCALE)/(1-MIN_SCALE)*(1-MIN_ALPHA));
			}
		}		
	}
	private OnChangeListener changeListener;
	public void setOnChangeListener(OnChangeListener changeListener){
		this.changeListener = changeListener;
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		changeListener.changeFragment();
	}
	private PointF downPoint = new PointF();
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		if(arg0 == roundViewPager){
	        switch (arg1.getAction()) {
            case MotionEvent.ACTION_DOWN:   
            	downPoint.x = arg1.getX();
                downPoint.y = arg1.getY();
                break;
            case MotionEvent.ACTION_UP: 
            	if (PointF.length(arg1.getX() - downPoint.x, arg1.getY()
                        - downPoint.y) < (float) 5.0) {
            		changeListener.changeFragment();
                    return true;
                }
                break;
        }
		}
		return false;
	}
	public void setCurrentPager(List<SingleSongBean> list,int position){
		if(roundViewPager!=null){
			viewPagerAdapter.setList(list);
			roundViewPager.setCurrentItem(position);
			currentPosition = position;
		}
	}
	public void startAnimator(){
		if(triangle == null || musicImg == null){
			Log.i("startAnimator", "triangle == null || musicImg == null");
			return;	
		}
		if((rotateAnimator!=null&&rotateAnimator.isPlaying())||triangle.isAnimatorPlaying()){
			return;
		}
		rotateAnimator = new BaseAnimator(musicImg,0,360) {
			
			@Override
			protected void doAnim(float animatedValue) {
				// TODO Auto-generated method stub
				musicImg.setRotation(animatedValue);
			}
		};
		triangle.setDragAnimator(activity.mScreenWidth/6);
		triangle.setInterpolator(new LinearInterpolator());
		triangle.setStartList();
		triangle.setAnimatorListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub				
				if(triangle!=null){
					Log.i("triangle", triangle.toString());					
					triangle.setStartList();
					triangle.updateRotate();
				}
				super.onAnimationRepeat(animation);

			}

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub				
				triangle.setStartList();
				super.onAnimationStart(animation);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				Log.i("onAnimationEnd", "onAnimationEnd");
				
			}
			
		});
		triangle.startAnimator(2000);
		rotateAnimator.setInterpolator(new LinearInterpolator());
		rotateAnimator.start(20*1000);
	}
	public void endAnimator(){
		if(triangle == null||rotateAnimator == null){
			Log.i("endAnimator时 ", "triangle == null||rotateAnimator == null");
			return;			
		}
		triangle.cancel();
		rotateAnimator.cancel();
		triangle = null;
		rotateAnimator = null;
		musicImg = null;
		
	}
	public void pauseAnimator(){
		if(triangle == null || musicImg == null)return;
		triangle.end();
		rotateAnimator.end();	
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//pauseAnimator();
		Log.i("PlayingFragment","onPause");
		endAnimator();
	}
}
