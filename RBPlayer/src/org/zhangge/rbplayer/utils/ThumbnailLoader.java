package org.zhangge.rbplayer.utils;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.widget.ImageView;

public class ThumbnailLoader {

	private HashMap<String, Bitmap> imageCache = new HashMap<String, Bitmap>();
	private static ThumbnailLoader INSTANCE = null;
	
	private ThumbnailLoader() {}

	public synchronized static ThumbnailLoader getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ThumbnailLoader();
		}
		return INSTANCE;
	}
	
	@SuppressWarnings("deprecation")
	public void loadThumbnail(final String path, final ImageView imageView, int defaultImg) {
		imageView.setTag(path);
		if(imageCache.containsKey(path)) {
			Bitmap bitmap = imageCache.get(path);
			if(bitmap != null) {
				if(path.equals(imageView.getTag())) {
					imageView.setBackground(new BitmapDrawable(bitmap));
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
				imageCache.put(path, bitmap);
				Message message = handler.obtainMessage(0, bitmap);
                handler.sendMessage(message);
			}
		}).start();
	}
}
