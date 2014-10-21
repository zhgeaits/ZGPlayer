package org.zhangge.rbplayer.lib;

import android.content.Context;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;

public class RBVideoSurfaceView extends GLSurfaceView {

	private RBVideoRender mRenderer;
	private MediaPlayer mMediaPlayer = null;

	public RBVideoSurfaceView(Context context, MediaPlayer mp) {
		super(context);

		setEGLContextClientVersion(2);
		mMediaPlayer = mp;
		mRenderer = new RBVideoRender(context);
		setRenderer(mRenderer);
		mRenderer.setMediaPlayer(mMediaPlayer);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
