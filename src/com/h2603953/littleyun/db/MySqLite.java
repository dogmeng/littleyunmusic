package com.h2603953.littleyun.db;

import com.h2603953.littleyun.db.table.PlayListInfo;
import com.h2603953.littleyun.db.table.PlayLists;
import com.h2603953.littleyun.db.table.SongInfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySqLite extends SQLiteOpenHelper{
	public static final String DATABASENAME = "littleyun.db";
    private static final int VERSION = 1;
    private static MySqLite mySqlite = null;
    
    public static final synchronized MySqLite getInstance(final Context context) {
        if (mySqlite == null) {
        	mySqlite = new MySqLite(context);
        }
        return mySqlite;
    }
    
    //建表語句
    public static final String CREATE_PLAYLISTINFO = "create table if not exists " + PlayListInfo.TABLE_NAME+" ("
    		+PlayListInfo.ID +" integer primary key autoincrement,"
    		+PlayListInfo.NAME +" text,"
    		+PlayListInfo.COUNT +" text,"
    		+PlayListInfo.IMGURL+" text,"
    		+PlayListInfo.TYPE+" integer"
    		+ ");";
    public static final String CREATE_PLAYLISTS = "create table if not exists " + PlayLists.TABLE_NAME+" ("
    		+PlayLists.ID +" integer primary key autoincrement,"
    		+PlayLists.PLAYLISTINFO_ID +" integer,"
    		+PlayLists.SONGINFO_ID +" integer,"
    		+PlayLists.TYPE +" integer"
    		+ ");";
    public static final String CREATE_SONGINFO = "create table if not exists " + SongInfo.TABLE_NAME+" ("
    		+SongInfo.ID +" integer,"
    		+SongInfo.TYPE +" integer"
    		+ ");";

	private MySqLite(Context context) {
		super(context, DATABASENAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql="drop table if exists " + PlayListInfo.TABLE_NAME;
		db.execSQL(sql);
		db.execSQL(CREATE_PLAYLISTINFO);
		db.execSQL(CREATE_PLAYLISTS);
		db.execSQL(CREATE_SONGINFO);
		Log.i("數據庫創建成功","數據庫創建成功");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		switch(oldVersion){
		case 1:
		case 2:
		default:
		}
	}

}
