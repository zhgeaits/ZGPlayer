package org.zhangge.rbplayer.utils;

import java.util.ArrayList;
import java.util.List;

import org.zhangge.almightyzgbox_android.AlmightAndroidBox;
import org.zhangge.almightyzgbox_android.utils.ZGConstant;
import org.zhangge.rbplayer.bmob.SamplePic;

public class BaseConfig {

	private static List<SamplePic> samplePicList = new ArrayList<SamplePic>();
	private static String picturePath;;
	
	public static void init() {
		picturePath = ZGConstant.SDCARD_ROOT + AlmightAndroidBox.gAppName + "/pictures/";
	}
	
	public static List<SamplePic> getSamplePic() {
		return samplePicList;
	}
	
	public static void addSamplePicList(List<SamplePic> picList) {
		samplePicList.addAll(picList);
	}
	
	public static String getPicturePath() {
		return picturePath;
	}
}
