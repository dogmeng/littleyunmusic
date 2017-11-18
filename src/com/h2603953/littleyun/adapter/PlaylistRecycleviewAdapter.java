package com.h2603953.littleyun.adapter;



import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.bean.EventBusAdapter;
import com.h2603953.littleyun.bean.PlayListBean;
import com.h2603953.littleyun.db.task.PlayListInfoTask;
import com.h2603953.littleyun.widget.CircleImageView;
import com.h2603953.littleyun.widget.CommonPopupWindow;
import com.h2603953.littleyun.widget.LoginEditText;

import de.greenrobot.event.EventBus;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PlaylistRecycleviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	 public int TYPE_ITEM = 0;
     public int TYPE_FOOTER = 1;
     private ArrayList<PlayListBean> mList;
     private List mShowList;
     private int line;
     private Context context;
     private static final int CREATE_PLAYLIST = 1;
     
     public PlaylistRecycleviewAdapter(Context context,ArrayList<PlayListBean> mList,int line){
    	 this.mList = mList;
    	 this.context = context;
    	 mShowList = new ArrayList();
    	 initShowList();
 	 
     }
     public void addItem(PlayListBean bean){
    	 mList.add(0, bean);
    	 initShowList();
    	 notifyDataSetChanged();    	 
     }
     private void initShowList(){    	 
				if(mList.size()>3){
					mShowList = mList.subList(0, 3);					
				}else{
					mShowList = mList;
				}
     }

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mShowList.size()+1;
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		 if (position == mShowList.size()) {
             return TYPE_FOOTER;
         }
         return TYPE_ITEM;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		 if (viewType == TYPE_ITEM) {
             return new ItemView(layoutInflater.inflate(R.layout.recyclerview_playlist, parent, false));
         } else {
             return new Footer(layoutInflater.inflate(R.layout.recyclerview_playlist, parent,false));
         }
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		if(getItemViewType(position) == TYPE_ITEM){			
				PlayListBean data = (PlayListBean)mShowList.get(position);
				if(data == null){
					return;
				}
				((ItemView)holder).playlistImg.setText(data.getName());				
				String imgurl = data.getImgurl();
				if(data.getType() == 0){
					Glide.with(context).load(Uri.parse(imgurl)).placeholder(R.drawable.album).centerCrop().into(((ItemView)holder).playlistImg);
				}else{
					Glide.with(context).load(imgurl).placeholder(R.drawable.album).centerCrop().into(((ItemView)holder).playlistImg);
				}
				
				((ItemView)holder).playlistImg.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub						
					}
				});
			}
			
		}

	class Footer extends RecyclerView.ViewHolder implements View.OnClickListener{
		private CircleImageView add;
		private TextView cancel,create;
		private LoginEditText edt;
        public Footer(View itemView) {
            super(itemView);
            add = (CircleImageView) itemView.findViewById(R.id.createplaylist);
            add.setIsColorFilter(true);
            add.setImageResource(R.drawable.add);
            add.setText("新建播放列表");
            add.setOnClickListener(this);
        }
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//popuwindow
			final CommonPopupWindow mPopupWindow =new CommonPopupWindow.Builder(context)
			.setView(R.layout.pwindow_createplaylist).setHeight(600)			
			.build();
			mPopupWindow.show(Gravity.CENTER);
			cancel = (TextView)mPopupWindow.getContentView().findViewById(R.id.popup_cancel);
			create = (TextView)mPopupWindow.getContentView().findViewById(R.id.popup_create);
			edt = (LoginEditText)mPopupWindow.getContentView().findViewById(R.id.popup_edt);
			edt.setBtnEnable(create);
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					mPopupWindow.dismiss();
				}
			});
			
				create.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String name = edt.getText().toString();
						PlayListBean bean = new PlayListBean(name, "0", "",0);
						PlayListInfoTask.getInstance(context).insertIntoTable(bean);
						addItem(bean);
//						EventBusAdapter bus = new EventBusAdapter();
//						bus.setData(bean);
//						EventBus.getDefault().post(bus);
						mPopupWindow.dismiss();
						
					}
				});
			}

    }
    class ItemView extends RecyclerView.ViewHolder {
        private CircleImageView playlistImg;

        public ItemView(View itemView) {
            super(itemView);
            playlistImg = (CircleImageView) itemView.findViewById(R.id.createplaylist);
            playlistImg.setIsColorFilter(false);
        }
    }




}
