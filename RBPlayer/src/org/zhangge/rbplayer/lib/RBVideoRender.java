package org.zhangge.rbplayer.lib;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

public class RBVideoRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
	private static String TAG = "RBVideoRender";

	private RBTextureRender mTextureRender;
	private SurfaceTexture mSurfaceTexture;
	private boolean updateSurface = false;

	private MediaPlayer mMediaPlayer;

	public RBVideoRender(Context context) {
		mTextureRender = new RBTextureRender();
	}

	public void setMediaPlayer(MediaPlayer player) {
		mMediaPlayer = player;
	}

	public void onDrawFrame(GL10 glUnused) {
		synchronized (this) {
			if (updateSurface) {
				mSurfaceTexture.updateTexImage();
				updateSurface = false;
			}
		}

		mTextureRender.drawFrame(mSurfaceTexture);
	}

	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
	}

	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		mTextureRender.surfaceCreated();

		mSurfaceTexture = new SurfaceTexture(mTextureRender.getTextureId());
		mSurfaceTexture.setOnFrameAvailableListener(this);

		Surface surface = new Surface(mSurfaceTexture);
		mMediaPlayer.setSurface(surface);
		surface.release();

		try {
			mMediaPlayer.prepare();
		} catch (IOException t) {
			Log.e(TAG, "media player prepare failed:" + t);
		}

		synchronized (this) {
			updateSurface = false;
		}

        mMediaPlayer.start();
	}

	synchronized public void onFrameAvailable(SurfaceTexture surface) {
		updateSurface = true;
	}
}
