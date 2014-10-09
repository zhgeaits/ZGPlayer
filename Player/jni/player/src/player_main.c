#include <player_main.h>

PlayerContext player;

int player_init(char* videoFileName) {
	AVCodec *pCodec = NULL;
	int i;
	AVDictionary *optionsDict = NULL;

	// Register all formats and codecs
	av_register_all();
	// Open video file
	if (avformat_open_input(&player.formatCtx, videoFileName, NULL, NULL) != 0)
		return -1; // Couldn't open file
	// Retrieve stream information
	if (avformat_find_stream_info(player.formatCtx, NULL) < 0)
		return -1; // Couldn't find stream information
	// Dump information about file onto standard error
	av_dump_format(player.formatCtx, 0, videoFileName, 0);
	// Find the first video stream
	player.videoStream = -1;
	for (i = 0; i < player.formatCtx->nb_streams; i++) {
		if (player.formatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
			player.videoStream = i;
			break;
		}
	}
	if (player.videoStream == -1)
		return -1; // Didn't find a video stream
	// Get a pointer to the codec context for the video stream
	player.codecCtx = player.formatCtx->streams[player.videoStream]->codec;
	// Find the decoder for the video stream
	pCodec = avcodec_find_decoder(player.codecCtx->codec_id);
	if (pCodec == NULL) {
		fprintf(stderr, "Unsupported codec!\n");
		return -1; // Codec not found
	}
	// Open codec
	if (avcodec_open2(player.codecCtx, pCodec, &optionsDict) < 0)
		return -1; // Could not open codec
	// Allocate video frame
	player.decodedFrame = avcodec_alloc_frame();
	// Allocate an AVFrame structure
	player.frameRGBA = avcodec_alloc_frame();
	if (player.frameRGBA == NULL)
		return -1;
	return 0;
}

jobject player_create_bitmap(JNIEnv *env, int width, int height) {
	int i;
	jclass javaBitmapClass = (jclass) (*env)->FindClass(env, "android/graphics/Bitmap");
	jmethodID mid = (*env)->GetStaticMethodID(env, javaBitmapClass, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
	//create Bitmap.Config
	//reference: https://forums.oracle.com/thread/1548728
	const wchar_t* configName = L"ARGB_8888";
	int len = wcslen(configName);
	jstring jConfigName;
	if (sizeof(wchar_t) != sizeof(jchar)) {
		//wchar_t is defined as different length than jchar(2 bytes)
		jchar* str = (jchar*) malloc((len + 1) * sizeof(jchar));
		for (i = 0; i < len; ++i) {
			str[i] = (jchar) configName[i];
		}
		str[len] = 0;
		jConfigName = (*env)->NewString(env, (const jchar*) str, len);
	} else {
		jConfigName = (*env)->NewString(env, (const jchar*) configName, len);
	}
	jclass bitmapConfigClass = (*env)->FindClass(env, "android/graphics/Bitmap$Config");
	jobject javaBitmapConfig = (*env)->CallStaticObjectMethod(env, bitmapConfigClass,
			(*env)->GetStaticMethodID(env, bitmapConfigClass, "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;"), jConfigName);
	//create the bitmap
	return (*env)->CallStaticObjectMethod(env, javaBitmapClass, mid, width, height, javaBitmapConfig);
}

void player_setSurface(JNIEnv *env, jobject surface) {
	if (0 != surface) {
		// get the native window reference
		player.window = ANativeWindow_fromSurface(env, surface);
		// set format and size of window buffer
		ANativeWindow_setBuffersGeometry(player.window, 0, 0, WINDOW_FORMAT_RGBA_8888);
	} else {
		// release the native window
		ANativeWindow_release(player.window);
	}
}

int player_setSize(JNIEnv *env, int width, int height) {
	player.width = width;
	player.height = height;
	player.bitmap = player_create_bitmap(env, width, height);
	if (AndroidBitmap_lockPixels(env, player.bitmap, &player.buffer) < 0)
		return -1;
	player.sws_ctx = sws_getContext(player.codecCtx->width, player.codecCtx->height, player.codecCtx->pix_fmt, width, height, AV_PIX_FMT_RGBA,
	SWS_BILINEAR,
	NULL,
	NULL,
	NULL);
	// Assign appropriate parts of bitmap to image planes in pFrameRGBA
	// Note that pFrameRGBA is an AVFrame, but AVFrame is a superset
	// of AVPicture
	avpicture_fill((AVPicture *) player.frameRGBA, player.buffer, AV_PIX_FMT_RGBA, width, height);
	return 0;
}

void player_finish(JNIEnv *env) {
	//unlock the bitmap
	AndroidBitmap_unlockPixels(env, player.bitmap);
	av_free(player.buffer);
	// Free the RGB image
	av_free(player.frameRGBA);
	// Free the YUV frame
	av_free(player.decodedFrame);
	// Close the codec
	avcodec_close(player.codecCtx);
	// Close the video file
	avformat_close_input(&player.formatCtx);
}

void player_decodeAndRenderThread(JNIEnv *env) {
	ANativeWindow_Buffer windowBuffer;
	AVPacket packet;
	int i = 0;
	int frameFinished;
	int lineCnt;
	while (av_read_frame(player.formatCtx, &packet) >= 0) {
		// Is this a packet from the video stream?
		if (packet.stream_index == player.videoStream) {
			// Decode video frame
			avcodec_decode_video2(player.codecCtx, player.decodedFrame, &frameFinished, &packet);
			// Did we get a video frame?
			if (frameFinished) {
				// Convert the image from its native format to RGBA
				sws_scale(player.sws_ctx, (uint8_t const * const *) player.decodedFrame->data, player.decodedFrame->linesize, 0, player.codecCtx->height, player.frameRGBA->data,
						player.frameRGBA->linesize);
				// lock the window buffer
				if (ANativeWindow_lock(player.window, &windowBuffer, NULL) < 0) {
					LOGE("cannot lock window");
				} else {
					// draw the frame on buffer
					LOGI("copy buffer %d:%d:%d", player.width, player.height, player.width * player.height * 4);
					LOGI("window buffer: %d:%d:%d", windowBuffer.width, windowBuffer.height, windowBuffer.stride);
					memcpy(windowBuffer.bits, player.buffer, player.width * player.height * 4);
					// unlock the window buffer and post it to display
					ANativeWindow_unlockAndPost(player.window);
					// count number of frames
					++i;
				}
			}
		}
		// Free the packet that was allocated by av_read_frame
		av_free_packet(&packet);
	}
	LOGI("total No. of frames decoded and rendered %d", i);
	//finish(env);
}

void play(JNIEnv *env, jobject pObj) {
	pthread_t decodeThread;
	pthread_create(&decodeThread, NULL, (void * (*)(void *))player_decodeAndRenderThread, env);
}
