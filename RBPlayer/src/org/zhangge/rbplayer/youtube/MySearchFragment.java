package org.zhangge.rbplayer.youtube;

import java.util.List;

import org.zhangge.almightyzgbox_android.log.ZGLog;
import org.zhangge.almightyzgbox_android.net.video.YoutubeBox;
import org.zhangge.almightyzgbox_android.net.video.YoutubeBox.VideoEntity;
import org.zhangge.almightyzgbox_android.ui.widget.EasyClearEditText;
import org.zhangge.almightyzgbox_android.utils.CommonUtils;
import org.zhangge.rbplayer.R;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MySearchFragment extends Fragment {

	private View mTouchBack;
	private EasyClearEditText mSearchInput;
	private TextView mSearchcancel;
	private View mSearchView;
	private View mContentView;
	private ProgressBar mLoading;
	private boolean isShowing = false;
	private YoutubeVideoListFragment youtubeFragment;
	private boolean isStop = false;

	public static MySearchFragment getInstance() {
		return new MySearchFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onPause() {
		isStop = true;
		super.onPause();
	}

	@Override
	public void onStop() {
		isStop = true;
		super.onStop();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_search_youtube, container, false);

		mTouchBack = view.findViewById(R.id.touch_bg);
		mSearchcancel = (TextView) view.findViewById(R.id.search_cancel_tip);
		mSearchInput = (EasyClearEditText) view.findViewById(R.id.search_input);
		mSearchView = view.findViewById(R.id.search_fragment);
		mLoading = (ProgressBar) view.findViewById(R.id.loading);
		mContentView = view.findViewById(R.id.content_area);

		mLoading.setVisibility(View.GONE);
		mSearchInput.setFocusable(true);
		mSearchInput.setFocusableInTouchMode(true);
		mSearchInput.requestFocus();
		initListener();
		return view;
	}

	private void initListener() {
		mSearchInput.setSmartIconClickListener(EasyClearEditText.getDefaultSmartIconClickListener());
		mTouchBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				hideMe();
			}
		});
		mSearchcancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				hideMe();
			}
		});
		mSearchInput.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
					String keyword = mSearchInput.getText().toString();
					System.out.println("click to search = " + keyword);
					search(keyword);
					return true;
				}
				return false;
			}
		});
	}

	private void search(final String keyword) {
		mLoading.setVisibility(View.VISIBLE);
		new AsyncTask<String, Void, Void>() {

			private List<VideoEntity> videos;

			@Override
			protected Void doInBackground(String... params) {
				try {
					videos = YoutubeBox.queryYoutubeVideo(keyword);
				} catch (Exception e) {
					ZGLog.error(this, e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				hideMe();
				if(youtubeFragment != null && !isStop) {
					youtubeFragment.onQueryVideoResult(videos, true, keyword);
				}
				super.onPostExecute(result);
			}

		}.execute(keyword);
	}
	
	public void setYoutubeFragment(YoutubeVideoListFragment fragment) {
		youtubeFragment = fragment;
	}

	public boolean isShowing() {
		return isShowing;
	}

	public void showMe() {
		isShowing = true;
		if (mSearchView != null) {
			mSearchView.setVisibility(View.VISIBLE);
			mContentView.setVisibility(View.VISIBLE);
			CommonUtils.showIMEDelay(getActivity(), mSearchInput, 500);
		}
	}

	public void hideMe() {
		isShowing = false;
		if (mSearchView != null) {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(mSearchInput.getWindowToken(), 0);
			mLoading.setVisibility(View.GONE);
			mSearchView.setVisibility(View.GONE);
			if(getActivity().findViewById(R.id.ytlistview) != null)
				getActivity().findViewById(R.id.ytlistview).setVisibility(View.VISIBLE);
		}
	}

}
