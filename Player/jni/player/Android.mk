#当前目录
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ZGPlayer

#源码目录
LOCAL_SRC_FILES += \
	src/ZGPlayer.cpp


include $(BUILD_SHARED_LIBRARY)
