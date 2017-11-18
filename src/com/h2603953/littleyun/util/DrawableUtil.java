package com.h2603953.littleyun.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.h2603953.littleyun.R;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class DrawableUtil {
	
	public static Drawable tintDrawable(Drawable drawable,int colorId){
		Drawable drawablenor = DrawableCompat.wrap(drawable).mutate();
		DrawableCompat.setTint(drawablenor,colorId);
		return drawablenor;		
	}
	
	public static void setTintDrawable(Context context,View view,int drawable){
		Bitmap normal = BitmapFactory.decodeResource(context.getResources(), drawable);
		Drawable drawablenor = new StateDrawable(context.getResources(),normal);
	      int[][] colorStates = new int[][]{
	    		  new int[]{android.R.attr.state_selected},new int[]{android.R.attr.state_pressed},new int[]{}
	      };
	      int[] colors = new int[]{context.getResources().getColor(R.color.toolbarselectedColor),context.getResources().getColor(R.color.toolbarselectedColor),context.getResources().getColor(R.color.toolbarTextNormalColor),};
	      ColorStateList colorlist = new ColorStateList(colorStates, colors);
	      drawablenor = DrawableCompat.wrap(drawablenor.mutate());
	      DrawableCompat.setTintList(drawablenor, colorlist);
	      DrawableCompat.setTintMode(drawablenor, PorterDuff.Mode.SRC_IN);
	      view.setBackgroundDrawable(drawablenor);
	}
	
	public static Bitmap makePressedDrawable(Context context,int normal,int pressedMask,int dWt,int dHt){		
		Bitmap normalBtp = BitmapFactory.decodeResource(context.getResources(), normal);
		Bitmap pressedMaskBtp = BitmapFactory.decodeResource(context.getResources(), pressedMask);
		if(pressedMaskBtp == null || normalBtp == null) return null;
		int presWt = Math.max(dWt, Math.max(normalBtp.getWidth(), pressedMaskBtp.getWidth()));
		int presHt = Math.max(dHt, Math.max(normalBtp.getHeight(),pressedMaskBtp.getHeight()));
		StateListDrawable sld = new StateListDrawable();
		BitmapDrawable normalD = new BitmapDrawable(context.getResources(), normalBtp);
		normalD.setBounds(0,0,dWt,dHt);
		Rect destRect = new Rect(0,0,presWt,presHt);
		Bitmap pressedBitmap = overlayBit(context, normalBtp, pressedMaskBtp, dWt, dHt, destRect,true);
		pressedBitmap = overlayBit(context, normalBtp, pressedMaskBtp, dWt, dHt, destRect,false);
		return pressedBitmap;		
	}
	@SuppressLint("NewApi") 
	public static Bitmap overlayBit(Context context,Bitmap bmp1,Bitmap bmp2,int drawableW,int drawableH,Rect destRect,boolean isPaint){
		int presWt = Math.max(drawableW, Math.max(bmp1.getWidth(), bmp2.getWidth()));
		int presHt = Math.max(drawableH, Math.max(bmp1.getHeight(),bmp2.getHeight()));
		Bitmap bmOverlay;
		if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
			bmOverlay = Bitmap.createBitmap(context.getResources().getDisplayMetrics(),presWt,presHt,bmp1.getConfig());
		}else{
			bmOverlay = Bitmap.createBitmap(presWt, presHt, bmp1.getConfig());
		}
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, null, destRect,null);
		if(isPaint){
			Paint paint =new Paint();
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(bmp2, null, destRect,paint);
			return bmOverlay;
		}				
		canvas.drawBitmap(bmp2, null, destRect,null);
		return bmOverlay;
	}
    public static Bitmap loadCover(Context context,String path,int isLocal) {
    	Bitmap bitmap = null;
    	BitmapFactory.Options  mNewOpts = new BitmapFactory.Options();
        mNewOpts.inSampleSize = 9;
        mNewOpts.inPreferredConfig = Bitmap.Config.RGB_565;
    	if(isLocal == 0){
    		ContentResolver resolver = context.getContentResolver();
    		Uri uri = Uri.parse(path);
            InputStream is;
                try {
					is = resolver.openInputStream(uri);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					return bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.album,mNewOpts);
				}

            Log.i("bitmap为本地", "bitmap为本地");
            bitmap = BitmapFactory.decodeStream(is, null, mNewOpts);            
    	}else if(isLocal == 1){
    		bitmap = BitmapFactory.decodeFile(path, mNewOpts);
    	}
    	if(bitmap == null){
    		Log.i("bitmal为null", "bitmal为null");
    		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.album,mNewOpts);
    	}
    	return bitmap;
    }
    public static Drawable createBlurredImageFromBitmap(Bitmap bitmap, Context context, int inSampleSize) {
    	if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
        RenderScript rs = RenderScript.create(context);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);

        final Allocation input = Allocation.createFromBitmap(rs, blurTemplate);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(8f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);
        bitmap.recycle();
        //After finishing everything, we destroy the Renderscript.
        rs.destroy();
        return new BitmapDrawable(context.getResources(), blurTemplate);
        }else{
        	return new BitmapDrawable(context.getResources(), bitmap);
        }
    }

}
