package org.zhangge.rbplayer.ui;

import org.zhangge.rbplayer.R;
import org.zhangge.rbplayer.lib.RBTextureRender;
import org.zhangge.rbplayer.lib.RBVideoRender;
import org.zhangge.rbplayer.lib.RBVideoRender.OnPlayGoing;
import org.zhangge.rbplayer.utils.UtilBox;

import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MediaPlayerActivity extends BaseActivity {

	public static String KEY_VIDEO_URL = "key_video_url";
	public static int SCREEN_MODE_NORMAL = 0;
	
	private MediaPlayer player;
    private Button gPlayBtn;
    private Button gModeBtn;
    private TextView gPlayTime;
    private SeekBar gSeekbar;
    private boolean shouldPlaying = false;
    private String totalTime = null;
    private View playControl;
    private GLSurfaceView videoview;
    private RBVideoRender mRenderer;
    private int screenMode;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_video_player);
		screenMode = SCREEN_MODE_NORMAL;
		if(getIntent().getExtras() != null) {
			String url = getIntent().getExtras().getString(KEY_VIDEO_URL);
            initView();
            play(url);
        }
	}
    

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
    	setScreenMode(screenMode);
//    	if(Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation) {
//    		System.out.println("ORIENTATION_LANDSCAPE");
//    	} else if(Configuration.ORIENTATION_PORTRAIT == newConfig.orientation) {
//    		System.out.println("ORIENTATION_PORTRAIT");
//    		videoview.getLayoutParams().height = 700;
//    	} else {
//    		System.out.println("nothing");
//    	}
		super.onConfigurationChanged(newConfig);
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
                    player.pause();
                    gPlayBtn.setBackgroundResource(R.drawable.btn_play_selecter);
                } else {
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
        gModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRenderer.getMode() == RBTextureRender.MODE_NORMAL) {
                    mRenderer.setMode(RBTextureRender.MODE_ONE);
                } else {
                    mRenderer.setMode(RBTextureRender.MODE_NORMAL);
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
    
    private OnPlayGoing playGoing = new OnPlayGoing() {

		@Override
		public void doPlayStuff() {
			shouldPlaying = true;
			player.start();
			seekBarHandler();
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
            player.setDataSource(url);
            player.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
				@Override
				public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
					if(width > 0 && height > 0) {
						setScreenMode(screenMode);
					}
				}
			});
            videoview.setEGLContextClientVersion(2);
            mRenderer = new RBVideoRender(this);
            mRenderer.setMediaPlayer(player);
            mRenderer.setOnPlayGoing(playGoing);
            videoview.setRenderer(mRenderer);
        } catch (Exception e) {
        }
    }
    
    private void setScreenMode(int mode) {
    	int[] screenSizes = UtilBox.getScreenSize(this);
    	int videoWidth = player.getVideoWidth();
    	int videoHeight = player.getVideoHeight();
    	double screenAspect = screenSizes[0] / (double) screenSizes[1];
    	double videoAspect = videoWidth / (double) videoHeight;
    	int dstWidth = 0;
    	int dstHeight = 0;
    	if(mode == SCREEN_MODE_NORMAL) {
    		if(videoAspect - screenAspect > 0) {
    			dstWidth = screenSizes[0];
    			dstHeight = (int) (videoHeight * (dstWidth / (double)videoWidth));
    		} else {
    			dstHeight = screenSizes[1];
    			dstWidth = (int) (videoWidth * (dstHeight / (double)videoHeight));
    		}
    	}
    	setVideoViewSize(dstWidth, dstHeight);
    }
    
    private void setVideoViewSize(int width, int height) {
    	try {
			videoview.getLayoutParams().width = width;
			videoview.getLayoutParams().height = height;
		} catch (NullPointerException e) {
			Log.e("", "");
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
		if(!player.isPlaying() && shouldPlaying)
			player.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		player.stop();
        player.release();
	}
	
}
