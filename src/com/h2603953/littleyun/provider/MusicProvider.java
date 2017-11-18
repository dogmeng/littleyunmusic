package com.h2603953.littleyun.provider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import com.h2603953.littleyun.service.SingleSongBean;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

public class MusicProvider {
	public static final int DEPEND_ALL = 0;
	public static final int DEPEND_ALBUM = 1;
	public static final int DEPEND_ARTIST = 2;
	public static final int DEPEND_SONGID = 3;
	public static final int ISLOCAL = 0;
	public static final int ISNET = 1;
	
	public static String[] singleSong = {
		Media.IS_MUSIC,Media._ID,Media.ALBUM_ID,Media.ARTIST_ID,Media.TITLE,Media.ALBUM,Media.ARTIST,Media.DATA,Media.DURATION,Media.SIZE,
		Media.DATE_MODIFIED,Media.DATE_ADDED
	};
	public static ArrayList<SingleSongBean> getSingleSong(Context context,String selectId,int Idtype,int isLocal){
		StringBuilder select = new StringBuilder(Media.IS_MUSIC +"=1"+" and " + Media.DURATION + ">= 30000");
		ArrayList<SingleSongBean> songList = new ArrayList<>();
		switch(Idtype){
		case DEPEND_SONGID:
			select.append(" and "+Media._ID + "="+selectId);
			break;
		case DEPEND_ALL:
			break;
		case DEPEND_ALBUM:
			select.append(" and "+Media.ALBUM_ID + "="+selectId);
			break;
		case DEPEND_ARTIST:
			select.append(" and "+Media.ARTIST_ID + "="+selectId);
			break;			
		}
		//網路方法待完成
		return isLocal == ISLOCAL ? queryMedia(context,select.toString(),songList) : queryMedia(context,select.toString(),songList);
	}
	public static ArrayList<SingleSongBean> queryMedia(Context context,String select,ArrayList<SingleSongBean> songList){
		Cursor cursor = context.getContentResolver().query(
                Media.EXTERNAL_CONTENT_URI,singleSong,select,null,Media.DEFAULT_SORT_ORDER);
		if(cursor!=null){
			while(cursor.moveToNext()){
				SingleSongBean singSong = new SingleSongBean();
				singSong.songId = cursor.getLong(cursor.getColumnIndex(Media._ID));
				singSong.albumId = cursor.getLong(cursor.getColumnIndex(Media.ALBUM_ID));
				singSong.artistId = cursor.getLong(cursor.getColumnIndex(Media.ARTIST_ID));
				String songName = cursor.getString(cursor.getColumnIndex(Media.TITLE));
				singSong.songName =  songName== "" ||songName.equals("<unknown>") ? "未知":songName;
				String albumName = cursor.getString(cursor.getColumnIndex(Media.ALBUM));
				singSong.albumName = albumName== ""||albumName.equals("<unknown>")? "未知":albumName;
				String artistName = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
				singSong.artistName = artistName== ""||artistName.equals("<unknown>")? "未知":artistName;
				singSong.songData = cursor.getString(cursor.getColumnIndex(Media.DATA));
				singSong.duration = cursor.getLong(cursor.getColumnIndex(Media.DURATION));
				singSong.size = cursor.getLong(cursor.getColumnIndex(Media.SIZE));
				singSong.album_art = MusicProvider.getAlbumArtUri(singSong.albumId)+"";
				singSong.date = cursor.getLong(cursor.getColumnIndex(Media.DATE_ADDED));
				//0,本地;1,網路
				singSong.islocal = 0;

				songList.add(singSong);
			}
			cursor.close();
		}
		return songList;
	}
	
    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }
    
    public static class MusicComparator implements Comparator<SingleSongBean>{

		@Override
		public int compare(SingleSongBean arg0, SingleSongBean arg1) {
			// TODO Auto-generated method stub
			long time1 = arg0.date;
			long time2 = arg1.date;
			return time1<time2? 1:(time1>time2?-1:0);
		}
    }

}
