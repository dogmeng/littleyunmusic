package com.h2603953.littleyun.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
	private int space;
	public SpaceItemDecoration(int space){
		this.space = space;
	}
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
			State state) {
		// TODO Auto-generated method stub
	//	super.getItemOffsets(outRect, view, parent, state);
		outRect.left = space;
		outRect.right = space;
		outRect.bottom = space;
	}
	

}
