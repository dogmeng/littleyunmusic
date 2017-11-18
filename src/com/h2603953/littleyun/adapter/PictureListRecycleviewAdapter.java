package com.h2603953.littleyun.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.adapter.PictureChoseRecycleviewAdapter.ItemView;
import com.h2603953.littleyun.bean.PictureFileBean;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureListRecycleviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	private List<PictureFileBean> list = new ArrayList<>();
	private Context context;
	private File firstFile;
	private List<File> AllPicture;
	public PictureListRecycleviewAdapter(Context context,List<PictureFileBean> list,File firstFile,List<File> AllPicture){
		this.context = context;
		if(list!=null)
		this.list = list;
		this.firstFile = firstFile;
		this.AllPicture=AllPicture;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return list.size()+1;
	}

	@Override
	public void onBindViewHolder(ViewHolder arg0, int arg1) {
		// TODO Auto-generated method stub
		if(arg1 == 0){
			Glide.with(context).load(firstFile).centerCrop().into(((ItemView)arg0).picture);
			((ItemView)arg0).fileTitle.setText("所有圖片");
			((ItemView)arg0).num.setText("");	
		}else{
			PictureFileBean currentBean = list.get(arg1-1);
			Glide.with(context).load(currentBean.getImages().get(0)).centerCrop().into(((ItemView)arg0).picture);
			((ItemView)arg0).fileTitle.setText(currentBean.getFileName());
			((ItemView)arg0).num.setText(currentBean.getCount()+"張");			
		}

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(arg0.getContext());
		return new ItemView(layoutInflater.inflate(R.layout.recyclerview_picturelist, arg0, false));
	}
	class ItemView extends RecyclerView.ViewHolder implements OnClickListener{
		private ImageView picture;
		private TextView fileTitle,num;

		public ItemView(View itemView) {
			super(itemView);
			// TODO Auto-generated constructor stub
			picture = (ImageView)itemView.findViewById(R.id.picture_img);
			fileTitle = (TextView)itemView.findViewById(R.id.picturefile_title);
			num = (TextView)itemView.findViewById(R.id.picturefile_nums);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(getAdapterPosition() == 0){
				pictureChangerListener.changPicture(AllPicture,"所有圖片");
				return;
			}
			if(pictureChangerListener!=null&&list!=null&&list.size()>0&&getAdapterPosition()>0){
				PictureFileBean bean = list.get(getAdapterPosition()-1);
				pictureChangerListener.changPicture(bean.getImages(),bean.getFileName());
			}
			
		}
		
	}
	public interface OnChangePictureListListener{
		void changPicture(List<File> pictures,String name);
	}
	private OnChangePictureListListener pictureChangerListener;
	public void setOnChangePictureListener(OnChangePictureListListener listener){
		pictureChangerListener = listener;
	}

}
