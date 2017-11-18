package com.h2603953.littleyun.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.h2603953.littleyun.R;

public class ThemeManager {
	public static int getCurrentColor(Context context){
		TypedValue type = new TypedValue();
		Resources.Theme theme = context.getTheme();
		theme.resolveAttribute(R.attr.bg_normal_color, type, true);
		return type.data;		
	}
}
