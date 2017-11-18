package com.h2603953.littleyun.bean;

public class PlayListsBean {
	private int id;
	private int playlist_id;
	private long song_id;
	private int type;
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return id;
	}
	public void setplaylistId(int id){
		this.playlist_id = id;
	}
	public int getPlaylistId(){
		return playlist_id;
	}
	public void setSongId(long id){
		this.song_id = id;
	}
	public long getSongId(){
		return song_id;
	}
	public void setType(int type){
		this.type = type;
	}
	public int getType(){
		return type;
	}
	public PlayListsBean(int playlist_id,long songId,int type){	
		this.playlist_id = playlist_id;
		this.song_id = songId;
		this.type = type;
	}
	public PlayListsBean(){}

}
