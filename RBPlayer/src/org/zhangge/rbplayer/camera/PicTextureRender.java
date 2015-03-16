package org.zhangge.rbplayer.camera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

public class PicTextureRender implements GLSurfaceView.Renderer {

	private Bitmap gBitmap;
	private float gOffset = 0.0f;
	private float gWidth;
	private float[] gMVPMatrix = new float[16];
	private int gTextureId;
	private int gProgram;
	private int gMVPMatrixHandle;
	private int gWidthHandle;
    private int gOffsetHandle;
	private int gAttribPosition;
	private int gAttribTexCoord;
	private int gUniformTexture;
	private FloatBuffer gVertex;
	private ShortBuffer gIndex;
	private float[] quadVertex = new float[] { 
			-1f, 1f, 0.0f, // Position 0
			0, 1.0f, // TexCoord 0
			-1f, -1f, 0.0f, // Position 1
			0, 0, // TexCoord 1
			1f, -1f, 0.0f, // Position 2
			1.0f, 0, // TexCoord 2
			1f, 1f, 0.0f, // Position 3
			1.0f, 1.0f, // TexCoord 3
	};
	private short[] quadIndex = new short[] { 
			(short) (0), // Position 0
			(short) (1), // Position 1
			(short) (2), // Position 2
			(short) (2), // Position 2
			(short) (3), // Position 3
			(short) (0), // Position 0
	};
	
	private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
            "attribute vec4 a_position;\n" +
            "attribute vec2 a_texCoord;\n" +
            "varying vec2 v_texCoord;\n" +
            "void main() {\n" +
            "  gl_Position = uMVPMatrix * a_position;\n" +
            "  v_texCoord = a_texCoord;\n" +
            "}\n";

    private static final String FRAGMENT_SHADER =
            "precision highp float;\n" +
            "varying vec2 v_texCoord;\n" +
            "uniform sampler2D u_samplerTexture;\n" +
            "uniform float offset;\n" +
            "uniform float width;\n" +
            "void doModeOne(){\n" +
            "  float origx = v_texCoord.x;\n" +
            "  if(origx < 0.5) {\n" +
            "	vec2 vTexL;\n" +
            "	vec2 vTexR;\n" +
            "	vTexL.y = v_texCoord.y;\n" +
            "	vTexR.y = v_texCoord.y;\n" +
            "	vTexL.x = v_texCoord.x;\n" +
            "	vTexR.x = v_texCoord.x + 0.5 + offset / width;\n" +
            "	gl_FragColor.r = texture2D(u_samplerTexture, vTexL).r;\n" +
            "  	gl_FragColor.gba = texture2D(u_samplerTexture, vTexR).gba;\n" +
            "  } else {\n" +
            "   discard;\n"+
            "  }\n" +
            "}\n" +
            "void main() {\n" +
            "   doModeOne();\n" +
            "}\n";
	
	public PicTextureRender(Bitmap bitmap) {
		super();
		this.gBitmap = bitmap;
		gOffset = 0;
	}

	public void setOffset(int offset) {
		gOffset = offset;
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
	
	private void loadVertex() {
		// float size = 4
		this.gVertex = ByteBuffer.allocateDirect(quadVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.gVertex.put(quadVertex).position(0);
		// short size = 2
		this.gIndex = ByteBuffer.allocateDirect(quadIndex.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		this.gIndex.put(quadIndex).position(0);
	}
	
	private void initShader() {
		String vertexSource = VERTEX_SHADER;
		String fragmentSource = FRAGMENT_SHADER;
		// Load the shaders and get a linked program
		gProgram = loadProgram(vertexSource, fragmentSource);
		
		// Get the attribute locations
		gMVPMatrixHandle = GLES20.glGetUniformLocation(gProgram, "uMVPMatrix");
		gAttribPosition = GLES20.glGetAttribLocation(gProgram, "a_position");
		gAttribTexCoord = GLES20.glGetAttribLocation(gProgram, "a_texCoord");
		gUniformTexture = GLES20.glGetUniformLocation(gProgram, "u_samplerTexture");
		gWidthHandle = GLES20.glGetUniformLocation(gProgram, "width");
        gOffsetHandle = GLES20.glGetUniformLocation(gProgram, "offset");
		GLES20.glUseProgram(gProgram);
		GLES20.glEnableVertexAttribArray(gAttribPosition);
		GLES20.glEnableVertexAttribArray(gAttribTexCoord);
		
		// Set the sampler to texture unit 0
		GLES20.glUniform1i(gUniformTexture, 0);
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
		loadVertex(); 
		initShader(); 
		gTextureId = loadTexture(gBitmap);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gWidth = width;
        gl.glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, gTextureId);
		
		gVertex.position(0);
		// load the position
		// 3(x , y , z)
		// (2 + 3 )* 4 (float size) = 20
		GLES20.glVertexAttribPointer(gAttribPosition, 3, GLES20.GL_FLOAT, false, 20, gVertex);
		gVertex.position(3);
		// load the texture coordinate
		GLES20.glVertexAttribPointer(gAttribTexCoord, 2, GLES20.GL_FLOAT, false, 20, gVertex);
		
		Matrix.setIdentityM(gMVPMatrix, 0);
		//不明白为什么会翻转了，这里就设置关于X轴对称
		gMVPMatrix[5] = -1;
		Matrix.scaleM(gMVPMatrix, 0, 2f, 1f, 1f);
    	Matrix.translateM(gMVPMatrix, 0, 0.5f, 0, 0);
		GLES20.glUniformMatrix4fv(gMVPMatrixHandle, 1, false, gMVPMatrix, 0);
		
		GLES20.glUniform1f(gOffsetHandle, gOffset);
        GLES20.glUniform1f(gWidthHandle, gWidth);
		
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, gIndex);
	}
}
