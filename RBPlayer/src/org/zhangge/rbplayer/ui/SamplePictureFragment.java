package org.zhangge.rbplayer.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.zhangge.almightyzgbox_android.utils.CommonUtils;
import org.zhangge.almightyzgbox_android.utils.ZGConstant;
import org.zhangge.rbplayer.R;
import org.zhangge.rbplayer.utils.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class SamplePictureFragment extends BaseFragment {

	private ListView gListView;
	private SamplePicAdapter gAdapter;
	private List<File> gSamplePics;
	private Handler gHandler;
	
	public static SamplePictureFragment newInstance() {
		return new SamplePictureFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gHandler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_youtube_video_list, container, false);
		view.findViewById(R.id.search_fragment_container_content).setVisibility(View.GONE);
		view.findViewById(R.id.search_text_container).setVisibility(View.GONE);;
		gListView = (ListView) view.findViewById(R.id.youtubelist);
		gAdapter = new SamplePicAdapter(getActivity(), 0, new ArrayList<File>());
		gHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				gSamplePics = new ArrayList<File>();
				String destDir = ZGConstant.SDCARD_ROOT + CommonUtils.getApplicationName(getActivity()) + File.separator + MainActivity.SAMPLE_PATH;
				File dirs = new File(destDir);
				File[] files = dirs.listFiles();
				for (File file : files) {
					gSamplePics.add(file);
				}
				gAdapter.addAll(gSamplePics);
			}
		}, 1000);
		gListView.setAdapter(gAdapter);
		return view;
	}

	private class SamplePicAdapter extends BaseAdapter {

		private List<File> datas;

		public SamplePicAdapter(Context context, int resource, List<File> objects) {
			datas = objects;
		}

		public void addAll(List<File> objects) {
			datas.addAll(objects);
			this.notifyDataSetChanged();
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.samplepic_list_item, null);
				holder.leftContainer = convertView.findViewById(R.id.youtube_left_container);
				holder.leftIcon = (ImageView) convertView.findViewById(R.id.image_left);
				holder.rightContainer = convertView.findViewById(R.id.youtube_right_container);
				holder.rightIcon = (ImageView) convertView.findViewById(R.id.image_right);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			int index = position * 2;
			
			if(index < datas.size()) {
				final int pos = index;
				ImageLoader.getInstance().loadImage(datas.get(pos).getAbsolutePath(), holder.leftIcon, R.drawable.ic_video_default);
				holder.leftContainer.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});
			}
			index ++;
			if(index < datas.size()) {
				final int pos = index;
				ImageLoader.getInstance().loadImage(datas.get(pos).getAbsolutePath(), holder.rightIcon, R.drawable.ic_video_default);
				holder.rightContainer.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});
			}
            
			return convertView;
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
			View rightContainer;
			ImageView rightIcon;
		}
		
	}
}
