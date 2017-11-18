package com.h2603953.littleyun.util;

import java.util.ArrayList;
import java.util.List;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.activity.LocalMusic;
import com.h2603953.littleyun.application.MyApplication;
import com.h2603953.littleyun.widget.CommonPopupWindow;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PermissionUtil {
	public static final int WRITE_EXTERNAL_STORAGE = 200;
	public static final int REQUEST_OPEN_APPLICATION_SETTINGS = 1000;

	private Activity context;
	private PermissionListener listener;
	private TextView cancel,create;
	private CommonPopupWindow mPopupWindow ;
	private List<String> permissionlist;
	private String[] requestPermissions ;
	public PermissionUtil(Object context){
			this.context = getActivity(context);		
	}
    private static Activity getActivity(Object object) {
        if (object != null) {
            if (object instanceof Activity) {
                return (Activity) object;
            } else if (object instanceof Fragment) {
                return ((Fragment) object).getActivity();
            }
        }
        return null;
    }
	//READ_PHONE_STATE
	//WRITE_EXTERNAL_STORAGE Manifest.permission.WRITE_EXTERNAL_STORAGE
	public void requestPermission(final String[] permissions,final int requestCode){
		//先check,在request(分为是否拒绝过)
		if(permissions == null || permissions.length == 0) return;
			if(!isAllGranted(permissions)){
				Log.i("請求權限", "請求權限");
				requestPermissions = getDeniedPermission(permissions);
				if(shouldShowRequestPermissions(requestPermissions)){
					//显示dialog,点击按钮后再request
					showDialog();
					cancel.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							mPopupWindow.dismiss();

						}
					});			
					create.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							ActivityCompat.requestPermissions(context, requestPermissions, requestCode);
							mPopupWindow.dismiss();
						}
					});
				}else{
					ActivityCompat.requestPermissions(context, requestPermissions, requestCode);
					Log.i("ActivityCompat.requestPermissions", "");
				}
			}else{
				Log.i("版本低於6.0", "不用權限");
				if(listener!=null) listener.onPermissionGranted();
			}
		
	}
	public void onRequestPermissionsResult(int arg0,String[] permissions,int[] grantResults){
		permissionlist = new ArrayList<>();
		if(arg0 == WRITE_EXTERNAL_STORAGE){
		getFromResults(permissions,grantResults,permissionlist);
		if(permissionlist.size()>0){
			//显示dialog,点击按钮后再request
			showDialog();
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(listener!=null){
						listener.onPermissionDenied();
					}
					mPopupWindow.dismiss();

				}
			});			
			create.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					startAppSettings();
					mPopupWindow.dismiss();
				}
			});
		}else{
			if(listener!=null) listener.onPermissionGranted();
		}
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_OPEN_APPLICATION_SETTINGS:			
			if (isAllGranted(requestPermissions)) {
				Log.i("onPermissionGranted", "onPermissionGranted");
				if (listener != null) {
					listener.onPermissionGranted();
				}
			} else {
				Log.i("回調onPermissionDenied", "回調onPermissionDenied");
				if (listener != null) {
					listener.onPermissionDenied();
				}
			}
			break;
		}
	}
	public List<String> getFromResults(String[] permissions,int[] grantResults,List<String> permissionlist){		
		for(int i=0; i<grantResults.length;i++){
			if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
				permissionlist.add(permissions[i]);
			}
		}
		return permissionlist;
	}
	public void startAppSettings(){
		Intent it = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		it.setData(Uri.parse("package:"+context.getPackageName()));
		context.startActivityForResult(it, REQUEST_OPEN_APPLICATION_SETTINGS);
	}
	public boolean shouldShowRequestPermissions(String[] permissions){
		for (String permission : permissions) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				return true;
			}
		}
		return false;
	}
	public boolean isAllGranted(String[] permissions) {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
			return true;
		}
//		PackageInfo info;
//		try {
//			info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//			Log.i("當前版本號", info.applicationInfo.targetSdkVersion+"");
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		for (String permission : permissions) {
			if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, permission)) {
				return false;
			}
		}
		return true;
	}
	public void showDialog(){
		mPopupWindow =new CommonPopupWindow.Builder(context)
		.setView(R.layout.pwindow_tips).setHeight(600)			
		.build();
		cancel = (TextView)mPopupWindow.getContentView().findViewById(R.id.tip_cancel);
		create = (TextView)mPopupWindow.getContentView().findViewById(R.id.tip_create);
		mPopupWindow.show(Gravity.CENTER);
	}
	public String[] getDeniedPermission(String[] permissions){
		List<String> permissionlist = new ArrayList<>();
		for(String permission:permissions){
			if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, permission)) {
				permissionlist.add(permission);
			}
		}
		String[] strings = new String[permissionlist.size()];
		for(int i = 0;i<permissionlist.size();i++){
			strings[i] = permissionlist.get(i);
		}
		return strings;
	}
	public void setPerimissionListener(PermissionListener listener){
		this.listener = listener;
	}
	public interface PermissionListener{
		void onPermissionGranted();
		void onPermissionDenied();
	}
}
