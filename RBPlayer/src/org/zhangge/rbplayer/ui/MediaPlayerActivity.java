package org.zhangge.rbplayer.ui;

import org.zhangge.rbplayer.R;
import org.zhangge.rbplayer.lib.RBVideoRender;
import org.zhangge.rbplayer.lib.RBVideoSurfaceView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;

public class MediaPlayerActivity extends Activity {

	public static String KEY_VIDEO_URL = "key_video_url";
	
	private MediaPlayer player;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_video_player);
		if(getIntent().getExtras() != null) {
			String url = getIntent().getExtras().getString(KEY_VIDEO_URL);
            try {
                player = new MediaPlayer();
                player.reset();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if(url != null) {
                    player.setDataSource(url);
                }
                GLSurfaceView videoview = (GLSurfaceView) findViewById(R.id.videoview);
                videoview.setEGLContextClientVersion(2);
                RBVideoRender mRenderer = new RBVideoRender(this);
                mRenderer.setMediaPlayer(player);
                videoview.setRenderer(mRenderer);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	protected void onPause() {
		super.onPause();
		player.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		player.stop();
        player.release();
	}
	
}
