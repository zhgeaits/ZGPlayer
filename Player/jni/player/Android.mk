#当前目录 开头必须以这个开始
LOCAL_PATH := $(call my-dir)

#CLEAR_VARS变量指向了clear-vars.mk(mk就是makefile的意思)位置，include这个可以清除出来LOCAL_PATH以外的LOCAL开头的变量，如LOCAL_MODULE等
#这样做是因为android构建系统再单次执行中解析多个构建文件和模块定义，而LOCAL开头这些是全局变量，清除它们可以避免冲突。
include $(CLEAR_VARS)

#库的名字
LOCAL_MODULE    := ZGPlayer

#源码目录
LOCAL_SRC_FILES += \
	src/ZGPlayerJNI.c \
	src/player_main.c

#头文件目录	
LOCAL_CFLAGS := \
	-I$(LOCAL_PATH)"/include" \
	-I$(LOCAL_PATH)"/../ffmpeg/include"
	
LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid "C:/Users/Administrator/Documents/GitHub/ZGPlayer/Player/jni/ffmpeg/libffmpeg.so"
#LOCAL_SHARED_LIBRARIES := libffmpeg
	
ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
#C编译器的可选标记选项
	LOCAL_CFLAGS += -DHAVE_NEON=1

#这个会使得去编译,.neon文件，还没测试过。。
	#LOCAL_ARM_NEON := true
endif

#为了建立供主应用程序使用的模块，必须将该模块编程共享库。这个变量指向了build-shared-library.mk的位置，这个makefie包含了构建的过程。
include $(BUILD_SHARED_LIBRARY)
