package org.zhangge.rbplayer.utils;

import android.graphics.Bitmap;
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
	private LruCache<String, Drawable> drawableCache;
	  
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
	        INSTANCE.drawableCache = new LruCache<String, Drawable>(maxSize);
		}
		return INSTANCE;
	}
	
	@SuppressWarnings("deprecation")
	public void loadImage(final String path, final ImageView imageView, int defaultImg) {
		imageView.setTag(path);
		if(drawableCache.get(path) != null) {
			Drawable drawable = drawableCache.get(path);
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
            		Drawable drawable = (Drawable) message.obj;
            		if(path.equals(imageView.getTag())) {
            			imageView.setBackgroundDrawable(drawable);
            		}
            	}
            }
        };
		new Thread(new Runnable() {
			@Override
			public void run() {
				Drawable drawable = Drawable.createFromPath(path);
				if(drawable != null) {
					drawableCache.put(path, drawable);
					Message message = handler.obtainMessage(0, drawable);
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
