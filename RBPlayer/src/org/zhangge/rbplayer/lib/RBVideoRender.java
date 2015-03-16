package org.zhangge.rbplayer.lib;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.zhangge.almightyzgbox_android.log.ZGLog;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.view.Surface;

public class RBVideoRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
	private static String TAG = "RBVideoRender";

	private RBTextureRender mTextureRender;
	private SurfaceTexture mSurfaceTexture;
	private boolean updateSurface = false;

	private MediaPlayer mMediaPlayer;
	private OnPlayGoing playGoing;
	

	public RBVideoRender(Context context) {
	}

	public void setMediaPlayer(MediaPlayer player) {
		mMediaPlayer = player;
	}
	
	public void setOnPlayGoing(OnPlayGoing playGoing) {
		this.playGoing = playGoing;
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
		mTextureRender.setSize(width, height);
	}

	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		mTextureRender = new RBTextureRender();
		mTextureRender.surfaceCreated();
		
		mSurfaceTexture = new SurfaceTexture(mTextureRender.getTextureId());
		mSurfaceTexture.setOnFrameAvailableListener(this);

		Surface surface = new Surface(mSurfaceTexture);
		mMediaPlayer.setSurface(surface);
		surface.release();
		
		try {
			mMediaPlayer.prepare();
		} catch (IOException t) {
			ZGLog.error(TAG, "media player prepare failed:" + t);
		}

        synchronized (this) {
            updateSurface = false;
        }

        if(playGoing != null) {
            playGoing.doPlayStuff();
        }
	}
	
	public void resetOffset() {
		mTextureRender.resetOffset();
	}
	
	public void setOffset(int offset) {
		float f = Float.valueOf(offset);
		mTextureRender.setOffset(f);
	}

    public void setMode(int mode) {
        mTextureRender.setMode(mode);
    }

    public float getMode() {
        return mTextureRender.getMode();
    }

	public synchronized void onFrameAvailable(SurfaceTexture surface) {
		updateSurface = true;
	}
	
	public interface OnPlayGoing {
		public void doPlayStuff();
	}
}
