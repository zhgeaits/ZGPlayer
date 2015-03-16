package org.zhangge.rbplayer.bmob;

import cn.bmob.v3.BmobObject;

/**
 * 名字错了，好吧，不管了。
 * @author zhangge
 *
 */
public class RBSwithcer extends BmobObject  {

	private static final long serialVersionUID = 7774273146006152099L;
	private String packagename;
	private boolean advswitcher;
	private boolean sampleswitcher;
	private boolean youtubeswitcher;
	
	public String getPackagename() {
		return packagename;
	}
	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}
	public boolean isAdvswitcher() {
		return advswitcher;
	}
	public void setAdvswitcher(boolean advswitcher) {
		this.advswitcher = advswitcher;
	}
	public boolean isSampleswitcher() {
		return sampleswitcher;
	}
	public void setSampleswitcher(boolean sampleswitcher) {
		this.sampleswitcher = sampleswitcher;
	}
	public boolean isYoutubeswitcher() {
		return youtubeswitcher;
	}
	public void setYoutubeswitcher(boolean youtubeswitcher) {
		this.youtubeswitcher = youtubeswitcher;
	}
	
}
