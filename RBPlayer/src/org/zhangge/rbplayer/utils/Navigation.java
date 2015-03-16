package org.zhangge.rbplayer.utils;

import org.zhangge.rbplayer.camera.CameraActivity;
import org.zhangge.rbplayer.ui.MediaPlayerActivity;
import org.zhangge.rbplayer.ui.PhotoActivity;

import android.content.Context;
import android.content.Intent;

public class Navigation {

	public static void toMediaPlayer(Context contex, String url) {
		Intent intent = new Intent(contex, MediaPlayerActivity.class);
		intent.putExtra(MediaPlayerActivity.KEY_VIDEO_URL, url);
		contex.startActivity(intent);
	}
	
	public static void toCameraActivity(Context context, String picPath) {
		Intent intent = new Intent(context, CameraActivity.class);
		intent.putExtra(CameraActivity.KEY_PIC_PATH, picPath);
		context.startActivity(intent);
	}
	
	public static void toPhotoActivity(Context context, String url, String path, int mode) {
		Intent intent = new Intent(context, PhotoActivity.class);
		intent.putExtra(PhotoActivity.URL_KEY, url);
		intent.putExtra(PhotoActivity.PATH_KEY, path);
		intent.putExtra(PhotoActivity.TYPE_KEY, mode);
		context.startActivity(intent);
	}
}
