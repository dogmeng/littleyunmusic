package com.h2603953.littleyun.db.task;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.h2603953.littleyun.bean.PlayListBean;
import com.h2603953.littleyun.bean.PlayListsBean;
import com.h2603953.littleyun.db.MySqLite;
import com.h2603953.littleyun.db.table.PlayListInfo;
import com.h2603953.littleyun.db.table.PlayLists;

public class PlayListsTask {
	private static PlayListsTask sInstance = null;

    private MySqLite myDatabase = null;

    public PlayListsTask(final Context context) {
    	myDatabase = MySqLite.getInstance(context);
    }

    public static final synchronized PlayListsTask getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PlayListsTask(context.getApplicationContext());
        }
        return sInstance;
    }
	public boolean insertIntoTable(PlayListsBean bean) {
		boolean isSucceed = false;
		try {	
			if(bean!=null){
				ContentValues values = new ContentValues(3);
				values.put(PlayLists.PLAYLISTINFO_ID, bean.getPlaylistId());
				values.put(PlayLists.SONGINFO_ID, bean.getSongId());
				values.put(PlayLists.TYPE, bean.getType());
				myDatabase.getWritableDatabase().insert(PlayLists.TABLE_NAME, null, values);
			}			
			Log.i("插入成功", "插入成功");
			isSucceed = true;
		} catch (Exception ex) {
			Log.e("insert into table ", ex.getMessage());
		}
		return isSucceed;
	}
	public boolean insertIntoTable(int playListInfoId,long songId,int type) {
		boolean isSucceed = false;
		try {	
				ContentValues values = new ContentValues(3);
				values.put(PlayLists.PLAYLISTINFO_ID, playListInfoId);
				values.put(PlayLists.SONGINFO_ID, songId);
				values.put(PlayLists.TYPE, type);
				myDatabase.getWritableDatabase().insert(PlayLists.TABLE_NAME, null, values);			
			Log.i("插入成功", "插入成功");
			isSucceed = true;
		} catch (Exception ex) {
			Log.e("insert into table ", ex.getMessage());
		}
		return isSucceed;
	}
	public boolean insertIntoTable(ArrayList<PlayListsBean> playlists) {
		boolean isSucceed = false;
		SQLiteDatabase database = myDatabase.getWritableDatabase();
		database.beginTransaction();
        try {
            for (int i = 0; i < playlists.size(); i++) {
                ContentValues values = new ContentValues(3);
                values.put(PlayLists.PLAYLISTINFO_ID, playlists.get(i).getPlaylistId());
				values.put(PlayLists.SONGINFO_ID, playlists.get(i).getSongId());
				values.put(PlayLists.TYPE, playlists.get(i).getType());
                database.insert(PlayLists.TABLE_NAME, null, values);
            }
            database.setTransactionSuccessful();
            isSucceed = true;
        } finally {
            database.endTransaction();
        }
		return isSucceed;

	}
	public ArrayList<PlayListsBean> loadPlayLists(String[] selectArgs){
		ArrayList<PlayListsBean> beans = new ArrayList<>();
		String select = PlayLists.PLAYLISTINFO_ID +"= ?";
		//(tables,columns,selection(條件1=?and條件2=?),selectionArgs,)
		Cursor cursor = myDatabase.getWritableDatabase().query(PlayLists.TABLE_NAME, null, select, selectArgs, null, null, null);
		while(cursor!=null&&cursor.moveToNext()){
			PlayListsBean bean = new PlayListsBean();
			bean.setId(cursor.getInt(cursor.getColumnIndex(PlayLists.ID)));
			bean.setplaylistId(cursor.getInt(cursor.getColumnIndex(PlayLists.PLAYLISTINFO_ID)));
			bean.setSongId(cursor.getLong(cursor.getColumnIndex(PlayLists.SONGINFO_ID)));
			bean.setType(cursor.getInt(cursor.getColumnIndex(PlayLists.TYPE)));
			beans.add(bean);
		}
		cursor.close();		
		return beans;
		
	}

}
