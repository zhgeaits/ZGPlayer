package org.zhangge.zgplayer;

import java.io.IOException;

import org.zhangge.zgplayer.lib.LibZGPlayer;
import org.zhangge.zgplayer.test.CodecOutputSurface;
import org.zhangge.zgplayer.test.ExtractMpegFramesTest;
import org.zhangge.zgplayer.test.MediaPlayerActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class MainActivity extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LibZGPlayer.loadLibrary();
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			SurfaceView surface = (SurfaceView) rootView.findViewById(R.id.surface);
			surface.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					System.out.println("setVideoPath");
//					LibZGPlayer.setVideoPath("sdafasdf");
					Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
					startActivity(intent);
					
//					new Thread(new Runnable() {
//
//						@Override
//						public void run() {
//							try {
//								MediaPlayer player = new MediaPlayer();
//								player.reset();
//								player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//								player.setDataSource(Environment.getExternalStorageDirectory() + "/Tongli3D-II/videores/Dark Horse-1080p.mp4");
//								CodecOutputSurface ouputSurface = new CodecOutputSurface(500, 500, null);
//								player.setSurface(ouputSurface.getSurface());
//								player.prepare();
//								player.start();
//							} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
//								e.printStackTrace();
//							}
							
//						}
//						
//					}).start();
					
					
//					try {
//						new ExtractMpegFramesTest().extractMpegFrames();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
				}
			});
			surface.getHolder().addCallback(new Callback() {

				@Override
				public void surfaceCreated(SurfaceHolder holder) {
				}

				@Override
				public void surfaceChanged(SurfaceHolder holder, int format,
						int width, int height) {
					System.out.println("setSurface");
					LibZGPlayer.setSurface(holder.getSurface());
				}

				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					
				}
				
			});
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

}
