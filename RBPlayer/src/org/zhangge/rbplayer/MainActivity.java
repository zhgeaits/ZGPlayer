package org.zhangge.rbplayer;

import org.zhangge.rbplayer.ui.VideoListActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	private Context gContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gContext = this;
		findViewById(R.id.helloworld).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(gContext, VideoListActivity.class);
				startActivity(intent);				
			}
		});
		
	}
	
}
