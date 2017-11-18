package com.h2603953.littleyun.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.h2603953.littleyun.R;
import com.h2603953.littleyun.activity.BaseActivity;
import com.h2603953.littleyun.activity.MainActivity;
import com.h2603953.littleyun.adapter.PlaylistRecycleviewAdapter.ItemView;
import com.h2603953.littleyun.adapter.SonglistRecycleviewAdapter.ItemHolder;
import com.h2603953.littleyun.bean.EventBusAdapter;
import com.h2603953.littleyun.bean.PlayListBean;
import com.h2603953.littleyun.bean.PlayListsBean;
import com.h2603953.littleyun.db.task.PlayListInfoTask;
import com.h2603953.littleyun.db.task.PlayListsTask;
import com.h2603953.littleyun.provider.MusicProvider;
import com.h2603953.littleyun.service.PlayerProxy;
import com.h2603953.littleyun.service.SingleSongBean;
import com.h2603953.littleyun.util.ToastUtil;
import com.h2603953.littleyun.widget.CommonPopupWindow;
import com.h2603953.littleyun.widget.LoginEditText;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AddToFavListRecycleviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	 public int TYPE_ITEM = 0;
     public int TYPE_HEADER = 1;
	private Activity context;
	private List<PlayListBean> mShowList;
	public AddToFavListRecycleviewAdapter(Activity context,ArrayList<PlayListBean> mList){
		this.context = context;
		mShowList = mList;
	}
    public void addItem(PlayListBean bean){
    	mShowList.add(0, bean);
   	 	notifyDataSetChanged();   	 
    }

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mShowList.size()+1;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		 if (position == 0) {
             return TYPE_HEADER;
         }
         return TYPE_ITEM;

	}
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		if(getItemViewType(position) == TYPE_ITEM){
			PlayListBean data = (PlayListBean)mShowList.get(position-1);
			((ItemView)holder).title.setText(data.getName());	
			((ItemView)holder).number.setText(data.getCount());	
			if(data.getType() == 0){
				Glide.with(context).load(Uri.parse(data.getImgurl())).placeholder(R.drawable.album).fitCenter().into(((ItemView)holder).itemImg);
			}else{
				Glide.with(context).load(data.getImgurl()).placeholder(R.drawable.album).fitCenter().into(((ItemView)holder).itemImg);
			}
		}		
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		 if (viewType == TYPE_ITEM) {
            return new ItemView(layoutInflater.inflate(R.layout.recyclerview_addtofavlist, parent, false));
        } else {
            return new Header(layoutInflater.inflate(R.layout.recyclerview_addtofavlist, parent,false));
        }
	}
	class Header extends RecyclerView.ViewHolder implements View.OnClickListener{
		private ImageView add;
		private TextView title,number,cancel,create;
		private LoginEditText edt;
        public Header(View itemView) {
            super(itemView);
            add = (ImageView) itemView.findViewById(R.id.favlist_img);
            title = (TextView)itemView.findViewById(R.id.favlist_title);
            number = (TextView)itemView.findViewById(R.id.favlist_number);
            title.setText("新建歌單");
            number.setVisibility(View.GONE);
            add.setImageResource(R.drawable.add);
            itemView.setOnClickListener(this);
        }
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//popuwindow
			final CommonPopupWindow mPopupWindow =new CommonPopupWindow.Builder(context)
			.setView(R.layout.pwindow_createplaylist).setHeight(600)			
			.build();
			//mPopupWindow.show(Gravity.CENTER);
			mPopupWindow.showAtLocation(itemView, Gravity.CENTER, 0, -600);
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
						EventBusAdapter bus = new EventBusAdapter();
						bus.setData(bean);
						EventBus.getDefault().post(bus);
						mPopupWindow.dismiss();
					}
				});
			}

    }
	class ItemView extends RecyclerView.ViewHolder implements View.OnClickListener{
		private ImageView itemImg;
		private TextView title,number;
		private List<SingleSongBean> songs;
		public ItemView(View itemView) {
			super(itemView);
			// TODO Auto-generated constructor stub
			itemImg = (ImageView) itemView.findViewById(R.id.favlist_img);
            title = (TextView)itemView.findViewById(R.id.favlist_title);
            number = (TextView)itemView.findViewById(R.id.favlist_number);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			 reloadAdapter();
		}
		private void reloadAdapter(){
			new AsyncTask<Void, Void, Boolean>(){
				SingleSongBean singsong;
				ArrayList<PlayListsBean> playLists;

				@Override
				protected Boolean doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					boolean isInsert= false;
					try {
						songs = PlayerProxy.getIntance().getPlayList();
						if(songs!=null){
							PlayListsBean playList;
							playLists = new ArrayList<>();
							int playListId = mShowList.get(getAdapterPosition()-1).getId();
							for(int i = 0;i<songs.size();i++){
								playList = new PlayListsBean(playListId,songs.get(i).songId,songs.get(i).islocal);
								playLists.add(playList);						
							}
							isInsert = PlayListsTask.getInstance(context).insertIntoTable(playLists);
							if(isInsert){
								playLists = PlayListsTask.getInstance(context).loadPlayLists(new String[]{playListId+""});
								
								playList = playLists.get(0);
								ArrayList<SingleSongBean> song = 
								MusicProvider.getSingleSong(context, playList.getSongId()+"", MusicProvider.DEPEND_SONGID, playList.getType());
								if(song!=null&&song.size()>0){
									singsong = song.get(0);
									PlayListInfoTask.getInstance(context).update(playListId, playLists.size()+"",singsong.album_art,singsong.islocal);
								}															
							}
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return isInsert;
				}
				@Override
		        protected void onPostExecute(Boolean aVoid) {
					if(aVoid && singsong!=null &&playLists!=null){
						if(singsong.islocal == 0){
							Log.i("本地照片", "");
							Glide.with(context).load(Uri.parse(singsong.album_art)).placeholder(R.drawable.album).fitCenter().into(itemImg);	
						}
						number.setText(playLists.size()+"");
						ToastUtil.show("添加成功");
					}else{
						ToastUtil.show("添加失敗");
					}

		        }
				
			}.execute();
		}
		
	}

}
