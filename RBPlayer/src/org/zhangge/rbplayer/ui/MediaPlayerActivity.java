package org.zhangge.rbplayer.ui;

import org.zhangge.almightyzgbox_android.log.ZGLog;
import org.zhangge.almightyzgbox_android.ui.bar.VerticalSeekBar;
import org.zhangge.rbplayer.lib.RBTextureRender;
import org.zhangge.rbplayer.lib.RBVideoRender;
import org.zhangge.rbplayer.lib.RBVideoRender.OnPlayGoing;
import org.zhangge.rbplayer.utils.AdUtils;
import org.zhangge.rbplayer.utils.UtilBox;

import org.zhangge.rbplayerpro.R;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MediaPlayerActivity extends BaseActivity {

	public static String KEY_VIDEO_URL = "key_video_url";
	public static int SCREEN_MODE_NORMAL = 0;
	private static final int OFFSET_MAX = 30;

	private MediaPlayer gPlayer;
	private Button gPlayBtn;
	private Button gModeBtn;
	private TextView gPlayTime;
	private SeekBar gSeekbar;
	private boolean gShouldPlaying = false;
	private String gTotalTime = null;
	private View gPlayControl;
	private VerticalSeekBar gOffsetSeekbar;
	private GLSurfaceView gVideoview;
	private RBVideoRender gRenderer;
	private int gScreenMode;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_video_player);
		
		gScreenMode = SCREEN_MODE_NORMAL;
		if (getIntent().getExtras() != null) {
			String url = getIntent().getExtras().getString(KEY_VIDEO_URL);
			initView();
			play(url);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setScreenMode(gScreenMode);
		super.onConfigurationChanged(newConfig);
	}

	private void initView() {
		gPlayBtn = (Button) findViewById(R.id.play_btn);
		gModeBtn = (Button) findViewById(R.id.mode);
		gSeekbar = (SeekBar) findViewById(R.id.seekbar);
		gPlayTime = (TextView) findViewById(R.id.playtime);
		gPlayControl = findViewById(R.id.playControl);
		gVideoview = (GLSurfaceView) findViewById(R.id.videoview);
		gOffsetSeekbar = (VerticalSeekBar) findViewById(R.id.offset_seekbar);
		gOffsetSeekbar.setMax(OFFSET_MAX * 2);
		gOffsetSeekbar.setProgress(OFFSET_MAX);

		gPlayBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (gPlayer.isPlaying()) {
					gPlayer.pause();
					gPlayBtn.setBackgroundResource(R.drawable.btn_play_selecter);
					AdUtils.displayInterstitial();
				} else {
					gPlayer.start();
					seekBarHandler();
					gPlayBtn.setBackgroundResource(R.drawable.btn_pause_selecter);
				}
			}
		});
		gSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					gPlayer.seekTo(progress);
					setPlayTime();
				}
				if (progress == seekBar.getMax()) {
					gPlayer.seekTo(0);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		gVideoview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (gPlayControl.getVisibility() == View.VISIBLE) {
					gPlayControl.setVisibility(View.GONE);
				} else {
					gPlayControl.setVisibility(View.VISIBLE);
				}
			}
		});
		gModeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (gRenderer.getMode() == RBTextureRender.MODE_NORMAL) {
					gRenderer.setMode(RBTextureRender.MODE_ONE);
					gModeBtn.setBackgroundResource(R.drawable.btn_3d);
					gOffsetSeekbar.setVisibility(View.VISIBLE);
				} else {
					gRenderer.setMode(RBTextureRender.MODE_NORMAL);
					gModeBtn.setBackgroundResource(R.drawable.btn_2d);
					gOffsetSeekbar.setVisibility(View.GONE);
				}
			}
		});
		gOffsetSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				gRenderer.setOffset(progress - OFFSET_MAX);
			}
		});
	}

	private void setPlayTime() {
		if (gTotalTime == null) {
			int total = gPlayer.getDuration();
			gTotalTime = UtilBox.formatTime(total);
			gSeekbar.setMax(total);
		}
		int current = gPlayer.getCurrentPosition();
		String curTime = UtilBox.formatTime(current);
		gSeekbar.setProgress(current);
		gPlayTime.setText(curTime + "/" + gTotalTime);
	}

	private void seekBarHandler() {
		gHandler.postDelayed(updateSeekbar, 500);
	}

	private void play(String url) {
		try {
			gPlayer = new MediaPlayer();
			gPlayer.reset();
			gPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			gPlayer.setDataSource(url);
			gVideoview.setEGLContextClientVersion(2);
			gRenderer = new RBVideoRender(this);
			gRenderer.setMediaPlayer(gPlayer);
			gRenderer.setOnPlayGoing(playGoing);
			gVideoview.setRenderer(gRenderer);
		} catch (Exception e) {
			ZGLog.error(this, e);
		}
	}

	private void setVideoViewSize(int width, int height) {
		try {
			gVideoview.getLayoutParams().width = width;
			gVideoview.getLayoutParams().height = height;
		} catch (NullPointerException e) {
			Log.e("", "");
		}
	}

	private void setScreenMode(int mode) {
		int[] screenSizes = UtilBox.getScreenSize(this);
		int videoWidth = gPlayer.getVideoWidth();
		int videoHeight = gPlayer.getVideoHeight();
		double screenAspect = screenSizes[0] / (double) screenSizes[1];
		double videoAspect = videoWidth / (double) videoHeight;
		int dstWidth = 0;
		int dstHeight = 0;
		if (mode == SCREEN_MODE_NORMAL) {
			if (videoAspect - screenAspect > 0) {
				dstWidth = screenSizes[0];
				dstHeight = (int) (videoHeight * (dstWidth / (double) videoWidth));
			} else {
				dstHeight = screenSizes[1];
				dstWidth = (int) (videoWidth * (dstHeight / (double) videoHeight));
			}
		}
		setVideoViewSize(dstWidth, dstHeight);
	}

	private Runnable updateSeekbar = new Runnable() {
		@Override
		public void run() {
			if (gPlayer.isPlaying()) {
				setPlayTime();
				gHandler.postDelayed(updateSeekbar, 500);
			}
		}
	};

	private OnPlayGoing playGoing = new OnPlayGoing() {

		@Override
		public void doPlayStuff() {
			gShouldPlaying = true;
			gPlayer.start();
			seekBarHandler();
		}

	};

	@Override
	public void onPause() {
		super.onPause();
		gPlayer.pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		AdUtils.addInterstitialAd(this);
		if (!gPlayer.isPlaying() && gShouldPlaying) {
			gPlayBtn.setBackgroundResource(R.drawable.btn_pause_selecter);
			gPlayer.start();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		gPlayer.stop();
		gPlayer.release();
	}

}
