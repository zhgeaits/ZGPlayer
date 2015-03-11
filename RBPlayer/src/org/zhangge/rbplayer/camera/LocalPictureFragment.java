package org.zhangge.rbplayer.camera;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.zhangge.almightyzgbox_android.log.ZGLog;
import org.zhangge.rbplayer.ui.BaseFragment;
import org.zhangge.rbplayer.utils.AdUtils;
import org.zhangge.rbplayer.utils.BaseConfig;
import org.zhangge.rbplayer.utils.Navigation;
import org.zhangge.rbplayer.utils.SimpleImageLoader;
import org.zhangge.rbplayerpro.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class LocalPictureFragment extends BaseFragment {

	private ListView gListView;
	private LocalPicAdapter gAdapter;
	
	public static LocalPictureFragment newInstance() {
		return new LocalPictureFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_local_picture_list, container, false);
		gListView = (ListView) view.findViewById(R.id.localpiclist);
		gAdapter = new LocalPicAdapter();
		gListView.setAdapter(gAdapter);
		gListView.setDivider(null);
		AdUtils.addAdModBanner(getActivity(), view);
		loadLocalPic();
		return view;
	}
	
	private void onLoadPic(List<String> files) {
		gAdapter.addAll(files);
	}
	
	private void loadLocalPic() {
		gHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				String path = BaseConfig.getPicturePath();
				File dir = new File(path);
				if(dir.exists() && dir.isDirectory()) {
					List<String> fileArray = new ArrayList<String>();
					File[] files = dir.listFiles();
					for (File file : files) {
						String filePath = file.getAbsolutePath();
						if(!filePath.endsWith("-2.jpg")) {
							fileArray.add(filePath);
						}
					}
					onLoadPic(fileArray);
				} else {
					ZGLog.info(this, "not exists, not directory!");
				}
			}
			
		}, 0);
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
	
	private class LocalPicAdapter extends BaseAdapter {

		private List<String> datas = new ArrayList<String>();
		
		public void addAll(List<String> objects) {
			datas.addAll(objects);
			notifyDataSetChanged();
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.fragment_local_pic_list_item, null);
				holder.image = (ImageView) convertView.findViewById(R.id.image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final String path = datas.get(position);
			SimpleImageLoader.getInstance().loadImage(path, holder.image, R.drawable.red_blue_3d);
			holder.image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Navigation.toPhotoActivity(getActivity(), null, path);
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
		}
		
	}
}
