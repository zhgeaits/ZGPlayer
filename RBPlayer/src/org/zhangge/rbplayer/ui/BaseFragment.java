package org.zhangge.rbplayer.ui;

import com.umeng.analytics.MobclickAgent;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by zhangge on 2014/10/24.
 */
public class BaseFragment extends Fragment {

    protected Handler gHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gHandler = new Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(gHandler != null){
            gHandler.removeCallbacksAndMessages(null);
        }
    }
    
    @Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageStart(this.getClass().getName());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageEnd(this.getClass().getName()); 
	}
}
