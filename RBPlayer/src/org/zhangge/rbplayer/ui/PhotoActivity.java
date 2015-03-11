package org.zhangge.rbplayer.ui;

import org.zhangge.almightyzgbox_android.net.http.VolleyManager;
import org.zhangge.almightyzgbox_android.ui.bar.VerticalSeekBar;
import org.zhangge.almightyzgbox_android.utils.CommonUtils;
import org.zhangge.rbplayer.utils.SimpleImageLoader;
import org.zhangge.rbplayerpro.R;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	public static int SCREEN_MODE_NORMAL = 0;
	private static final int OFFSET_MAX = 30;
	
	private VerticalSeekBar gOffsetSeekbar;
	private String gPath;
	private String gPath2;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photo);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        
        gOffsetSeekbar = (VerticalSeekBar) findViewById(R.id.offset_seekbar);
		gOffsetSeekbar.setMax(OFFSET_MAX * 2);
		gOffsetSeekbar.setProgress(OFFSET_MAX);
		
        if(getIntent().getExtras() != null) {
        	String url = getIntent().getExtras().getString(URL_KEY);
        	if(!CommonUtils.isNullOrEmpty(url)) {
        		gOffsetSeekbar.setVisibility(View.GONE);
        		VolleyManager.getInstance().loadImage(url, imageView, R.drawable.red_blue_3d, R.drawable.red_blue_3d);
        		PhotoViewAttacher mAttacher = new PhotoViewAttacher((ImageView) imageView);
            	mAttacher.update();
        	}
        	gPath = getIntent().getExtras().getString(PATH_KEY);
        	if(!CommonUtils.isNullOrEmpty(gPath)) {
        		gOffsetSeekbar.setVisibility(View.VISIBLE);
        		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
        		gPath2 = gPath.replace(".jpg", "-2.jpg");
        		Bitmap bitmap1 = BitmapFactory.decodeFile(gPath);
        		Bitmap bitmap2 = BitmapFactory.decodeFile(gPath2);
        		SimpleImageLoader.getInstance().loadImage(gPath, imageView, R.drawable.red_blue_3d);
        	}
        }
        
        gOffsetSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//				gRenderer.setOffset(progress - OFFSET_MAX);
			}
		});
    }
}
