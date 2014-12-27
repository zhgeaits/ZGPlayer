package org.zhangge.rbplayer.ui;

import org.zhangge.rbplayer.R;
import org.zhangge.rbplayer.lib.RBTextureRender;
import org.zhangge.rbplayer.lib.RBVideoRender;
import org.zhangge.rbplayer.lib.RBVideoRender.OnPlayGoing;
import org.zhangge.rbplayer.utils.UtilBox;

import android.content.pm.ActivityInfo;
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
    private View modeControl;
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
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_video_player);
		screenMode = SCREEN_MODE_NORMAL;
		if(getIntent().getExtras() != null) {
			String url = getIntent().getExtras().getString(KEY_VIDEO_URL);
            initView();
            //eassee
//            url = "http://r6---sn-hpjx-hn2e.googlevideo.com/videoplayback?itag=22&source=youtube&signature=D11D7711083B4107639739BEDDD7F013A6A7A393.2AEECDEE7431EF1DACBF37C6C9CDFCFD5D257F19&mv=m&ratebypass=yes&mt=1418830045&ms=au&gcr=us&sver=3&mm=31&ip=65.49.14.133&id=o-AGm238ryWjjvp5VZcA1Urw90CJn3sf2O2sdD-Zz2IA8O&initcwndbps=1168750&key=yt5&mime=video%2Fmp4&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Cmime%2Cmm%2Cms%2Cmv%2Cratebypass%2Csource%2Cupn%2Cexpire&expire=1418851665&upn=SOl-sgrgtPU&ipbits=0&fexp=900718%2C901496%2C912135%2C913445%2C917000%2C922247%2C924231%2C924639%2C927622%2C930676%2C932404%2C9405649%2C941004%2C943917%2C947209%2C947218%2C948124%2C952302%2C952605%2C952901%2C955301%2C957103%2C957105%2C957201%2C959701&dur=218.128&signature=D11D7711083B4107639739BEDDD7F013A6A7A393.2AEECDEE7431EF1DACBF37C6C9CDFCFD5D257F19";
            //mine
//            url = "http://r4---sn-hpjx-hn2e.googlevideo.com/videoplayback?mm=31&ip=65.49.14.134&sver=3&itag=22&ratebypass=yes&ipbits=0&id=o-AMJVtt1W6KDHsWLEBzShsUXyEVnCm8odV_c92UEJzgcO&dur=195.349&ms=au&mt=1418832866&mv=m&source=youtube&key=yt5&upn=seouJ2LzAWQ&expire=1418854547&sparams=dur%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Cmm%2Cms%2Cmv%2Cratebypass%2Csource%2Cupn%2Cexpire&signature=B6FCFB43F2040379171BDC56A752A65754B0DF46.9209B5F8A7ECBC01EA652E7F4C088F87C51DEFB5&fexp=900245%2C900718%2C904845%2C916645%2C927622%2C930676%2C931358%2C932404%2C9405716%2C9406047%2C941004%2C941458%2C943917%2C947209%2C947218%2C948124%2C952302%2C952605%2C952901%2C955301%2C957103%2C957105%2C957201&initcwndbps=1262500";
            //都OK
            play(url);
        }
	}
    

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
    	setScreenMode(screenMode);
		super.onConfigurationChanged(newConfig);
	}

	private void initView() {
        gPlayBtn = (Button) findViewById(R.id.play_btn);
        gModeBtn = (Button) findViewById(R.id.mode);
        gSeekbar = (SeekBar) findViewById(R.id.seekbar);
        gPlayTime = (TextView) findViewById(R.id.playtime);
        playControl = findViewById(R.id.playControl);
        videoview = (GLSurfaceView) findViewById(R.id.videoview);
        modeControl = findViewById(R.id.modeControl);

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
                	modeControl.setVisibility(View.GONE);
                } else {
                	playControl.setVisibility(View.VISIBLE);
                	modeControl.setVisibility(View.VISIBLE);
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
            videoview.setEGLContextClientVersion(2);
            mRenderer = new RBVideoRender(this);
            mRenderer.setMediaPlayer(player);
            mRenderer.setOnPlayGoing(playGoing);
            videoview.setRenderer(mRenderer);
        } catch (Exception e) {
        }
    }

    private void setVideoViewSize(int width, int height) {
        try {
            videoview.getLayoutParams().width = width;
            videoview.getLayoutParams().height = height;
        } catch (NullPointerException e) {
            Log.e("", "");
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
