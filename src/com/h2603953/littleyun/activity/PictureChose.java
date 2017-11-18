package com.h2603953.littleyun.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.adapter.PictureChoseRecycleviewAdapter;
import com.h2603953.littleyun.adapter.PictureChoseRecycleviewAdapter.PictureRecycleListener;
import com.h2603953.littleyun.adapter.PictureListRecycleviewAdapter;
import com.h2603953.littleyun.adapter.PictureListRecycleviewAdapter.OnChangePictureListListener;
import com.h2603953.littleyun.bean.PictureFileBean;
import com.h2603953.littleyun.crop.Crop;
import com.h2603953.littleyun.util.ToastUtil;
import com.h2603953.littleyun.widget.CommonPopupWindow;
import com.h2603953.littleyun.widget.MyToolbar;
import com.h2603953.littleyun.widget.SpaceItemDecoration;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PictureChose extends BaseActivity implements OnClickListener,OnChangePictureListListener,PictureRecycleListener{
	private PictureChoseRecycleviewAdapter pictureAdapter;
	private RecyclerView pictureRv,pictureList;
	private TextView pictureTitle,pictureTime;
	private ArrayList<File> AllPicture = new ArrayList<>();
	private ArrayList<PictureFileBean> fileBeans = new ArrayList<>();
	private GridLayoutManager gridLayoutManager;
	private LinearLayoutManager linearLayoutManager;
	private MyToolbar toolbar;
	private CommonPopupWindow picturewindow;
	private PictureListRecycleviewAdapter pictureListAdapter;
	private LinearLayout timebar;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picturechose);
		pictureRv = (RecyclerView)findViewById(R.id.picture_recyclerview);
		pictureTitle = (TextView)findViewById(R.id.picture_title);
		timebar = (LinearLayout)findViewById(R.id.pwindow_time);
		pictureTime = (TextView)findViewById(R.id.picture_time);
		toolbar = (MyToolbar)findViewById(R.id.picture_toolbar);
		setToolBar();
		gridLayoutManager = new GridLayoutManager(this, 3);		
		pictureRv.setLayoutManager(gridLayoutManager);	
		pictureRv.addItemDecoration(new SpaceItemDecoration(5));
		reloadAdapter();
		initListener();
	}
	private void setToolBar(){
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");	
	}

	private void initListener(){
		pictureTitle.setOnClickListener(PictureChose.this);
		
		pictureRv.addOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				// TODO Auto-generated method stub
				super.onScrollStateChanged(recyclerView, newState);
				if(newState !=pictureRv.SCROLL_STATE_IDLE){
					timebar.setVisibility(View.VISIBLE);
				}else{
					timebar.setVisibility(View.GONE);
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				// TODO Auto-generated method stub
				super.onScrolled(recyclerView, dx, dy);
			}
			
		});
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent it = new Intent(PictureChose.this,MainActivity.class);
		startActivity(it);
		finish();
		//super.onBackPressed();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        switch (item.getItemId()) {
        case android.R.id.home:
        	onBackPressed();
            return true;		
		}
        return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void reloadAdapter(){	
		new AsyncTask<Void, Void, Void>(){
			

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					ToastUtil.show("sd卡不可用");
					return;
				}
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				HashSet<String> mFilePaths = new HashSet<>();
				List<File> images = null;
				Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = PictureChose.this.getContentResolver();
				Cursor mCursor = mContentResolver.query(imageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
						+MediaStore.Images.Media.MIME_TYPE+"=?", new String[]{"image/jpeg","image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
				if(mCursor == null){
					return null;
				}
				while(mCursor.moveToNext()){
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
					File firstPath = new File(path);
					File parentFile = firstPath.getParentFile();
						if(parentFile == null){continue;}
							String filePath = parentFile.getAbsolutePath();
							PictureFileBean fileBean = null;
							if(mFilePaths.contains(filePath)){
								continue;
							}else{
								mFilePaths.add(filePath);								
								fileBean = new PictureFileBean();
								fileBean.setFileDir(filePath);
							}
							images =Arrays.asList(parentFile.listFiles(new FilenameFilter() {								
								@Override
								public boolean accept(File dir, String filename) {
									// TODO Auto-generated method stub
									if(filename.endsWith(".jpg")||filename.endsWith(".png")||filename.endsWith(".jpeg")){
										return true;
									}
									return false;
								}
							})) ;
							int picCount = images.size();
							fileBean.setCount(picCount);							
							Collections.sort(images,new FileComparator());
							fileBean.setImages(images);
							AllPicture.addAll(images);
							fileBeans.add(fileBean);
				}				
				mCursor.close();
				mFilePaths = null;								
				return null;
			}
			@Override
	        protected void onPostExecute(Void aVoid) {		
				Collections.sort(AllPicture,new FileComparator());
				pictureAdapter = new PictureChoseRecycleviewAdapter(PictureChose.this,AllPicture);
				pictureAdapter.setOnPictureRecycleListener(PictureChose.this);
				pictureRv.setAdapter(pictureAdapter);
	        }			
		}.execute();
	}
	class FileComparator implements Comparator<File>{

		@Override
		public int compare(File arg0, File arg1) {
			// TODO Auto-generated method stub
			if(arg0.lastModified()<arg1.lastModified()){
				return 1;
			}else if(arg0.lastModified()==arg1.lastModified()){
				return 0;
			}else{
				return -1;				
			}
		}				
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.picture_title:
			if(AllPicture==null||fileBeans==null||AllPicture.size()<=0||fileBeans.size()<=0){
				ToastUtil.show("暫無圖片");
				return;
			}
			picturewindow =new CommonPopupWindow.Builder(this).setView(R.layout.pwindow_picturelist)
							.setWidth(mScreenWidth).setHeight(mScreenHeight/2).setAnimation(R.style.dialog_fragment_animation)
							.setOutsideTouchable(true).build();
			pictureList = (RecyclerView) picturewindow.getContentView().findViewById(R.id.picturelist_recyclerview);
			linearLayoutManager = new LinearLayoutManager(this);
			pictureList.setLayoutManager(linearLayoutManager);
			pictureListAdapter = new PictureListRecycleviewAdapter(this, fileBeans, AllPicture.get(0),AllPicture);
			pictureListAdapter.setOnChangePictureListener(this);
			pictureList.setAdapter(pictureListAdapter);
			picturewindow.show(Gravity.BOTTOM);
			break;
		}
	}
	@Override
	public void changPicture(List<File> pictures,String name) {
		// TODO Auto-generated method stub
		pictureAdapter.setData(pictures);
		pictureTitle.setText(name);
		picturewindow.dismiss();
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
    	if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
    		Intent it = new Intent(PictureChose.this,MainActivity.class);
    		it.setData(Crop.getOutput(result));
    		startActivity(it);
    		finish();
        } else if (resultCode == Crop.RESULT_ERROR) {
        	ToastUtil.show(Crop.getError(result).getMessage());
        }
    }
	@Override
	public void setTime(long time) {
		// TODO Auto-generated method stub
		if(time <= 0)return;
		Date date = new Date(time);
		Log.i("文件時間", format.format(date)+"");
		pictureTime.setText(format.format(date));
		
	}
	                      
	                    

}
