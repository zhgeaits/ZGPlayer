package org.zhangge.rbplayer.ui;

import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import org.zhangge.rbplayer.R;
import org.zhangge.rbplayer.lib.RBVideoRender;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import org.zhangge.rbplayer.utils.UtilBox;

public class MediaPlayerActivity extends BaseActivity {

	public static String KEY_VIDEO_URL = "key_video_url";
	
	private MediaPlayer player;
    private Button gPlayBtn;
    private Button gModeBtn;
    private TextView gPlayTime;
    private SeekBar gSeekbar;
    private boolean isPlaying = true;
    private String totalTime = null;
    private View playControl;
    private GLSurfaceView videoview;

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
            initView();
            seekBarHandler();
            play(url);
        }
	}

    private void initView() {
        gPlayBtn = (Button) findViewById(R.id.play_btn);
        gModeBtn = (Button) findViewById(R.id.mode);
        gSeekbar = (SeekBar) findViewById(R.id.seekbar);
        gPlayTime = (TextView) findViewById(R.id.playtime);
        playControl = findViewById(R.id.playControl);
        videoview = (GLSurfaceView) findViewById(R.id.videoview);

        gPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying()) {
                    isPlaying = false;
                    player.pause();
                    gPlayBtn.setBackgroundResource(R.drawable.btn_play_selecter);
                } else {
                    isPlaying = true;
                    player.start();
                    seekBarHandler();
                    gPlayBtn.setBackgroundResource(R.drawable.btn_pause_selecter);
                }
            }
        });
        gSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    player.seekTo(progress);
                    setPlayTime();
                }
                if(progress == seekBar.getMax()) {
                    player.seekTo(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        videoview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playControl.getVisibility() == View.VISIBLE) {
                    playControl.setVisibility(View.GONE);
                } else {
                    playControl.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            if(player.isPlaying()) {
                setPlayTime();
                gHandler.postDelayed(updateSeekbar, 500);
            }
        }
    };

    private void setPlayTime() {
        if(totalTime == null) {
            int total = player.getDuration();
            totalTime = UtilBox.formatTime(total);
            gSeekbar.setMax(total);
        }
        int current = player.getCurrentPosition();
        String curTime = UtilBox.formatTime(current);
        gSeekbar.setProgress(current);
        gPlayTime.setText(curTime + "/" + totalTime);
    }

    private void seekBarHandler() {
        gHandler.postDelayed(updateSeekbar, 500);
    }

    private void play(String url) {
        try {
            player = new MediaPlayer();
            player.reset();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if(url != null) {
                player.setDataSource(url);
            }
            videoview.setEGLContextClientVersion(2);
            RBVideoRender mRenderer = new RBVideoRender(this);
            mRenderer.setMediaPlayer(player);
            videoview.setRenderer(mRenderer);
        } catch (Exception e) {
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
