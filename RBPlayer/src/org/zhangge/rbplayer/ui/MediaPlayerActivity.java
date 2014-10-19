package org.zhangge.rbplayer.ui;

import org.zhangge.rbplayer.lib.VideoSurfaceView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;

public class MediaPlayerActivity extends Activity {

	private MediaPlayer player;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		try {
			player = new MediaPlayer();
			player.reset();
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setDataSource(Environment.getExternalStorageDirectory() + "/Tongli3D-II/videores/̫ƽ��(00h32m49s-01h05m38s).mp4");
			VideoSurfaceView vsv = new VideoSurfaceView(this, player);
			setContentView(vsv);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		player.pause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		player.stop();
	}
	
	
}
