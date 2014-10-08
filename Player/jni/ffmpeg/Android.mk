LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := ffmpeg
LOCAL_SRC_FILES := libffmpeg.so

#创建预构建库
include $(PREBUILT_SHARED_LIBRARY)
