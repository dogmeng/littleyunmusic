package com.h2603953.littleyun.fragment;

import java.util.ArrayList;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.adapter.AddToFavListRecycleviewAdapter;
import com.h2603953.littleyun.bean.PlayListBean;
import com.h2603953.littleyun.db.task.PlayListInfoTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class AddToFavDialogFragment extends DialogFragment{
	private RecyclerView ryView;
	private LinearLayoutManager layoutManager;
	private ArrayList<PlayListBean> mList = new ArrayList<>();
	private AddToFavListRecycleviewAdapter addToFavAdapter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);	     
		View view = inflater.inflate(R.layout.fragment_addtofav, null);
		ryView = (RecyclerView)view.findViewById(R.id.current_favlist);
		layoutManager = new LinearLayoutManager(getActivity());		
		ryView.setLayoutManager(layoutManager);
		ryView.setHasFixedSize(true);
		reloadAdapter();
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getDialog().setCanceledOnTouchOutside(true);
		// getDialog().getWindow().setLayout(width, 600);
    	WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();

    	params.height = ViewGroup.LayoutParams.MATCH_PARENT;
    	params.width = ViewGroup.LayoutParams.MATCH_PARENT;
      getDialog().getWindow().setAttributes(params);  
	}

	private void reloadAdapter(){
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				mList = PlayListInfoTask.getInstance(getActivity()).loadPlayListInfo();
				return null;
			}
			@Override
	        protected void onPostExecute(Void aVoid) {
				if(mList!=null){
					addToFavAdapter = new AddToFavListRecycleviewAdapter(getActivity(), mList);
					ryView.setAdapter(addToFavAdapter);	
				}
	        }
			
		}.execute();
	}

}
