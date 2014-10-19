package org.zhangge.rbplayer.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class MediaBox {

	public static List<MediaData> searchVideos(Context context) {
		List<MediaData> medias = new ArrayList<MediaData>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
				null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
		
		boolean hasMore = cursor.moveToFirst();
		while(hasMore) {
			String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
			String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
			MediaData media = new MediaData();
			media.title = title;
			media.url = path;
			medias.add(media);
			hasMore = cursor.moveToNext();
		}
		
		cursor.close();
		return medias;
	}
}
