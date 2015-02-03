package org.zhangge.rbplayer;

import java.util.List;

import org.zhangge.almightyzgbox_android.AlmightAndroidBox;
import org.zhangge.almightyzgbox_android.log.ZGLog;
import org.zhangge.rbplayer.bmob.SamplePic;
import org.zhangge.rbplayer.utils.BaseConfig;

import com.umeng.analytics.MobclickAgent;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

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
		if (isMainProcess()) {
			AlmightAndroidBox.init(this);
			Bmob.initialize(this, "63f7159ca32b64ab96dec8eee0e7c39c");
			getSamplePicList();
			MobclickAgent.updateOnlineConfig(this);
		} else {
			Log.i(TAG, "return");
			return;
		}
	}

	private void getSamplePicList() {
		BmobQuery<SamplePic> query = new BmobQuery<SamplePic>();
		query.setLimit(100);
		query.findObjects(gContext, new FindListener<SamplePic>() {
			@Override
			public void onSuccess(List<SamplePic> samplePics) {
				ZGLog.info(this, "get SamplePic from bmob size:" + samplePics.size());
				BaseConfig.addSamplePicList(samplePics);
			}

			@Override
			public void onError(int code, String msg) {
				ZGLog.info(this, "get SamplePic from bmob error code:" + code + ",msg:" + msg);
			}
		});
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
