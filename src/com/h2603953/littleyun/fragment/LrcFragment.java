package com.h2603953.littleyun.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.h2603953.littleyun.R;
import com.h2603953.littleyun.bean.LrcBean;
import com.h2603953.littleyun.service.SingleSongBean;
import com.h2603953.littleyun.widget.LrcView;

public class LrcFragment extends BaseFragment implements OnClickListener{
    public static LrcFragment newInstance(SingleSongBean currentSong) {
    	LrcFragment lrcFragment = new LrcFragment();
    	 Bundle bdl = new Bundle();
         bdl.putParcelable("CURRENT_SONG", currentSong);
         lrcFragment.setArguments(bdl);
    	return lrcFragment;
    }
    private LrcView lrcView;
    private List<LrcBean> currentLrc;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_lrccontent, container, false);
		lrcView = (LrcView)view.findViewById(R.id.lrccontent);
		lrcView.setData(testBean());
		lrcView.setOnClickListener(this);
		return view;
	}
	
	private List<LrcBean> testBean(){
		currentLrc = new ArrayList<>();
			currentLrc.add(new LrcBean("0",0,"我在人民广场吃炸鸡"));
			currentLrc.add(new LrcBean("1",1,"演唱:阿肆"));
			currentLrc.add(new LrcBean("2",2,"最近你变得很冷漠"));			
			currentLrc.add(new LrcBean("3",3,"让我有些不知所措"));
			currentLrc.add(new LrcBean("4",4,"其实我没期待太多"));
			currentLrc.add(new LrcBean("5",5,"你能像从前般爱我"));
			currentLrc.add(new LrcBean("6",6,"只是连约会你都逃脱"));
			currentLrc.add(new LrcBean("7",7,"什么解释都不说"));
			currentLrc.add(new LrcBean("8",8,"不是我不知道"));
			currentLrc.add(new LrcBean("9",9,"爱情需要煎熬"));
			currentLrc.add(new LrcBean("10",10,"不是我没祈祷"));
			currentLrc.add(new LrcBean("11",11,"我在人民广场吃着炸鸡"));
			currentLrc.add(new LrcBean("12",12,"而此时此刻你在哪里"));
			currentLrc.add(new LrcBean("13",13,"虽然或许你在声东击西"));
			currentLrc.add(new LrcBean("14",14,"但疲倦已让我懒得怀疑"));
			currentLrc.add(new LrcBean("15",15,"我在人民广场吃着炸鸡"));
			currentLrc.add(new LrcBean("16",16,"而此刻你在?"));
		return currentLrc;
	}
	private OnChangeListener changeListener;
	public void setOnChangeListener(OnChangeListener changeListener){
		this.changeListener = changeListener;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		changeListener.changeFragment();
	}

}
