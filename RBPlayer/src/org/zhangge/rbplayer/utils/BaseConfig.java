package org.zhangge.rbplayer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.zhangge.almightyzgbox_android.AlmightAndroidBox;
import org.zhangge.almightyzgbox_android.utils.ZGConstant;
import org.zhangge.rbplayer.bmob.RBSwithcer;
import org.zhangge.rbplayer.bmob.SBSSamplePic;
import org.zhangge.rbplayer.bmob.SamplePic;

public class BaseConfig {

	private static List<SamplePic> samplePicList = new ArrayList<SamplePic>();
	private static List<SBSSamplePic> sbsSamplePicList = new ArrayList<SBSSamplePic>();
	private static String picturePath;;
	private static String sbsPath;
	private static RBSwithcer switcher;
	
	public static void init() {
		picturePath = ZGConstant.SDCARD_ROOT + AlmightAndroidBox.gAppName + "/pictures/";
		File file = new File(picturePath);
		if(!file.exists())
			file.mkdirs();
		
		sbsPath = ZGConstant.SDCARD_ROOT + AlmightAndroidBox.gAppName + "/sbspictures/";
		File sbsfile = new File(picturePath);
		if(!sbsfile.exists())
			sbsfile.mkdirs();
	}
	
	public static void setSwitcher(RBSwithcer swithcer) {
		switcher = swithcer;
	}
	
	public static RBSwithcer getSwitcher() {
		return switcher;
	}
	
	public static List<SamplePic> getSamplePic() {
		return samplePicList;
	}
	
	public static void addSamplePicList(List<SamplePic> picList) {
		samplePicList.addAll(picList);
	}
	
	public static List<SBSSamplePic> getSBSSamplePic() {
		return sbsSamplePicList;
	}
	
	public static void addSBSSamplePicList(List<SBSSamplePic> datas) {
		sbsSamplePicList.addAll(datas);	
	}
	
	public static String getPicturePath() {
		return picturePath;
	}
	
	public static String getSBSPicturePath() {
		return sbsPath;
	}
}
