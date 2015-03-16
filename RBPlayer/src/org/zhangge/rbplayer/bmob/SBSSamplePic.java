package org.zhangge.rbplayer.bmob;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class SBSSamplePic extends BmobObject {

	private static final long serialVersionUID = 8146272683600758588L;
	private BmobFile pic;
	private String url;
	
	public BmobFile getPic() {
		return pic;
	}
	public void setPic(BmobFile pic) {
		this.pic = pic;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
