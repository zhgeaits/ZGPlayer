#!/bin/bash
######################################################
# Desc:
# This script is based on dolphin player's ffmpeg script, 
# modified by zhangge.
######################################################

NDK=/home/zhangge/android-ndk-r10b
PLATFORM=$NDK/platforms/android-9/arch-arm/
PREBUILT=$NDK/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86
RESULTDIR=./result

#BZLIBPATH="/Users/apple/Downloads/dolphin_player/dolphin-player/p/jni/bzip2/"
#BZLIB_LDPATHARM="/Users/apple/Downloads/dolphin_player/dolphin-player/p/jni/bzip2"
#BZLIB_LDPATHARMV7="/Users/apple/Downloads/dolphin_player/dolphin-player/p/jni/bzip2"

function build_one
{
./configure --target-os=linux \
    --prefix=$PREFIX \
    --enable-cross-compile \
    --extra-libs="-lgcc" \
    --cc=$PREBUILT/bin/arm-linux-androideabi-gcc \
    --cross-prefix=$PREBUILT/bin/arm-linux-androideabi- \
    --nm=$PREBUILT/bin/arm-linux-androideabi-nm \
    --sysroot=$PLATFORM \
    --extra-cflags=" -O3 -fpic -DANDROID -DHAVE_SYS_UIO_H=1 -Dipv6mr_interface=ipv6mr_ifindex -fasm -Wno-psabi -fno-short-enums -fno-strict-aliasing -finline-limit=300 $OPTIMIZE_CFLAGS -D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE -D_LARGEFILE64_SOURCE" \
    --enable-gpl \
    --enable-version3 \
    --enable-static \
    --extra-ldflags="-Wl,-rpath-link=$PLATFORM/usr/lib -L$PLATFORM/usr/lib -nostdlib -lc -lm -ldl -llog" \
    --enable-parsers \
    --enable-decoders \
    --enable-demuxers \
    --enable-network \
    --enable-protocols \
    --enable-protocol=file \
    --enable-swscale  \
    --enable-swresample \
    --enable-avformat \
    --enable-avcodec \
    --enable-pthreads \
    --disable-shared \
    --disable-ffmpeg \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-ffserver \
    --disable-devices \
    --disable-avdevice \
    --disable-postproc \
    --disable-avfilter \
    --disable-swscale-alpha \
    --disable-bsfs \
    --disable-encoders \
    --disable-muxers \
    --disable-indevs \
    --disable-debug \
    --disable-doc \
    --disable-demuxer=srt \
    --disable-demuxer=microdvd \
    --disable-demuxer=jacosub \
    --disable-decoder=ass \
    --disable-decoder=srt \
    --disable-decoder=microdvd \
    --disable-decoder=jacosub \
    --disable-bzlib \
    --enable-zlib \
    --enable-pic \
    --enable-optimizations \
    --disable-filters \
    $ADDITIONAL_CONFIGURE_FLAG

make clean
make  -j4 install
$PREBUILT/bin/arm-linux-androideabi-ar d libavcodec/libavcodec.a inverse.o

$PREBUILT/bin/arm-linux-androideabi-ld -rpath-link=$PLATFORM/usr/lib -L$PLATFORM/usr/lib  -soname libffmpeg.so -shared -nostdlib -z noexecstack -Bsymbolic --whole-archive --no-undefined -o $PREFIX/libffmpeg.so libavcodec/libavcodec.a libavformat/libavformat.a libavutil/libavutil.a libswscale/libswscale.a libswresample/libswresample.a -lc -lm -lz -ldl -llog --dynamic-linker=/system/bin/linker $PREBUILT/lib/gcc/arm-linux-androideabi/4.6/libgcc.a

}

function build_arm_v5
{
        #arm v5
	CPU=armv5te
	OPTIMIZE_CFLAGS="-march=$CPU -Wno-multichar -fno-exceptions"
        #-D__thumb__ -mthumb
	PREFIX=./android/$CPU 
	ADDITIONAL_CONFIGURE_FLAG="--arch=arm --cpu=armv5te --enable-armv5te --enable-memalign-hack"
	build_one
}

function build_arm_v6
{
        #arm v6
	CPU=armv6
	OPTIMIZE_CFLAGS="-march=$CPU "
	PREFIX=./$RESULTDIR/$CPU 
	ADDITIONAL_CONFIGURE_FLAG="--arch=armv6 --enable-armv5te --enable-armv6 --enable-memalign-hack"
	build_one
}

function build_arm_v6_vfp
{
        #arm v6+vfp
	CPU=armv6
	OPTIMIZE_CFLAGS="-DCMP_HAVE_VFP -mfloat-abi=softfp -mfpu=vfp -march=$CPU "
	PREFIX=./$RESULTDIR/${CPU}_vfp 
	ADDITIONAL_CONFIGURE_FLAG="--arch=armv6 --enable-armv5te --enable-armv6 --enable-armvfp --enable-memalign-hack"
	build_one
}

function build_arm_v7_vfp
{
        #arm v7vfp
	CPU=armv7-a
	OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=vfp -marm -march=$CPU "
	PREFIX=./$RESULTDIR/$CPU-vfp
	ADDITIONAL_CONFIGURE_FLAG="--arch=armv7-a --enable-armv5te --enable-armv6 --enable-armvfp --enable-memalign-hack"
	build_one
}

function build_arm_v7_vfpv3
{
        #arm tegra v7vfpv3
	CPU=armv7-a
	OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=vfpv3-d16 -march=$CPU"
	PREFIX=./$RESULTDIR/$CPU-vfpv3
	ADDITIONAL_CONFIGURE_FLAG="--arch=armv7-a --enable-armv5te --enable-armv6 --enable-armvfp --enable-memalign-hack"
	build_one
}

function build_arm_v7_neon
{
        #arm v7n
	CPU=armv7-a
	OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=neon -march=$CPU -mtune=cortex-a8 -Wno-multichar -fno-exceptions "
	PREFIX=./$RESULTDIR/$CPU 
	ADDITIONAL_CONFIGURE_FLAG="--arch=arm --cpu=armv7-a --enable-armv5te --enable-armv6 --enable-vfpv3 --enable-memalign-hack --enable-neon"
	build_one
}


function main
{
  #BZLIB_LDPATH=$BZLIB_LDPATHARM
  #build_arm_v5
  #cp config.h zg/v5/.
  #cp config.log zg/v5/.

  #build_arm_v6
  #cp config.h zg/v6/.
  #cp config.log zg/v6/.

  #build_arm_v6_vfp
  #cp config.h zg/v6vfp/.
  #cp config.log zg/v6vfp/.

  #build_arm_v7_vfp
  #cp config.h zg/v7vfp/.
  #cp config.log zg/v7vfp/.

  #build_arm_v7_vfpv3
  #cp config.h zg/v7vfpv3/.
  #cp config.log zg/v7vfpv3/.

  #BZLIB_LDPATH=$BZLIB_LDPATHARMV7
  build_arm_v7_neon
  #mkdir -p zg/v7neon
  #cp config.h zg/v7neon/.
  #cp config.log zg/v7neon/.

  #Strip the debug symbols after compilation using strip command
  #$PREBUILT/bin/arm-linux-androideabi-strip android/armv5te/libffmpeg.so
  #$PREBUILT/bin/arm-linux-androideabi-strip android/armv6/libffmpeg.so
  #$PREBUILT/bin/arm-linux-androideabi-strip android/armv6_vfp/libffmpeg.so

  #$PREBUILT/bin/arm-linux-androideabi-strip android/armv7-a-vfp/libffmpeg.so
  #$PREBUILT/bin/arm-linux-androideabi-strip android/armv7-a-vfpv3/libffmpeg.so
  $PREBUILT/bin/arm-linux-androideabi-strip $RESULTDIR/armv7-a/libffmpeg.so
}

main
