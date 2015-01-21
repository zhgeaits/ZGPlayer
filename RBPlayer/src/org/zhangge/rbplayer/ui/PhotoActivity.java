package org.zhangge.rbplayer.ui;

import org.zhangge.almightyzgbox_android.net.http.VolleyManager;
import org.zhangge.rbplayer.R;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.os.Bundle;
import android.widget.ImageView;

public class PhotoActivity extends BaseActivity {
	
	public static String URL_KEY = "url_key";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        if(getIntent().getExtras() != null) {
        	String url = getIntent().getExtras().getString(URL_KEY);
        	VolleyManager.getInstance().loadImage(url, imageView, R.drawable.red_blue_3d, R.drawable.red_blue_3d);
        	PhotoViewAttacher mAttacher = new PhotoViewAttacher((ImageView) imageView);
        	mAttacher.update();
        }
    }
}
