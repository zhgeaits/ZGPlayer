package org.zhangge.rbplayer.ui;

import org.zhangge.almightyzgbox_android.net.http.VolleyManager;
import org.zhangge.almightyzgbox_android.ui.bar.VerticalSeekBar;
import org.zhangge.almightyzgbox_android.utils.CommonUtils;
import org.zhangge.rbplayer.camera.ColorView;
import org.zhangge.rbplayer.camera.PicTextureRender;
import org.zhangge.rbplayerpro.R;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PhotoActivity extends BaseActivity {
	
	public static String URL_KEY = "url_key";
	public static String PATH_KEY = "path_key";
	public static String TYPE_KEY = "type_key";
	public static int SCREEN_MODE_NORMAL = 0;
	public static int SCREEN_MODE_COLOR = 1;
	public static int SCREEN_MODE_GLES = 2;
	private static final int OFFSET_MAX = 80;
	
	private VerticalSeekBar gOffsetSeekbar;
	private ImageView gImageView;
	private GLSurfaceView gSurfaceView;
	private String gPath1;
	private String gPath2;
	private Bitmap gBitmap1;
	private Bitmap gBitmap2;
	private int gOffset;
	private PicTextureRender gRender;
	private ColorView gColor;
	private int gMode;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photo);
        gImageView = (ImageView) findViewById(R.id.image);
        gSurfaceView = (GLSurfaceView) findViewById(R.id.surfaceview);
        gColor = (ColorView) findViewById(R.id.colorimage);
        gOffsetSeekbar = (VerticalSeekBar) findViewById(R.id.offset_seekbar);
		gOffsetSeekbar.setMax(OFFSET_MAX * 2);
		gOffsetSeekbar.setProgress(OFFSET_MAX);
		
        if(getIntent().getExtras() != null) {
        	gMode = getIntent().getExtras().getInt(TYPE_KEY);
        	if(gMode == SCREEN_MODE_NORMAL) {
        		String url = getIntent().getExtras().getString(URL_KEY);
            	if(!CommonUtils.isNullOrEmpty(url)) {
            		initCommonImage(url);
            	}
        	} else if(gMode == SCREEN_MODE_COLOR) {
        		gPath1 = getIntent().getExtras().getString(PATH_KEY);
            	if(!CommonUtils.isNullOrEmpty(gPath1)) {
            		initRBImage();
            	}
        	} else if(gMode == SCREEN_MODE_GLES) {
        		gPath1 = getIntent().getExtras().getString(PATH_KEY);
            	if(!CommonUtils.isNullOrEmpty(gPath1)) {
            		initSBSImage();
            	}
        	}
        }
    }
	
	private void initCommonImage(String url) {
		gOffsetSeekbar.setVisibility(View.GONE);
		gSurfaceView.setVisibility(View.GONE);
		gColor.setVisibility(View.GONE);
		gImageView.setVisibility(View.VISIBLE);
		VolleyManager.getInstance().loadImage(url, gImageView, R.drawable.red_blue_3d, R.drawable.red_blue_3d);
		PhotoViewAttacher mAttacher = new PhotoViewAttacher((ImageView) gImageView);
    	mAttacher.update();
	}
	
	private void initSBSImage() {
		gImageView.setVisibility(View.GONE);
		gOffsetSeekbar.setVisibility(View.VISIBLE);
		gSurfaceView.setVisibility(View.VISIBLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
		gBitmap1 = BitmapFactory.decodeFile(gPath1);
		
		gRender = new PicTextureRender(gBitmap1);
		gSurfaceView.setEGLContextClientVersion(2);
		gSurfaceView.setRenderer(gRender);
		gSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
		gOffsetSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				gOffset = progress - OFFSET_MAX;
				gRender.setOffset(gOffset);
			}
		});
	}
	
	private void initRBImage() {
		gImageView.setVisibility(View.GONE);
		gOffsetSeekbar.setVisibility(View.VISIBLE);
		gColor.setVisibility(View.VISIBLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
		gPath2 = gPath1.replace(".jpg", "-2.jpg");
		gBitmap1 = BitmapFactory.decodeFile(gPath1);
		gBitmap2 = BitmapFactory.decodeFile(gPath2);
		gColor.setBitmap(gBitmap1, gBitmap2);
		
		gOffsetSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				gOffset = progress - OFFSET_MAX;
				gColor.setOffset(gOffset);
			}
		});
	}
	
	@Override
	public void onPause() {
        super.onPause();
//        gSurfaceView.onPause();
    }
 
    @Override
    public void onResume() {
        super.onResume();
//        gSurfaceView.onResume();
    }
}
