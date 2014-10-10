ZGPlayer
========

一个播放器，初步实现使用ffmpeg来解码。

视频解码：ffmpeg
视频渲染：android native window


ffmpeg编译：使用ffmpeg-2.4.2+android-ndk32-r10b-linux-x86，在ubuntu14.04_32下编译
复制脚本build_android_arm.sh到ffmpeg根目录即可进行运行编译。

应该根据自己的环境来修改脚本相关内容。

编译完毕以后，复制include头文件和so库文件到jni和lib目录去使用。

在eclipse下，右键项目，选择add native support来添加jni的支持。

在jni目录下新建ffmpeg，并且添加一个android.mk，详细内容看项目，这样每次build时候就不会删除了。

在eclipse里面可以配置javah命令，然后选中java文件即可生成.h头文件了。
Run->External Tools->External Tools Configurations.
Name:Generate Header File
Location:${system_path:javah}
Working Directory: ${project_loc}/jni/player/include
Arguments:-classpath "${project_classpath};${env_var:ANDROID_SDK_HOME}/platforms/android-16/android.jar" ${java_type_name}
然后切换到Refresh标签，勾上Refresh resources upon completion。再选上The project containing the seleced resource.
最后切换到Common标签，勾上External Tools.

在Eclipse里面进行native调试，很简单，最新版的adt已经支持了，在build command那里改成ndk-build NDK_DEBUG=1，然后右键项目，Debug as->Android native application就可以了。。。
可惜android4.3有一个bug，一直run-as不了，我没成功，然后用2.3也试成功，也没有别的手机了，就不试了。