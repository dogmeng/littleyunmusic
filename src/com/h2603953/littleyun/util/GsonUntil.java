package com.h2603953.littleyun.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class GsonUntil {
	private static GsonUntil gsonUntil;
	private static Gson gson;

	private GsonUntil(){
		gson = new Gson();
		
	}
	public static GsonUntil getInstance(){
		if(gsonUntil == null){
			synchronized (GsonUntil.class) {
				if(gsonUntil == null){
					gsonUntil = new GsonUntil();
				}
			}
		}
		return gsonUntil;
	}
	
	 public <T> T GsonToBean(String gsonString, Class<T> cls) {
	        T t = null;
	        if (gson != null) {
	            t = gson.fromJson(gsonString, cls);
	        }
	        return t;
	    }

	public <T> List<T> GsonToList(String gsonString, Class<T> cls) {
		List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString, new ListOfSomething<T>(cls));
        }
        return list;
    }
	
	public <T> List<T> jsonToList(String json, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for(final JsonElement elem : array){
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }
	public <T> String objectTojson(T data){
		if(data == null){
			return "";
		}
		String str = "";
		if (gson != null) {
			str = gson.toJson(data);
        }
		return str;
	}
	


    class ListOfSomething<X> implements ParameterizedType {  
    	        private Class<?> wrapped;  
    	          
    	        public ListOfSomething(Class<X> wrapped) {  
    	            this.wrapped = wrapped;  
    	        }  
    	        public Type[] getActualTypeArguments() {  
    	          return new Type[] {wrapped};  
    	        }  
    	        public Type getRawType() {  
    	            return List.class;  
    	        }  
    	       public Type getOwnerType() {  
    	    	   return null;  
    	       	}  
    }

}
