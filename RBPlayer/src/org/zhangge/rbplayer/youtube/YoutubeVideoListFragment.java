package org.zhangge.rbplayer.youtube;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zhangge.almightyzgbox_android.log.ZGLog;
import org.zhangge.almightyzgbox_android.net.http.VolleyManager;
import org.zhangge.almightyzgbox_android.net.video.YoutubeBox;
import org.zhangge.almightyzgbox_android.net.video.YoutubeBox.VideoEntity;
import org.zhangge.almightyzgbox_android.net.video.YoutubeBox.VideoStream;
import org.zhangge.almightyzgbox_android.utils.ZGTask;
import org.zhangge.rbplayer.R;
import org.zhangge.rbplayer.ui.BaseFragment;
import org.zhangge.rbplayer.utils.AdUtils;
import org.zhangge.rbplayer.utils.Navigation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class YoutubeVideoListFragment extends BaseFragment {

	private static int SUB_ID = R.id.search_fragment_container_content;
	public static Map<String, List<VideoStream>> VIDEOID_URLS = new HashMap<String, List<VideoStream>>();

	private Activity context;
	private View search;
	private MySearchFragment searchFragment;
	private ListView youtubeList;
	private YoutubeListAdapter adapter;
	private AlertDialog.Builder builder;
	private Dialog dialog;

	public static YoutubeVideoListFragment newInstance() {
		return new YoutubeVideoListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		defaultSearch("yt3d SBS3D");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_youtube_video_list, container, false);
		search = view.findViewById(R.id.search_text_container);
		youtubeList = (ListView) view.findViewById(R.id.youtubelist);
		adapter = new YoutubeListAdapter(getActivity(), 0, new ArrayList<VideoEntity>());
		youtubeList.setAdapter(adapter);
		initListener();
		AdUtils.addAdModBanner(getActivity(), view);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		AdUtils.onResume();
	}

	@Override
	public void onPause() {
		AdUtils.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		AdUtils.onDestory();
		super.onDestroy();
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

	private void defaultSearch(final String keyword) {
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
				onQueryVideoResult(videos, false, keyword);
			}

		}.execute(keyword);
	}

	private void initListener() {
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchFragment = creatSearchFragment(context);
				searchFragment.setYoutubeFragment(YoutubeVideoListFragment.this);
			}
		});
	}

	public void onQueryVideoResult(List<VideoEntity> videos, boolean isFlush, String keyWord) {
		if (videos == null) {
			return;
		}
		adapter.setKeyWord(keyWord);
		ZGLog.info(this, "onQueryVideoResult isFlush=" + isFlush + ",videos=" + videos);
		if (isFlush) {
			adapter.setData(videos);
		} else {
			adapter.addAll(videos);
		}
	}

	public MySearchFragment creatSearchFragment(Activity context) {
		FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
		Fragment creatFragment = MySearchFragment.getInstance();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(SUB_ID, creatFragment);
		transaction.addToBackStack(null);
		int result = transaction.commit();
        if (result < 0) {
            fm.executePendingTransactions();
        }
		return (MySearchFragment) creatFragment;
	}

	private class YoutubeListAdapter extends BaseAdapter {

		private List<VideoEntity> datas;
		private String searchKey;

		public YoutubeListAdapter(Context context, int resource, List<VideoEntity> objects) {
			datas = objects;
		}

		public void setKeyWord(String keyword) {
			this.searchKey = keyword;
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
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.youtube_list_item, null);
				holder.leftContainer = convertView.findViewById(R.id.youtube_left_container);
				holder.leftIcon = (ImageView) convertView.findViewById(R.id.image_left);
				holder.leftTitle = (TextView) convertView.findViewById(R.id.title_left);
				holder.rightContainer = convertView.findViewById(R.id.youtube_right_container);
				holder.rightIcon = (ImageView) convertView.findViewById(R.id.image_right);
				holder.rightTitle = (TextView) convertView.findViewById(R.id.title_right);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			int index = position * 2;
			
			if(index < datas.size()) {
				SpannableString leftSpan = parseString(datas.get(index).getTitle());
				holder.leftTitle.setText(leftSpan);
				VolleyManager.getInstance().loadImage(datas.get(index).getSqThumbnail(),
						 holder.leftIcon, R.drawable.ic_video_default, R.drawable.ic_video_default);
				final int pos = index;
				holder.leftContainer.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						loadYoutube(datas.get(pos).getVideoId());
					}
				});
			}
			index ++;
			if(index < datas.size()) {
				SpannableString rightSpan = parseString(datas.get(index).getTitle());
				holder.rightTitle.setText(rightSpan);
				VolleyManager.getInstance().loadImage(datas.get(index).getSqThumbnail(),
						 holder.rightIcon, R.drawable.ic_video_default, R.drawable.ic_video_default);
				final int pos = index;
				holder.rightContainer.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						loadYoutube(datas.get(pos).getVideoId());
					}
				});
			}
            
			return convertView;
		}
		
		private void loadYoutube(final String videoId) {
			showProgressDialog(getActivity().getString(R.string.loading));
			ZGTask.getInstance().postDelayed(new CalUrlTask(videoId, new CalUrlTaskListener() {
				@Override
				public void onOK() {
					dialog.dismiss();
					List<VideoStream> urls = VIDEOID_URLS.get(videoId);
					if(urls != null && urls.size() > 0) {
						String url = urls.get(0).getUrl();
						if(!url.startsWith("http://") || !url.startsWith("https://")) {
							try {
								url = URLDecoder.decode(url, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								ZGLog.error(this, "decode url error=" + e);
							}
						}
						ZGLog.info(this, "goto play youtube url=" + url);
						Navigation.toMediaPlayer(getActivity(), url);
					}
				}
				
				@Override
				public void onError(String msg) {
					dialog.dismiss();
					Toast.makeText(context, R.string.load_error, Toast.LENGTH_LONG).show();
				}
			}), 0);
		}
		
		private SpannableString parseString(String title) {
			SpannableString span = new SpannableString(title);
            for(int j = 0; j < title.length() - searchKey.length() + 1; j++) {
                int start = j, end = start + searchKey.length();
                String temp = "";
                if(end <= (title.length() - 1)) {
                    temp = title.substring(start, end);
                } else {
                    temp = title.substring(start);
                }
                if(temp.equalsIgnoreCase(searchKey)) {
                    span.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFF8900")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    j += searchKey.length() - 1;
                }
            }
            if(title.length() == searchKey.length() && title.equalsIgnoreCase(searchKey)) {
                span.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFF8900")), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return span;
		}

		@Override
		public int getCount() {
			int size = datas.size();
			int count = size / 2;
			if(size % 2 != 0) {
				count += 1;
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		private class ViewHolder {
			View leftContainer;
			ImageView leftIcon;
			TextView leftTitle;
			View rightContainer;
			ImageView rightIcon;
			TextView rightTitle;
		}
		
	}

	private class CalUrlTask implements Runnable {
		private String videoId;
		private CalUrlTaskListener listener;

		public CalUrlTask(String videoId, CalUrlTaskListener listener) {
			this.videoId = videoId;
			this.listener = listener;
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

				if (getUrlsFromId == null || getUrlsFromId.size() == 0) {
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
	
	public void showProgressDialog(String msg) {
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
        if (null != context)
        	dialog.show();
        dialog.setContentView(R.layout.layout_progress_dialog);
        TextView tvTip = (TextView) dialog.findViewById(R.id.tv_tip);
        tvTip.setText(msg);
    }
}
