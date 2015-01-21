package org.zhangge.rbplayer.ui;

import java.util.ArrayList;
import java.util.List;

import org.zhangge.almightyzgbox_android.net.http.VolleyManager;
import org.zhangge.rbplayer.R;
import org.zhangge.rbplayer.bmob.SamplePic;
import org.zhangge.rbplayer.utils.BaseConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

	private ListView gListView;
	private SamplePicAdapter gAdapter;
	
	public static SamplePictureFragment newInstance() {
		return new SamplePictureFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_youtube_video_list, container, false);
		view.findViewById(R.id.search_fragment_container_content).setVisibility(View.GONE);
		view.findViewById(R.id.search_text_container).setVisibility(View.GONE);;
		gListView = (ListView) view.findViewById(R.id.youtubelist);
		gAdapter = new SamplePicAdapter(getActivity(), 0, new ArrayList<SamplePic>());
		gListView.setAdapter(gAdapter);
		gListView.setDivider(null);
		gAdapter.addAll(BaseConfig.getSamplePic());
		return view;
	}

	private class SamplePicAdapter extends BaseAdapter {

		private List<SamplePic> datas;

		public SamplePicAdapter(Context context, int resource, List<SamplePic> objects) {
			datas = objects;
		}

		public void addAll(List<SamplePic> objects) {
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
				holder.image = (ImageView) convertView.findViewById(R.id.image);
				holder.url = (TextView) convertView.findViewById(R.id.url);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String url = datas.get(position).getPic().getFileUrl(getActivity());
			holder.url.setText(url);
			VolleyManager.getInstance().loadImage(url, holder.image, R.drawable.red_blue_3d, R.drawable.red_blue_3d);
			holder.image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), PhotoActivity.class);
					intent.putExtra(PhotoActivity.URL_KEY, holder.url.getText().toString());
					getActivity().startActivity(intent);
				}
			});
            
			return convertView;
		}
		
		@Override
		public int getCount() {
			return datas.size();
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
			ImageView image;
			TextView url;
		}
		
	}
}
