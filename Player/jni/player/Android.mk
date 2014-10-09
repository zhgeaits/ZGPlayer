#��ǰĿ¼ ��ͷ�����������ʼ
LOCAL_PATH := $(call my-dir)

#CLEAR_VARS����ָ����clear-vars.mk(mk����makefile����˼)λ�ã�include��������������LOCAL_PATH�����LOCAL��ͷ�ı�������LOCAL_MODULE��
#����������Ϊandroid����ϵͳ�ٵ���ִ���н�����������ļ���ģ�鶨�壬��LOCAL��ͷ��Щ��ȫ�ֱ�����������ǿ��Ա����ͻ��
include $(CLEAR_VARS)

#�������
LOCAL_MODULE    := ZGPlayer

#Դ��Ŀ¼
LOCAL_SRC_FILES += \
	src/ZGPlayerJNI.c \
	src/player_main.c

#ͷ�ļ�Ŀ¼	
LOCAL_CFLAGS := \
	-I$(LOCAL_PATH)"/include" \
	-I$(LOCAL_PATH)"/../ffmpeg/include"
	
LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid "C:/Users/Administrator/Documents/GitHub/ZGPlayer/Player/jni/ffmpeg/libffmpeg.so"
#LOCAL_SHARED_LIBRARIES := libffmpeg
	
ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
#C�������Ŀ�ѡ���ѡ��
	LOCAL_CFLAGS += -DHAVE_NEON=1

#�����ʹ��ȥ����,.neon�ļ�����û���Թ�����
	#LOCAL_ARM_NEON := true
endif

#Ϊ�˽�������Ӧ�ó���ʹ�õ�ģ�飬���뽫��ģ���̹���⡣�������ָ����build-shared-library.mk��λ�ã����makefie�����˹����Ĺ��̡�
include $(BUILD_SHARED_LIBRARY)
