LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := ffmpeg
LOCAL_SRC_FILES := libffmpeg.so

#����Ԥ������
include $(PREBUILT_SHARED_LIBRARY)
