ZGPlayer
========

一个播放器，初步实现使用ffmpeg来解码。

视频解码：ffmpeg
视频渲染：android window


ffmpeg编译：使用ffmpeg-2.4.2+android-ndk32-r10b-linux-x86，在ubuntu14.04_32下编译
复制脚本build_android_arm.sh到ffmpeg根目录即可进行运行编译。

应该根据自己的环境来修改脚本相关内容。

编译完毕以后，复制include头文件和so库文件到jni和lib目录去使用。