#��ǰĿ¼
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ZGPlayer

#Դ��Ŀ¼
LOCAL_SRC_FILES += \
	src/ZGPlayer.cpp


include $(BUILD_SHARED_LIBRARY)
