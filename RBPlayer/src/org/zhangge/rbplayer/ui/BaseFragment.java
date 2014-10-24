package org.zhangge.rbplayer.ui;

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
}
