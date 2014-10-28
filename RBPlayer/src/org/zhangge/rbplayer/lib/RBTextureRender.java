package org.zhangge.rbplayer.lib;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.zhangge.rbplayer.utils.ShaderUtils;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
 

public class RBTextureRender {

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private final float[] mTriangleVerticesData = {
        // X, Y, Z, U, V
        -1.0f, -1.0f, 0, 0.f, 0.f,
        1.0f, -1.0f, 0, 1.f, 0.f,
        -1.0f,  1.0f, 0, 0.f, 1.f,
        1.0f,  1.0f, 0, 1.f, 1.f,
    };

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
            "uniform int mode;\n" +
            "uniform float offset;\n" +
            "uniform float width;\n" +
            "void doModeOne(){\n" +
            "  float origx = vTextureCoord.x;\n" +
            "  if(origx < 0.5) {\n" +
            "	vec2 vTexL;\n" +
            "	vec2 vTexR;\n" +
            "	vTexL.y = vTextureCoord.y;\n" +
            "	vTexR.y = vTextureCoord.y;\n" +
            "	vTexL.x = vTextureCoord.x;\n" +
            "	vTexR.x = vTextureCoord.x + 0.5 + offset / width;\n" +
            "  	gl_FragColor.r = texture2D(sTexture, vTexL).r;\n" +
            "  	gl_FragColor.gba = texture2D(sTexture, vTexR).gba;\n" +
            "  } else {\n" +
            "   discard;\n"+
            "  }\n" +
            "}\n" +
            "void main() {\n" +
            "   if(mode == 1) {\n" +
            "       doModeOne();\n" +
            "   } else {\n" +
            "       gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
            "   }\n" +
            "}\n";

    private float[] mMVPMatrix = new float[16];
    private float[] mSTMatrix = new float[16];

    private int mProgram;
    private int mTextureID = -12345;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int maPositionHandle;
    private int maTextureHandle;
    private int mModeHandle;
    private int mWidthHandle;
    private int mOffsetHandle;

    private float offset = 0.0f;
    private float mWidth;
    private int mHeight;
    private int mode;
    public static int MODE_NORMAL = 0;
    public static int MODE_ONE = 1;
    public static int MODE_TWO = 2;

    public RBTextureRender() {
        mTriangleVertices = ByteBuffer.allocateDirect(
            mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(mTriangleVerticesData).position(0);

        Matrix.setIdentityM(mSTMatrix, 0);

    }

    public int getTextureId() {
        return mTextureID;
    }
    
    public void setSize(int width, int height) {
    	mWidth = width;
    	mHeight = height;
    	GLES20.glViewport(0, 0, width, height);
    }
    
    public void setOffset(int offset) {
    	this.offset = offset;
    }

    public void addOffset() {
        this.offset += 1;
    }

    public void subOffset() {
        this.offset -= 1;
    }

    public void resetOffset() {
        this.offset = 0;
    }

    public void drawFrame(SurfaceTexture st) {
    	ShaderUtils.checkGlError("onDrawFrame start");
        st.getTransformMatrix(mSTMatrix);

        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mProgram);
        ShaderUtils.checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);

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
        if(mode == MODE_ONE) {
        	Matrix.scaleM(mMVPMatrix, 0, 2f, 1f, 1f);
        	Matrix.translateM(mMVPMatrix, 0, 0.5f, 0, 0);
        }
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

        //传送模式下去
        GLES20.glUniform1i(mModeHandle, mode);
        GLES20.glUniform1f(mOffsetHandle, offset);
        GLES20.glUniform1f(mWidthHandle, mWidth);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        ShaderUtils.checkGlError("glDrawArrays");
        GLES20.glFinish();
    }

    public void surfaceCreated() {
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

        mModeHandle = GLES20.glGetUniformLocation(mProgram, "mode");
        ShaderUtils.checkGlError("glGetUniformLocation mode");
        if(mModeHandle == -1) {
        	throw new RuntimeException("Could not get attrib location for mode");
        }
        
        mWidthHandle = GLES20.glGetUniformLocation(mProgram, "width");
        ShaderUtils.checkGlError("glGetUniformLocation width");
        if(mWidthHandle == -1) {
        	throw new RuntimeException("Could not get attrib location for width");
        }
        
        mOffsetHandle = GLES20.glGetUniformLocation(mProgram, "offset");
        ShaderUtils.checkGlError("glGetUniformLocation offset");
        if(mOffsetHandle == -1) {
            throw new RuntimeException("Could not get attrib location for offset");
        }

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
        ShaderUtils.checkGlError("glBindTexture mTextureID");

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        ShaderUtils.checkGlError("glTexParameter");
    }

    public synchronized void setMode(int mode) {
        this.mode = mode;
    }

    public float getMode() {
        return this.mode;
    }

    public void saveFrame(String filename, int mWidth, int mHeight) throws IOException {

        ByteBuffer mPixelBuf = ByteBuffer.allocateDirect(mWidth * mHeight * 4);
        mPixelBuf.order(ByteOrder.LITTLE_ENDIAN);
        mPixelBuf.rewind();
        GLES20.glReadPixels(0, 0, mWidth, mHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mPixelBuf);

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filename));
            Bitmap bmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mPixelBuf.rewind();
            bmp.copyPixelsFromBuffer(mPixelBuf);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bmp.recycle();
        } finally {
            if (bos != null) bos.close();
        }
    }

    public void changeFragmentShader(String fragmentShader) {
        GLES20.glDeleteProgram(mProgram);
        mProgram = ShaderUtils.createProgram(VERTEX_SHADER, fragmentShader);
        if (mProgram == 0) {
            throw new RuntimeException("failed creating program");
        }
    }
}
