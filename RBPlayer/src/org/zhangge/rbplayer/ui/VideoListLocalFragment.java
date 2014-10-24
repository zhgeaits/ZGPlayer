package org.zhangge.rbplayer.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.zhangge.rbplayer.R;
import org.zhangge.rbplayer.utils.MediaBox;
import org.zhangge.rbplayer.utils.MediaData;
import org.zhangge.rbplayer.utils.Navigation;

import java.util.List;


/**
 * Created by zhangge on 2014/10/24.
 */
public class VideoListLocalFragment extends BaseFragment {

    private ProgressBar gLoading;
    private ListView gListView;
    private Context gContext;
    private VideoListAdapter gAdapter;

    public static VideoListLocalFragment getInstance() {
        return new VideoListLocalFragment();
    }

    public VideoListLocalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_video_list, container, false);
        gLoading = (ProgressBar) rootView.findViewById(R.id.loading);
        gListView = (ListView) rootView.findViewById(R.id.localvideolist);
        gListView.setVisibility(View.GONE);
        gLoading.setVisibility(View.VISIBLE);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        findVideos();
    }

    private void findVideos() {
        gHandler.post(new Runnable() {
            @Override
            public void run() {
                List<MediaData> medias = MediaBox.searchVideos(gContext);
                setVideoList(medias);
            }

        });
    }

    private void setVideoList(List<MediaData> medias) {
        gListView.setVisibility(View.VISIBLE);
        gLoading.setVisibility(View.GONE);
        gAdapter = new VideoListAdapter(medias, gContext);
        gListView.setAdapter(gAdapter);
        gListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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