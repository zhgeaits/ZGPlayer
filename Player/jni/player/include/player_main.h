#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include <libavutil/pixfmt.h>

#include <stdio.h>
#include <pthread.h>

#include <jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>

#define LOG_TAG "ZGPlayer_jni"
#define LOGI(...) __android_log_print(4, LOG_TAG, __VA_ARGS__);
#define LOGE(...) __android_log_print(6, LOG_TAG, __VA_ARGS__);

typedef struct {
	ANativeWindow* 		window;
	char 				*videoFileName;
	AVFormatContext 	*formatCtx;
	int 				videoStream;
	AVCodecContext  	*codecCtx;
	AVFrame         	*decodedFrame;
	AVFrame         	*frameRGBA;
	jobject				bitmap;
	void*				buffer;
	struct SwsContext   *sws_ctx;
	int 				width;
	int 				height;
	int					stop;
} PlayerContext;

int player_init(char* videoFileName);

jobject player_create_bitmap(JNIEnv *env, int width, int height);

void player_setSurface(JNIEnv *env, jobject pSurface);

void player_finish(JNIEnv *env);

void player_decodeAndRenderThread(JNIEnv *env);

int player_setSize(JNIEnv *env, int width, int height);
