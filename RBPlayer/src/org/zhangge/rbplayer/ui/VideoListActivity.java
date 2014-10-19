package org.zhangge.rbplayer.ui;

import java.util.List;

import org.zhangge.rbplayer.R;
import org.zhangge.rbplayer.utils.MediaBox;
import org.zhangge.rbplayer.utils.MediaData;
import org.zhangge.rbplayer.utils.Navigation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class VideoListActivity extends Activity {

	private Context gContext;
	private VideoListAdapter gAdapter;
	private ListView gListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gContext = this;
		setContentView(R.layout.activity_video_list_loading);
		findVideos();
	}

	private void findVideos() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<MediaData> medias = MediaBox.searchVideos(gContext);
				setVideoList(medias);
			}
			
		}).start();
	}
	
	private void setVideoList(List<MediaData> medias) {
		setContentView(R.layout.activity_video_list);
		gListView = (ListView) findViewById(R.id.videolist);
		gAdapter = new VideoListAdapter(medias, gContext);
		gListView.setAdapter(gAdapter);
		gListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MediaData media = (MediaData) gAdapter.getItem(position);
				Navigation.toMediaPlayer(gContext, media.url);
			}
		});
	}
	
	private class VideoListAdapter extends BaseAdapter {

		private List<MediaData> medias;
		private Context context;
		
		public VideoListAdapter(List<MediaData> medias, Context context) {
			super();
			this.medias = medias;
			this.context = context;
		}

		@Override
		public int getCount() {
			return medias.size();
		}

		@Override
		public Object getItem(int position) {
			return medias.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MediaData media = medias.get(position);
			VideoListItemHolder holder;
			if(convertView == null) {
				holder = new VideoListItemHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.activity_video_list_item, null);
				holder.title = (TextView) convertView.findViewById(R.id.video_title);
				holder.url = (TextView) convertView.findViewById(R.id.video_url);
				convertView.setTag(holder);
			} else {
				holder = (VideoListItemHolder) convertView.getTag();
			}
			holder.title.setText(media.title);
			holder.url.setText(media.url);
			return convertView;
		}
		
		private class VideoListItemHolder {
			public TextView title;
			public TextView url;
		}
		
	}
	
}
