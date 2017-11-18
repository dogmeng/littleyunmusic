package com.h2603953.littleyun.fragment;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.activity.BaseActivity;
import com.h2603953.littleyun.activity.LocalMusic;
import com.h2603953.littleyun.activity.MainActivity;
import com.h2603953.littleyun.activity.PictureChose;
import com.h2603953.littleyun.adapter.PlaylistRecycleviewAdapter;
import com.h2603953.littleyun.bean.EventBusAdapter;
import com.h2603953.littleyun.bean.PlayListBean;
import com.h2603953.littleyun.crop.CropUtil;
import com.h2603953.littleyun.db.task.PlayListInfoTask;
import com.h2603953.littleyun.util.DrawableUtil;
import com.h2603953.littleyun.util.PermissionUtil;
import com.h2603953.littleyun.util.SharePreferenceManager;
import com.h2603953.littleyun.util.PermissionUtil.PermissionListener;
import com.h2603953.littleyun.util.ToastUtil;
import com.h2603953.littleyun.widget.CircleImageView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class MyMusicFragment extends BaseFragment implements OnClickListener{
	private CircleImageView userImg,localBtn;
	private RecyclerView ry;
	private GridLayoutManager gridLayoutManager;
	private RelativeLayout rL;
	private BitmapFactory.Options mNewOpts;
	private ArrayList<PlayListBean> mList;
	private PlaylistRecycleviewAdapter listAdapter;
	private static Uri uri;
	private SharedPreferences mPreferences;
	private  SharedPreferences.Editor editor;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_mymusic, null);
		userImg  = (CircleImageView)view.findViewById(R.id.user_img);
		localBtn =(CircleImageView)view.findViewById(R.id.local_btn);
		ry = (RecyclerView)view.findViewById(R.id.recyclerview);
		rL = (RelativeLayout)view.findViewById(R.id.user_img_bg);
		mList = new ArrayList<>();
		mPreferences =getActivity().getSharedPreferences("Activity", 0);	
		editor = mPreferences.edit();
		if(uri!=null){
			setUserImage(uri);
			setUserUri(uri);
		}else if(getUserUri()!=null){
			uri = Uri.fromFile(getUserUri());
			setUserImage(uri);
		}else{
			Glide.with(this).load(R.drawable.userimage).skipMemoryCache(true)
			.diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.userimage)
			.into(userImg);
			userImg.setText("wumeng");
			mNewOpts = new BitmapFactory.Options();
	        mNewOpts.inSampleSize = 9;
	        mNewOpts.inPreferredConfig = Bitmap.Config.RGB_565;
			Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.userimage,mNewOpts);
			rL.setBackground(DrawableUtil.createBlurredImageFromBitmap(mBitmap, getActivity(), 9));					
		}	
		gridLayoutManager = new GridLayoutManager(getActivity(), 4);
		ry.setLayoutManager(gridLayoutManager);
		ry.setHasFixedSize(true);
		ry.setItemAnimator(new DefaultItemAnimator());
		reloadAdapter();
		setListener();

		return view;
	}
	
	@Override
	@Deprecated
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if(activity instanceof MainActivity){
			if(((MainActivity) activity).userUri!=null){
				uri = ((MainActivity) activity).userUri;
			}
		}
	}

	public void setUserImage(Uri uri){	
		mNewOpts = new BitmapFactory.Options();
        mNewOpts.inSampleSize = 9;
        mNewOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        InputStream is = null;
        Bitmap mBitmap = null;
        try {
			is = getActivity().getContentResolver().openInputStream(uri);
			mBitmap = BitmapFactory.decodeStream(is, null,mNewOpts);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(mBitmap!=null)
		rL.setBackground(DrawableUtil.createBlurredImageFromBitmap(mBitmap, getActivity(), 9));
		userImg.setText("wumeng");	
		Glide.with(getActivity()).load(uri).skipMemoryCache(true)
		.diskCacheStrategy(DiskCacheStrategy.NONE)
		.into(userImg);	
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("myMusicFragment执行onresume", "");

	}

	private void reloadAdapter(){
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				mList = PlayListInfoTask.getInstance(getActivity()).loadPlayListInfo();
				return null;
			}
			@Override
	        protected void onPostExecute(Void aVoid) {
				listAdapter = new PlaylistRecycleviewAdapter(getActivity(), mList, 0);
				ry.setAdapter(listAdapter);	
	        }
			
		}.execute();
	}
	private void setListener(){
		localBtn.setOnClickListener(this);
		userImg.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.local_btn:
			requestPermission(LocalMusic.class);
			break;
		case R.id.user_img:
			requestPermission(PictureChose.class);
			break;
		}
	}

	public void requestPermission(final Class<?> classes){
		BaseActivity.permissionUtil.setPerimissionListener(new PermissionListener() {					
			@Override
			public void onPermissionGranted() {
				// TODO Auto-generated method stub
				Intent it = new Intent(getActivity(),classes);
				startActivity(it);
				getActivity().finish();
			}					
			@Override
			public void onPermissionDenied() {
				// TODO Auto-generated method stub
				ToastUtil.show("未授權讀取SD卡功能,請在系統設置中開啟后再次使用");
			}
		});
		BaseActivity.permissionUtil.requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtil.WRITE_EXTERNAL_STORAGE);
	}
	public void changeAdapterList(PlayListBean bean){
		listAdapter.addItem(bean);
	}
	public void setUserUri(Uri uri){
		ContentResolver resolver = getActivity().getContentResolver();
		File file = CropUtil.getFromMediaUri(getActivity(), resolver, uri);
		if(file == null){
			Log.i("file==null", "file==null");
			return;
		}
		if(file!=null){
			 editor.putString("user_image",file.getAbsolutePath());
			 editor.commit();			
		}

	}
	public File getUserUri(){
		 String str = mPreferences.getString("user_image", "");
		 if(!TextUtils.isEmpty(str)){
			 return new File(str); 
		 }else{
			 return null;
		 }
		 
	}
}
