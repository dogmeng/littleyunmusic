package com.h2603953.littleyun.util;

import com.h2603953.littleyun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {
    private static Context mContext;
    private static Toast mToast;
    private static TextView tv;
    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static void show(String text) {
        if (mToast == null) {
        	mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        	View view = LayoutInflater.from(mContext).inflate(R.layout.toast_view, null);
        	tv = (TextView)view.findViewById(R.id.toast_tv);
        	tv.setText(text);
        	mToast.setView(view);
        } else {
        	tv.setText(text);
        }
        mToast.show();
    }
}
