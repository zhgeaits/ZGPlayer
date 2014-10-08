LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ZGPlayer
LOCAL_SRC_FILES := ZGPlayer.cpp

include $(BUILD_SHARED_LIBRARY)
