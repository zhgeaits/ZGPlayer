package org.zhangge.rbplayer.utils;

import org.zhangge.almightyzgbox_android.utils.CommonUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class ImageLoader {

	private LruCache<String, Bitmap> imageCache;
	  
	private static ImageLoader INSTANCE = null;
	
	private ImageLoader() {}

	public synchronized static ImageLoader getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ImageLoader();
			int maxSize = 32 * 1024 * 1024;  
			INSTANCE.imageCache = new LruCache<String, Bitmap>(maxSize) {  
	            @Override  
	            protected int sizeOf(String key, Bitmap bitmap) {  
	                return bitmap.getRowBytes() * bitmap.getHeight();  
	            }  
	        };
		}
		return INSTANCE;
	}
	
	@SuppressWarnings("deprecation")
	public void loadImage(final String path, final ImageView imageView, int defaultImg) {
		imageView.setTag(path);
		if(imageCache.get(path) != null) {
			Drawable drawable = new BitmapDrawable(imageCache.get(path));
			if(drawable != null) {
				if(path.equals(imageView.getTag())) {
					imageView.setBackground(drawable);
				}
				return;
			}
		}
		imageView.setBackgroundResource(defaultImg);
		final Handler handler = new Handler() {
            public void handleMessage(Message message) {
            	if(message.obj != null) {
            		Drawable drawable = new BitmapDrawable((Bitmap) message.obj);
            		if(path.equals(imageView.getTag())) {
            			imageView.setBackgroundDrawable(drawable);
            		}
            	}
            }
        };
		new Thread(new Runnable() {
			@Override
			public void run() {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path, options);
				int reqWidth = 240;
				int reqHeight = 220;
				options.inSampleSize = CommonUtils.calculateInSampleSize(options, reqWidth, reqHeight);
				options.inJustDecodeBounds = false;
				Bitmap bitmap = BitmapFactory.decodeFile(path, options);
				
				if(bitmap != null) {
					imageCache.put(path, bitmap);
					Message message = handler.obtainMessage(0, bitmap);
					handler.sendMessage(message);
				}
			}
		}).start();
	}
	
	@SuppressWarnings("deprecation")
	public void loadThumbnail(final String path, final ImageView imageView, int defaultImg) {
		imageView.setTag(path);
		if(imageCache.get(path) != null) {
			Drawable drawable = new BitmapDrawable(imageCache.get(path));
			if(drawable != null) {
				if(path.equals(imageView.getTag())) {
					imageView.setBackground(drawable);
				}
				return;
			}
		}
		imageView.setBackgroundResource(defaultImg);
		final Handler handler = new Handler() {
            public void handleMessage(Message message) {
            	if(message.obj != null) {
            		Drawable drawable = new BitmapDrawable((Bitmap) message.obj);
            		if(path.equals(imageView.getTag())) {
            			imageView.setBackgroundDrawable(drawable);
            		}
            	}
            }
        };
		new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, Images.Thumbnails.MINI_KIND);
				if(bitmap != null) {
					imageCache.put(path, bitmap);
					Message message = handler.obtainMessage(0, bitmap);
					handler.sendMessage(message);
				}
			}
		}).start();
	}
}
