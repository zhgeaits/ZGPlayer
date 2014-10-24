package org.zhangge.rbplayer.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by zhangge on 2014/10/24.
 */
public class BaseActivity extends Activity {
    protected Handler gHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gHandler = new Handler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(gHandler != null){
            gHandler.removeCallbacksAndMessages(null);
        }
    }
}
