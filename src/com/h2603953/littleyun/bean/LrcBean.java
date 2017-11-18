package com.h2603953.littleyun.bean;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Paint;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.Log;

public class LrcBean implements Comparable<LrcBean>{
	private String content;
	private String strTime;
	private long time;
    public String getstrTime() {
        return strTime;
    }
    public void setTimeStr(String strTime) {
        this.strTime = strTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
	public LrcBean(){
		
	}
	public LrcBean(String strTime,long time,String content){
		this.strTime = strTime;
		this.time = time;
		this.content = content;
	}
    public static final List<LrcBean> praseRows(String lrcLine) {
        if (!lrcLine.startsWith("[") || lrcLine.indexOf("]") != 9) {
            return null;
        }

        int lastIndexOfRightBracket = lrcLine.lastIndexOf("]");

        String content = lrcLine.substring(lastIndexOfRightBracket + 1, lrcLine.length());

        System.out.println("lrcLine=" + lrcLine);
        // -03:33.02--00:36.37-
        String times = lrcLine.substring(0, lastIndexOfRightBracket + 1).replace("[", "-").replace("]", "-");
        String[] timesArray = times.split("-");
        List<LrcBean> lrcRows = new ArrayList<LrcBean>();
        for (String tem : timesArray) {
            if (TextUtils.isEmpty(tem.trim())) {
                continue;
            }
            //[02:34.14][01:07.00]
            try {
            	LrcBean lrcRow = new LrcBean(tem, formatTime(tem), content);
                lrcRows.add(lrcRow);
            } catch (Exception e) {
                Log.w("LrcRow", e.getMessage());
            }
        }
        return lrcRows;
    }
    private static long formatTime(String timeStr) {
        timeStr = timeStr.replace('.', ':');
        String[] times = timeStr.split(":");

        return Integer.valueOf(times[0]) * 60 * 1000
                + Integer.valueOf(times[1]) * 1000
                + Integer.valueOf(times[2]);
    }

	@Override
	public int compareTo(LrcBean arg0) {
		// TODO Auto-generated method stub
		 return (int) (this.time - arg0.time);
	}
    @Override
    public String toString() {
        return "LrcRow [strTime=" + strTime + ", time=" + time + ", content="
                + content + "]";
    }

}
