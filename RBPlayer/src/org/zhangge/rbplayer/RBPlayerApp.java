package org.zhangge.rbplayer;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by zhangge on 2014/10/24.
 */
public class RBPlayerApp extends Application {

    public static String TAG = "RBPlayerApp";
    public static Context gContext;

    @Override
    public void onCreate() {
        super.onCreate();
        gContext = this;
        if(isMainProcess()) {
            //以后可以在这里写初始化整个应用的代码
        } else {
            Log.i(TAG, "return");
            return;
        }
    }

    private boolean isMainProcess() {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        int myPid = android.os.Process.myPid();
        String mainProcessName = this.getPackageName();
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
