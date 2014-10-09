/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_zhangge_zgplayer_lib_LibZGPlayer */

#ifndef _Included_org_zhangge_zgplayer_lib_LibZGPlayer
#define _Included_org_zhangge_zgplayer_lib_LibZGPlayer
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_zhangge_zgplayer_lib_LibZGPlayer
 * Method:    play
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_zhangge_zgplayer_lib_LibZGPlayer_play
  (JNIEnv *, jclass);

/*
 * Class:     org_zhangge_zgplayer_lib_LibZGPlayer
 * Method:    pause
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_zhangge_zgplayer_lib_LibZGPlayer_pause
  (JNIEnv *, jclass);

/*
 * Class:     org_zhangge_zgplayer_lib_LibZGPlayer
 * Method:    stop
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_zhangge_zgplayer_lib_LibZGPlayer_stop
  (JNIEnv *, jclass);

/*
 * Class:     org_zhangge_zgplayer_lib_LibZGPlayer
 * Method:    setVideoPath
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_zhangge_zgplayer_lib_LibZGPlayer_setVideoPath
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_zhangge_zgplayer_lib_LibZGPlayer
 * Method:    setSurface
 * Signature: (Landroid/view/Surface;)V
 */
JNIEXPORT void JNICALL Java_org_zhangge_zgplayer_lib_LibZGPlayer_setSurface
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_zhangge_zgplayer_lib_LibZGPlayer
 * Method:    setWindowSize
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_zhangge_zgplayer_lib_LibZGPlayer_setWindowSize
  (JNIEnv *, jclass, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
