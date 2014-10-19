package org.zhangge.rbplayer.utils;

import org.zhangge.rbplayer.ui.MediaPlayerActivity;

import android.content.Context;
import android.content.Intent;

public class Navigation {

	public static void toMediaPlayer(Context contex, String url) {
		Intent intent = new Intent(contex, MediaPlayerActivity.class);
		intent.putExtra(MediaPlayerActivity.KEY_VIDEO_URL, url);
		contex.startActivity(intent);
	}
}
