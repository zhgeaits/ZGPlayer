package org.zhangge.rbplayer.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.zhangge.rbplayer.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SamplePictureFragment extends BaseFragment {

	public static final String SAMPLE_PATH = "samplepics";
	private ListView gListView;
	private SamplePicAdapter gAdapter;
	private List<File> gSamplePics;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			String fileNames[] = getActivity().getAssets().list(SAMPLE_PATH);
			gSamplePics = new ArrayList<File>();
			for (String path : fileNames) {
				File file = new File(path);
				gSamplePics.add(file);
			}
		} catch (Exception e) {
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_youtube_video_list, container, false);
		view.findViewById(R.id.search_fragment_container_content).setVisibility(View.GONE);
		view.findViewById(R.id.search_text_container).setVisibility(View.GONE);;
		gListView = (ListView) view.findViewById(R.id.youtubelist);
		gAdapter = new SamplePicAdapter(getActivity(), 0, new ArrayList<File>());
		gAdapter.addAll(gSamplePics);
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

		@SuppressLint({"InflateParams", "deprecation"})
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.youtube_list_item, null);
				holder.leftContainer = convertView.findViewById(R.id.youtube_left_container);
				holder.leftIcon = (ImageView) convertView.findViewById(R.id.image_left);
				holder.leftTitle = (TextView) convertView.findViewById(R.id.title_left);
				holder.midContainer = convertView.findViewById(R.id.youtube_mid_container);
				holder.midIcon = (ImageView) convertView.findViewById(R.id.image_mid);
				holder.midTitle = (TextView) convertView.findViewById(R.id.title_mid);
				holder.rightContainer = convertView.findViewById(R.id.youtube_right_container);
				holder.rightIcon = (ImageView) convertView.findViewById(R.id.image_right);
				holder.rightTitle = (TextView) convertView.findViewById(R.id.title_right);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.leftTitle.setVisibility(View.GONE);
			holder.midTitle.setVisibility(View.GONE);
			holder.rightTitle.setVisibility(View.GONE);
			
			int index = position * 3;
			
			if(index < datas.size()) {
				final int pos = index;
				holder.leftContainer.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Drawable drawable = Drawable.createFromPath(datas.get(pos).getAbsolutePath());
						holder.leftIcon.setBackgroundDrawable(drawable);
					}
				});
			}
			index ++;
			if(index < datas.size()) {
				final int pos = index;
				holder.midContainer.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});
			}
			if(index < datas.size()) {
				final int pos = index;
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
			int count = size / 3;
			if(size % 3 != 0) {
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
			View midContainer;
			ImageView midIcon;
			TextView midTitle;
			View rightContainer;
			ImageView rightIcon;
			TextView rightTitle;
		}
		
	}
}
