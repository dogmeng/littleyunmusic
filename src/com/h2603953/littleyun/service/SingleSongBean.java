package com.h2603953.littleyun.service;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SingleSongBean implements Parcelable{
	public static final String SONG_ID = "song_id";
	public static final String ALBUM_ID = "albumid";
	public static final String ARTIST_ID = "artist_id";
	public static final String SONG_NAME = "song_name";
	public static final String ALBUM_NAME = "album_name";
	public static final String ARTIST_NAME = "artist_name";
	public static final String SONG_DATA = "song_data";
	public static final String DURATION = "duration";
	public static final String SIZE = "size";
	public static final String ALBUM_ART = "album_art";
	public static final String DATE ="date";
	public static final String ISLOCAL = "islocal";
	
    public long songId = -1;
    public long albumId = -1;
    public long artistId = -1;
    public String songName;
    public String albumName;
    public String artistName;
    public String songData;
    public long duration;
    public long size; 
    public String album_art;
    public long date;
    public int islocal;

    
	

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();		
		bundle.putLong(SONG_ID, songId);
        bundle.putLong(ALBUM_ID, albumId);
        bundle.putLong(ARTIST_ID, artistId);
        bundle.putString(SONG_NAME, songName);
        bundle.putString(ALBUM_NAME, albumName);
        bundle.putString(ARTIST_NAME, artistName);
        bundle.putString(SONG_DATA, songData);
        bundle.putLong(DURATION, duration);
        bundle.putLong(SIZE, size);
        bundle.putString(ALBUM_ART, album_art);
        bundle.putLong(DATE, date);
        bundle.putInt(ISLOCAL, islocal);
        arg0.writeBundle(bundle);
	}
	
	public static final Creator<SingleSongBean> CREATOR = new Creator<SingleSongBean>() {

		@Override
		public SingleSongBean createFromParcel(Parcel arg0) {
			// TODO Auto-generated method stub
			SingleSongBean song = new SingleSongBean();
            Bundle bundle = arg0.readBundle();            
            song.songId = bundle.getLong(SONG_ID);
            song.albumId = bundle.getLong(ALBUM_ID);
            song.artistId = bundle.getLong(ARTIST_ID);
            song.songName = bundle.getString(SONG_NAME);
            song.albumName = bundle.getString(ALBUM_NAME);
            song.artistName = bundle.getString(ARTIST_NAME);
            song.songData = bundle.getString(SONG_DATA);
            song.duration = bundle.getLong(DURATION);
            song.size = bundle.getLong(SIZE);
            song.album_art = bundle.getString(ALBUM_ART);
            song.date = bundle.getLong(DATE);
            song.islocal = bundle.getInt(ISLOCAL);
            return song;
		}

		@Override
		public SingleSongBean[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return new SingleSongBean[arg0];
		}
		
	};
}
