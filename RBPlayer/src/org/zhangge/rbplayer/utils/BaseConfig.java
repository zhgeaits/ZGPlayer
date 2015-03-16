package org.zhangge.rbplayer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.zhangge.almightyzgbox_android.AlmightAndroidBox;
import org.zhangge.almightyzgbox_android.utils.ZGConstant;
import org.zhangge.almightyzgbox_android.utils.ZGPreference;
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
	
	public static void checkPackageName(String packageName) {
		if(switcher != null) {
			String pn = switcher.getPackagename();
			if(pn != null && !pn.equalsIgnoreCase(packageName)) {
				throw new RuntimeException();
			}
		}
	}
	
	private static void saveSwitcher(RBSwithcer swithcer) {
		ZGPreference.getInstance().put("packagename", swithcer.getPackagename());
		ZGPreference.getInstance().putBoolean("advswitcher", swithcer.isAdvswitcher());
		ZGPreference.getInstance().putBoolean("sampleswitcher", swithcer.isSampleswitcher());
		ZGPreference.getInstance().putBoolean("youtubeswitcher", swithcer.isYoutubeswitcher());
	}
	
	private static RBSwithcer querySwitcher() {
		RBSwithcer switcher = new RBSwithcer();
		switcher.setPackagename(ZGPreference.getInstance().get("packagename"));
		switcher.setAdvswitcher(ZGPreference.getInstance().getBoolean("advswitcher", true));
		switcher.setSampleswitcher(ZGPreference.getInstance().getBoolean("sampleswitcher", false));
		switcher.setYoutubeswitcher(ZGPreference.getInstance().getBoolean("youtubeswitcher", false));
		return switcher;
	}
	
	public static void setSwitcher(RBSwithcer swithcer) {
		switcher = swithcer;
		saveSwitcher(swithcer);
	}
	
	public static RBSwithcer getSwitcher() {
		if(switcher == null) {
			switcher = querySwitcher();
		}
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
