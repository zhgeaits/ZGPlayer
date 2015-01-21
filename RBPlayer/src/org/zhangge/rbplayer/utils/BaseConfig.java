package org.zhangge.rbplayer.utils;

import java.util.ArrayList;
import java.util.List;

import org.zhangge.rbplayer.bmob.SamplePic;

public class BaseConfig {

	private static List<SamplePic> samplePicList = new ArrayList<SamplePic>();
	
	public static List<SamplePic> getSamplePic() {
		return samplePicList;
	}
	
	public static void addSamplePicList(List<SamplePic> picList) {
		samplePicList.addAll(picList);
	}
}
