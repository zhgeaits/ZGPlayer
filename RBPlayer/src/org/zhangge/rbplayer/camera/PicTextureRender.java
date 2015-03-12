package org.zhangge.rbplayer.camera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.zhangge.rbplayer.utils.ShaderUtils;

import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

public class PicTextureRender implements GLSurfaceView.Renderer {

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
	private final float[] mTriangleVerticesData = {
			// X, Y, Z, U, V
			-1.0f, -1.0f, 0, 0.f, 0.f, 1.0f, -1.0f, 0, 1.f, 0.f, -1.0f, 1.0f, 0, 0.f, 1.f, 1.0f, 1.0f, 0, 1.f, 1.f, };

	private FloatBuffer mTriangleVertices;

	private static final String VERTEX_SHADER = 
			"uniform mat4 uMVPMatrix;\n" +
            "uniform mat4 uSTMatrix;\n" +
            "attribute vec4 aPosition;\n" +
            "attribute vec4 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "  gl_Position = uMVPMatrix * aPosition;\n" +
            "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
            "}\n";

	private static final String FRAGMENT_SHADER = 
			"#extension GL_OES_EGL_image_external : require\n" +
		    "precision highp float;\n" +
		    "varying vec2 vTextureCoord;\n" +
		    "uniform samplerExternalOES sTexture;\n" +
		    "void main() {\n" +
		    "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
		    "}\n";

	private float[] mMVPMatrix = new float[16];
	private float[] mSTMatrix = new float[16];

	private int mProgram;
	private int muMVPMatrixHandle;
	private int muSTMatrixHandle;
	private int maPositionHandle;
	private int maTextureHandle;
	private int mWidthHandle;
	private int mOffsetHandle;

	private int[] texturesIds;
	private Bitmap gBitmap1;
	private Bitmap gBitmap2;
	private float gOffset = 0.0f;
	private float mWidth;

	public PicTextureRender(Bitmap gBitmap1, Bitmap gBitmap2) {
		super();
		this.gBitmap1 = gBitmap1;
		this.gBitmap2 = gBitmap2;
		gOffset = 0;
		texturesIds = new int[2];

		mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangleVertices.put(mTriangleVerticesData).position(0);

		Matrix.setIdentityM(mSTMatrix, 0);
	}

	public void setOffset(int offset) {
		gOffset = offset;
	}

	public int initTexture(Bitmap bitmap) {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int textureId = textures[0];
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		return textureId;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		mProgram = ShaderUtils.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (mProgram == 0) {
            throw new RuntimeException("failed creating program");
        }
		
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        ShaderUtils.checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        ShaderUtils.checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        ShaderUtils.checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        ShaderUtils.checkGlError("glGetUniformLocation uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

//        mWidthHandle = GLES20.glGetUniformLocation(mProgram, "width");
//        ShaderUtils.checkGlError("glGetUniformLocation width");
//        if(mWidthHandle == -1) {
//        	throw new RuntimeException("Could not get attrib location for width");
//        }
//        
//        mOffsetHandle = GLES20.glGetUniformLocation(mProgram, "offset");
//        ShaderUtils.checkGlError("glGetUniformLocation offset");
//        if(mOffsetHandle == -1) {
//            throw new RuntimeException("Could not get attrib location for offset");
//        }
        
        texturesIds[0] = initTexture(gBitmap1);
//		texturesIds[1] = initTexture(gBitmap2);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		mWidth = width;
		GLES20.glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		ShaderUtils.checkGlError("onDrawFrame start");
		
		drawFrame(texturesIds[0]);
	}
	
	private void drawFrame(int textureId) {
		GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mProgram);
        ShaderUtils.checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
            TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        ShaderUtils.checkGlError("glVertexAttribPointer maPosition");
        
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        ShaderUtils.checkGlError("glEnableVertexAttribArray maPositionHandle");

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
            TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        ShaderUtils.checkGlError("glVertexAttribPointer maTextureHandle");
        
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        ShaderUtils.checkGlError("glEnableVertexAttribArray maTextureHandle");

        Matrix.setIdentityM(mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

//        GLES20.glUniform1f(mOffsetHandle, gOffset);
//        GLES20.glUniform1f(mWidthHandle, mWidth);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        ShaderUtils.checkGlError("glDrawArrays");
        GLES20.glFinish();
	}
}
