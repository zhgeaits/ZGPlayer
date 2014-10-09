#include <org_zhangge_zgplayer_lib_LibZGPlayer.h>
#include <player_main.h>

JNIEXPORT void JNICALL Java_org_zhangge_zgplayer_lib_LibZGPlayer_play
  (JNIEnv *env, jclass jclazz)
{}

JNIEXPORT void JNICALL Java_org_zhangge_zgplayer_lib_LibZGPlayer_setVideoPath
  (JNIEnv *env, jclass jclazz, jstring fileName){
	char *videoFileName = (char *)(*env)->GetStringUTFChars(env, fileName, NULL);
	LOGI("video file name is %s", videoFileName);
	player_init(videoFileName);
}

JNIEXPORT void JNICALL Java_org_zhangge_zgplayer_lib_LibZGPlayer_setSurface
  (JNIEnv *env, jclass jclazz, jobject surface){
	player_setSurface(env, surface);
}

JNIEXPORT void JNICALL Java_org_zhangge_zgplayer_lib_LibZGPlayer_setWindowSize
  (JNIEnv *env, jclass jclazz, jint width, jint height){
	player_setSize(env, width, height);
}
