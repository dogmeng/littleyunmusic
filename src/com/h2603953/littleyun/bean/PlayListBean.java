package com.h2603953.littleyun.bean;

public class PlayListBean {
	private int id;
	private String name;
	private String imgurl;
	private String count;
	private int type;
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return id;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public void setImgurl(String imgurl){
		this.imgurl = imgurl;
	}
	public String getImgurl(){
		return imgurl;
	}
	public void setCount(String count){
		this.count = count;
	}
	public String getCount(){
		return count;
	}
	public void setType(int type){
		this.type = type;
	}
	public int getType(){
		return type;
	}
	public PlayListBean(String name,String count,String imgurl,int type){
		this.name = name;		
		this.count = count;
		this.imgurl = imgurl;
		this.type = type;
	}
	public PlayListBean(){}

}
