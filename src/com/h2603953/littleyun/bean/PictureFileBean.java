package com.h2603953.littleyun.bean;

import java.io.File;
import java.util.List;

public class PictureFileBean {
	
	private String filedir;
	private String filename;
	private int count;
	private List<File> images;
	public String getFileDir(){
		return filedir;
	}
	public void setFileDir(String dir){
		filedir = dir;
		int lastIndex = dir.lastIndexOf("/")+1;
		filename = dir.substring(lastIndex);
	}
	public String getFileName(){
		return filename;
	}
	public void setFileName(String name){
		filename = name;
	}
	public int getCount(){
		return count;
	}
	public void setCount(int count){
		this.count = count;
	}
	public void setImages(List<File> list){
		images = list;
	}
	public List<File> getImages(){
		return images;
	}
	

}
