package org.zhangge.zgplayer.lib;

import android.view.Surface;

public class LibZGPlayer {
	
	public static void loadLibrary() {
		System.loadLibrary("ZGPlayer");
		System.loadLibrary("ffmpeg");
	}

	public static native void play();
	
	public static native void pause();
	
	public static native void stop();
	
	public static native void setVideoPath(String filename);
	
	public static native void setSurface(Surface surface);
	
	public static native void setWindowSize(int width, int height);
}
