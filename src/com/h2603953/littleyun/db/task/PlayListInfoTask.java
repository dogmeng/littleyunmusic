package com.h2603953.littleyun.db.task;

import java.util.ArrayList;
import java.util.List;

import com.h2603953.littleyun.bean.PlayListBean;
import com.h2603953.littleyun.db.MySqLite;
import com.h2603953.littleyun.db.table.PlayListInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PlayListInfoTask {
	private static PlayListInfoTask sInstance = null;

    private MySqLite myDatabase = null;

    public PlayListInfoTask(final Context context) {
    	myDatabase = MySqLite.getInstance(context);
    }

    public static final synchronized PlayListInfoTask getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PlayListInfoTask(context.getApplicationContext());
        }
        return sInstance;
    }
    
	public boolean insertIntoTable(PlayListBean bean) {
		boolean isSucceed = false;
//		String columns = PlayListInfo.NAME+","+PlayListInfo.COUNT+","+PlayListInfo.IMGURL;
		try {			
//			String sql = "insert into " + PlayListInfo.TABLE_NAME + "(" + columns + ")"
//					+ " values(?,?,?)";			
//			myDatabase.getWritableDatabase().execSQL(sql,new String[]{"wumeng","1","1"});
			if(bean!=null){
				ContentValues values = new ContentValues();
				values.put(PlayListInfo.NAME, bean.getName());
				values.put(PlayListInfo.COUNT, bean.getCount());
				values.put(PlayListInfo.IMGURL, bean.getImgurl());
				values.put(PlayListInfo.TYPE, bean.getType());
				myDatabase.getWritableDatabase().insert(PlayListInfo.TABLE_NAME, null, values);
			}			
			Log.i("插入成功", "插入成功");
			isSucceed = true;
		} catch (Exception ex) {
			Log.e("insert into table ", ex.getMessage());
		}
		return isSucceed;
	}
	public ArrayList<PlayListBean> loadPlayListInfo(){
		ArrayList<PlayListBean> beans = new ArrayList<>();
		Cursor cursor = myDatabase.getWritableDatabase().query(PlayListInfo.TABLE_NAME, null, null, null, null, null, null);
		while(cursor!=null&&cursor.moveToNext()){
			PlayListBean bean = new PlayListBean();
			bean.setId(cursor.getInt(cursor.getColumnIndex(PlayListInfo.ID)));
			bean.setName(cursor.getString(cursor.getColumnIndex(PlayListInfo.NAME)));
			bean.setCount(cursor.getString(cursor.getColumnIndex(PlayListInfo.COUNT)));
			bean.setImgurl(cursor.getString(cursor.getColumnIndex(PlayListInfo.IMGURL)));
			bean.setType(cursor.getInt(cursor.getColumnIndex(PlayListInfo.TYPE)));
			beans.add(0,bean);
		}
		cursor.close();		
		return beans;
		
	}
    public void update(int playListInfoid, String count,String url,int type) {

        SQLiteDatabase database = myDatabase.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues(3);
            values.put(PlayListInfo.COUNT, count);
            values.put(PlayListInfo.IMGURL,url);
            values.put(PlayListInfo.TYPE, type);
            database.update(PlayListInfo.TABLE_NAME, values, PlayListInfo.ID + " = ?" , new String[]{playListInfoid + ""});
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

    }

}
