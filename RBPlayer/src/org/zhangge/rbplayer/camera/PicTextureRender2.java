package org.zhangge.rbplayer.camera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

public class PicTextureRender2 implements GLSurfaceView.Renderer {

	private Bitmap gBitmap;
	
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
            "attribute vec4 aPosition;\n" +
            "attribute vec2 a_texCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "  gl_Position = uMVPMatrix * aPosition;\n" +
            "  vTextureCoord = a_texCoord;\n" +
            "}\n";

    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision highp float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D sTexture;\n" +
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
            "	gl_FragColor.r = texture2D(sTexture, vTexL).r;\n" +
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

    private int mProgram;
    private int mTextureID = -12345;
    private int uniformTexture;
    private int muMVPMatrixHandle;
    private int maPositionHandle;
    private int maTextureHandle;
    private int mModeHandle;
    private int mWidthHandle;
    private int mOffsetHandle;

    private float offset = 0.0f;
    private float mWidth;
    private int mode;
    public static int MODE_NORMAL = 0;
    public static int MODE_ONE = 1;
    public static int MODE_TWO = 2;
	
	public PicTextureRender2(Bitmap bitmap) {
		super();
		this.gBitmap = bitmap;
		
		mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangleVertices.put(mTriangleVerticesData).position(0);
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public static int loadProgram(String vertexSource, String fragmentSource) {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		int program = GLES20.glCreateProgram();
		if (program == 0) {
			throw new RuntimeException("Error create program.");
		}
		GLES20.glAttachShader(program, vertexShader);
		GLES20.glAttachShader(program, fragmentShader);
		GLES20.glLinkProgram(program);
		int[] linked = new int[1];
		GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linked, 0);
		if (linked[0] == 0) {
			GLES20.glDeleteProgram(program);
			throw new RuntimeException("Error linking program: " + GLES20.glGetProgramInfoLog(program));
		}
		GLES20.glDeleteShader(vertexShader);
		GLES20.glDeleteShader(fragmentShader);
		return program;
	}
	
	public static int loadShader(int shaderType, String source) {
		int shader = GLES20.glCreateShader(shaderType);
		if (shader == 0) {
			throw new RuntimeException("Error create shader.");
		}
		int[] compiled = new int[1];
		GLES20.glShaderSource(shader, source);
		GLES20.glCompileShader(shader);
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			GLES20.glDeleteShader(shader);
			throw new RuntimeException("Error compile shader: " + GLES20.glGetShaderInfoLog(shader));
		}
		return shader;
	}
	
	private int loadTexture(Bitmap bitmap) {
		int[] textureId = new int[1];
		// Generate a texture object
		GLES20.glGenTextures(1, textureId, 0);
		int result = 0;
		if (textureId[0] != 0) {
			result = textureId[0];
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			
			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		} else {
			throw new RuntimeException("Error loading texture.");
		}
		return result;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glEnable(GLES20.GL_TEXTURE_2D); 
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		mProgram = loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (mProgram == 0) {
            throw new RuntimeException("failed creating program");
        }
        
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        uniformTexture = GLES20.glGetUniformLocation(mProgram, "sTexture");
        mModeHandle = GLES20.glGetUniformLocation(mProgram, "mode");
        mWidthHandle = GLES20.glGetUniformLocation(mProgram, "width");
        mOffsetHandle = GLES20.glGetUniformLocation(mProgram, "offset");
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        GLES20.glUniform1i(uniformTexture, 0);
        
        mTextureID = loadTexture(gBitmap);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		mWidth = width;
        gl.glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {

        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
            TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
            TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);

        Matrix.setIdentityM(mMVPMatrix, 0);
        if(mode == MODE_ONE) {
        	Matrix.scaleM(mMVPMatrix, 0, 2f, 1f, 1f);
        	Matrix.translateM(mMVPMatrix, 0, 0.5f, 0, 0);
        }
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        //传送模式下去
        GLES20.glUniform1i(mModeHandle, mode);
        GLES20.glUniform1f(mOffsetHandle, offset);
        GLES20.glUniform1f(mWidthHandle, mWidth);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glFinish();
	}
}
