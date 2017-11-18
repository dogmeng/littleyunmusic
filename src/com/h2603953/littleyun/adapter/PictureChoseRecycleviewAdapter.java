package com.h2603953.littleyun.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.adapter.AddToFavListRecycleviewAdapter.ItemView;
import com.h2603953.littleyun.crop.Crop;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PictureChoseRecycleviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	private Activity context;
	private List<File> mList = new ArrayList<File>();
	private PictureRecycleListener picListener;
	public PictureChoseRecycleviewAdapter(Activity context,List<File> mList){
		this.context = context;
		if(mList!=null)
		this.mList = mList;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}
	public void setData(List<File> mList){
		if(mList!=null)
		this.mList = mList;
		notifyDataSetChanged();
	}
	@Override
	public void onBindViewHolder(ViewHolder arg0, int arg1) {
		// TODO Auto-generated method stub
		File picture = mList.get(arg1);
		Glide.with(context).load(picture).centerCrop().into(((ItemView)arg0).pictureIv);
		if(picListener!=null)			
		picListener.setTime(picture.lastModified());
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(arg0.getContext());
		 return new ItemView(layoutInflater.inflate(R.layout.recyclerview_chosepic, arg0, false));
	}
	class ItemView extends RecyclerView.ViewHolder implements OnClickListener{
		private ImageView pictureIv;

		public ItemView(View itemView) {
			super(itemView);
			// TODO Auto-generated constructor stub
			pictureIv = (ImageView)itemView.findViewById(R.id.chosepic);
			pictureIv.setOnClickListener(this);
			
		}
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			File currentpic = mList.get(getAdapterPosition());
			beginCrop( Uri.fromFile(currentpic));
		}
	    private void beginCrop(Uri source) {
	        Uri destination = Uri.fromFile(new File(context.getCacheDir(), "cropped"));
	        Crop.of(source, destination).asSquare().start(context);
	    }
	}
	public interface PictureRecycleListener{
		void setTime(long time);
	}
	public void setOnPictureRecycleListener(PictureRecycleListener listener){
		picListener = listener;
	}

}
