package org.zhangge.rbplayer.youtube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zhangge.almightyzgbox_android.log.ZGLog;
import org.zhangge.almightyzgbox_android.net.video.YoutubeBox;
import org.zhangge.almightyzgbox_android.net.video.YoutubeBox.VideoEntity;
import org.zhangge.almightyzgbox_android.net.video.YoutubeBox.VideoStream;
import org.zhangge.almightyzgbox_android.utils.ZGTask;
import org.zhangge.rbplayer.R;
import org.zhangge.rbplayer.ui.BaseFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class YoutubeVideoListFragment extends BaseFragment {

	private static String SEARCH_TAG = "SEARCH_LISTIM_FRAGMENT_TAG";
	private static int SUB_ID = R.id.search_fragment_container_content;
	public static Map<String, List<VideoStream>> WEBURL_URLS = new HashMap<String, List<VideoStream>>();
	public static Map<String, List<VideoStream>> VIDEOID_URLS = new HashMap<String, List<VideoStream>>();
	public static String gCurrentId;
	public static String gCurrentWebUrl;
	private static Map<String, String> TITLE_VIDEOID = new HashMap<String, String>();
	private static Map<String, String> TITLE_WEBURL = new HashMap<String, String>();

	private Activity context;
	private View search;
	private MySearchFragment searchFragment;
	private ListView youtubeList;
	private YoutubeListAdapter adapter;
	private View mList;

	public static YoutubeVideoListFragment newInstance() {
		return new YoutubeVideoListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		// defaultSearch("yt3d");
		// defaultSearch("SBS3D");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_youtube_video_list, container, false);
		search = view.findViewById(R.id.search_text_container);
		youtubeList = (ListView) view.findViewById(R.id.youtubelist);
		mList = view.findViewById(R.id.ytlistview);
		adapter = new YoutubeListAdapter(getActivity(), 0, new ArrayList<VideoEntity>());
		youtubeList.setAdapter(adapter);
		initListener();
		return view;
	}

	public void hideSearchFragment() {
		if (searchFragment != null && searchFragment.isShowing()) {
			searchFragment.hideMe();
			return;
		}
	}

	public boolean isShowingSearch() {
		if(searchFragment != null) {
			return searchFragment.isShowing();
		}
		return false;
	}

	private void defaultSearch(String keyword) {
		new AsyncTask<String, Void, Void>() {

			private List<VideoEntity> videos;

			@Override
			protected Void doInBackground(String... params) {
				try {
					ZGLog.info(this, "defaultSearch keyword=" + params[0]);
					videos = YoutubeBox.queryYoutubeVideo(params[0]);
				} catch (Exception e) {
					ZGLog.error(this, e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				onQueryVideoResult(videos, false);
			}

		}.execute(keyword);
	}

	private void initListener() {
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchFragment = creatSearchFragment(context);
				searchFragment.showMe();
			}
		});
		youtubeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				clickToPlay(view);
			}

			private void clickToPlay(final View view) {
				final ViewHolder holder = (ViewHolder) view.getTag();
				final String videoId = TITLE_VIDEOID.get(holder.title.getText().toString());
				final String webUrl = TITLE_WEBURL.get(holder.title.getText().toString());
				final List<VideoStream> urls = WEBURL_URLS.get(webUrl);
				gCurrentId = videoId;
				gCurrentWebUrl = webUrl;
				if (urls == null || urls.size() == 0) {
					ZGLog.info(this, "no url start a new task to get url videoId=%s", videoId);
					ZGTask.getInstance().postDelayed(new CalUrlTask(webUrl, videoId, new CalUrlTaskListener() {
						@Override
						public void onOK() {
							// List<VideoStream> urls = WEBURL_URLS.get(webUrl);
							List<VideoStream> urls = VIDEOID_URLS.get(videoId);
							play(null, holder.title.getText().toString(), urls);
							ZGLog.info(this, "play Url=" + urls);
						}

						@Override
						public void onError(String msg) {
							ZGLog.info(this, "error:" + msg + ", try again");
							clickToPlay(view);
						}
					}), 0);
				} else {
					play(null, holder.title.getText().toString(), urls);
					ZGLog.info(this, "play Url=" + urls);
				}
			}

			private void play(final String url, final String title, final List<VideoStream> urls) {
			}
		});
	}

	public void onQueryVideoResult(List<VideoEntity> videos, boolean isFlush) {
		if (videos == null) {
			return;
		}
		ZGLog.info(this, "onQueryVideoResult isFlush=" + isFlush + ",videos=" + videos);
		if (isFlush) {
			adapter.setData(videos);
		} else {
			adapter.addAll(videos);
		}
	}

	public MySearchFragment creatSearchFragment(Activity context) {
		FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
		Fragment creatFragment = fm.findFragmentByTag(SEARCH_TAG);
//		if (f != null) {
//			return (MySearchFragment) f;
//		}
		if(creatFragment == null) {
			creatFragment = MySearchFragment.getInstance();
		}

		fm.beginTransaction().replace(SUB_ID, creatFragment, SEARCH_TAG).commit();

		return (MySearchFragment) creatFragment;
	}

	private class YoutubeListAdapter extends ArrayAdapter<VideoEntity> {

		private List<VideoEntity> datas;

		public YoutubeListAdapter(Context context, int resource, List<VideoEntity> objects) {
			super(context, resource, objects);
			datas = objects;
		}

		public void setData(List<VideoEntity> objects) {
			datas.clear();
			datas.addAll(objects);
			this.notifyDataSetChanged();
		}

		public void addAll(List<VideoEntity> objects) {
			datas.addAll(objects);
			this.notifyDataSetChanged();
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
				convertView = inflater.inflate(R.layout.youtube_list_item, null);
				holder.icon = (ImageView) convertView.findViewById(R.id.image);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText(getItem(position).getTitle());
			String webUrl = getItem(position).getPlayUrl();
			TITLE_WEBURL.put(getItem(position).getTitle(), webUrl);
			getDownloadUrl(webUrl, getItem(position).getTitle(), getItem(position).getVideoId());
			// VolleyManager.getInstance().loadImage(getItem(position).getSqThumbnail(),
			// holder.icon, defaultImage, failedImage);
			return convertView;
		}

		private void getDownloadUrl(String origUrl, String title, String videoId) {
			if (TITLE_VIDEOID.get(title) == null) {
				TITLE_VIDEOID.put(title, videoId);
			}
		}
	}

	private class CalUrlTask implements Runnable {
		private String videoId;
		private CalUrlTaskListener listener;
		private String webUrl;

		public CalUrlTask(String webUrl, String videoId, CalUrlTaskListener listener) {
			this.videoId = videoId;
			this.listener = listener;
			this.webUrl = webUrl;
		}

		@Override
		public void run() {
			try {
				ZGLog.info(this, "begin to getYouTubeUrlFromFmtVideoId videoId=" + videoId);
				List<VideoStream> getUrlsFromId = YoutubeBox.getAllYouTubeFromVideoId(videoId);
				if (getUrlsFromId == null || getUrlsFromId.size() == 0) {
					ZGLog.info(this, "do not get target url from videoid");
				} else {
					VIDEOID_URLS.put(videoId, getUrlsFromId);
				}

				ZGLog.info(this, "begin to getYoutubeUrlFromWebUrl webUrl=" + webUrl);
				List<VideoStream> getUrlsFromWebUrl = YoutubeBox.getYoutubeUrlFromWebUrl(webUrl);
				if (getUrlsFromWebUrl == null || getUrlsFromWebUrl.size() == 0) {
					ZGLog.info(this, "this weburl, I have no idea how to get the stream url. webUrl:" + webUrl);
				} else {
					WEBURL_URLS.put(webUrl, getUrlsFromWebUrl);
				}

				if ((getUrlsFromId == null || getUrlsFromId.size() == 0) && (getUrlsFromWebUrl == null || getUrlsFromWebUrl.size() == 0)) {
					if (listener != null) {
						listener.onError("can not play");
					}
				} else {
					if (listener != null) {
						listener.onOK();
					}
				}

			} catch (Exception e) {
				ZGLog.error(this, e);
			}
		}
	}

	public interface CalUrlTaskListener {
		public void onOK();

		public void onError(String msg);
	}

	private class ViewHolder {
		ImageView icon;
		TextView title;
	}

}
