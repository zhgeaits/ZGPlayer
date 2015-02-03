package org.zhangge.rbplayer.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by zhangge on 2014/10/24.
 */
public class BaseActivity extends FragmentActivity {
	protected Handler gHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gHandler = new Handler();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (gHandler != null) {
			gHandler.removeCallbacksAndMessages(null);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
