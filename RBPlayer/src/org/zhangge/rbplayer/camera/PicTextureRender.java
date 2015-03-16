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
            "void main() {\n" +
            "   gl_FragColor = texture2D(u_samplerTexture, v_texCoord);\n" +
            "}\n";
	
	public PicTextureRender(Bitmap bitmap) {
		super();
		this.gBitmap = bitmap;
		gOffset = 0;
	}

	public void setOffset(int offset) {
		gOffset = offset;
	}
	
	private float[] mMVPMatrix = new float[16];
	private int textureId;
	private int program;
	private int muMVPMatrixHandle;
	private int attribPosition;
	private int attribTexCoord;
	private int uniformTexture;
	private FloatBuffer vertex;
	private ShortBuffer index;
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
		this.vertex = ByteBuffer.allocateDirect(quadVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.vertex.put(quadVertex).position(0);
		// short size = 2
		this.index = ByteBuffer.allocateDirect(quadIndex.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		this.index.put(quadIndex).position(0);
	}
	
	private void initShader() {
		String vertexSource = VERTEX_SHADER;
		String fragmentSource = FRAGMENT_SHADER;
		// Load the shaders and get a linked program
		program = loadProgram(vertexSource, fragmentSource);
		
		// Get the attribute locations
		muMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
//        ShaderUtils.checkGlError("glGetUniformLocation uMVPMatrix");
//        if (muMVPMatrixHandle == -1) {
//            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
//        }
		
		attribPosition = GLES20.glGetAttribLocation(program, "a_position");
		attribTexCoord = GLES20.glGetAttribLocation(program, "a_texCoord");
		uniformTexture = GLES20.glGetUniformLocation(program, "u_samplerTexture");
		GLES20.glUseProgram(program);
		GLES20.glEnableVertexAttribArray(attribPosition);
		GLES20.glEnableVertexAttribArray(attribTexCoord);
		
		
		// Set the sampler to texture unit 0
		GLES20.glUniform1i(uniformTexture, 0);
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
		textureId = loadTexture(gBitmap);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gWidth = width;
        gl.glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// clear screen to black
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		
		vertex.position(0);
		// load the position
		// 3(x , y , z)
		// (2 + 3 )* 4 (float size) = 20
		GLES20.glVertexAttribPointer(attribPosition, 3, GLES20.GL_FLOAT, false, 20, vertex);
		vertex.position(3);
		// load the texture coordinate
		GLES20.glVertexAttribPointer(attribTexCoord, 2, GLES20.GL_FLOAT, false, 20, vertex);
		
		Matrix.setIdentityM(mMVPMatrix, 0);
		mMVPMatrix[5] = -1;
		Matrix.scaleM(mMVPMatrix, 0, 2f, 1f, 1f);
    	Matrix.translateM(mMVPMatrix, 0, 0.5f, 0, 0);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, index);
	}
}
