package org.zhangge.rbplayer.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.zhangge.almightyzgbox_android.log.ZGLog;
import org.zhangge.almightyzgbox_android.utils.CommonUtils;
import org.zhangge.almightyzgbox_android.utils.ZGConstant;
import org.zhangge.almightyzgbox_android.utils.ZGPreference;
import org.zhangge.almightyzgbox_android.utils.ZGTask;
import org.zhangge.rbplayer.youtube.YoutubeVideoListFragment;
import org.zhangge.rbplayerpro.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	public static final String LOCAL_VIDEO_TAG = "local_video_tag";
	public static final String YOUTUBE_VIDEO_TAG = "youtube_video_tag";
	public static final String SAMPLE_PIC_TAG = "sample_pic_tag";
	public static final String SAMPLE_PATH = "samplepics";

	private Context gContext;
	private DrawerLayout gDrawerLayout;
	private ListView gDrawerList;
	private ActionBarDrawerToggle gDrawerToggle;

	private CharSequence gDrawerTitle;
	private CharSequence gTitle;
	private String[] gMenuTitles;

	private int currentItem;
	private Fragment currentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gContext = this;
		
		setContentView(R.layout.activity_main);
		
		gTitle = gDrawerTitle = getTitle();
		gMenuTitles = getResources().getStringArray(R.array.menu_array);
		gDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		gDrawerList = (ListView) findViewById(R.id.left_drawer);

		// 设置阴影效果
		gDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		gDrawerList.setAdapter(new LeftDrawerAdapter(gContext, Arrays.asList(gMenuTitles)));
		gDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		if(getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setHomeButtonEnabled(true);
		}

		gDrawerToggle = new ActionBarDrawerToggle(this, gDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				if(getActionBar() != null) {
					getActionBar().setTitle(gTitle);
					invalidateOptionsMenu();
				}
			}

			public void onDrawerOpened(View drawerView) {
				if(getActionBar() != null) {
					getActionBar().setTitle(gDrawerTitle);
					invalidateOptionsMenu();
				}
			}
		};
		gDrawerLayout.setDrawerListener(gDrawerToggle);

		if (savedInstanceState == null) {
			//selectItem(1);
			selectItem(0);
		}

		boolean firstUse = ZGPreference.getInstance().getBoolean("firstUse", true);
		if(firstUse) {
			ZGPreference.getInstance().putBoolean("firstUse", false);
			ZGTask.getInstance().postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						String fileNames[] = getAssets().list(SAMPLE_PATH);
						String destDir = ZGConstant.SDCARD_ROOT + CommonUtils.getApplicationName(gContext) + File.separator + SAMPLE_PATH;
						File fileDir = new File(destDir);
						if(!fileDir.exists()) {
							fileDir.mkdirs();
						}
						for (String path : fileNames) {
							ZGLog.info(this, "copy sample pictures:" + path);
							copy(gContext, SAMPLE_PATH + File.separator + path, destDir + File.separator + path);
						}
					} catch (Exception e) {
						ZGLog.error(this, e);
					}
				}
				
				private void copy(Context context, String oldPath, String newPath) {
					InputStream is;
					try {
						is = context.getAssets().open(oldPath);
						FileOutputStream fos = new FileOutputStream(new File(newPath));
						byte[] buffer = new byte[1024];
						int byteCount = 0;
						while ((byteCount = is.read(buffer)) != -1) {
							fos.write(buffer, 0, byteCount);
						}
						fos.flush();
						is.close();
						fos.close();
					} catch (IOException e) {
						ZGLog.error(this, e);
					}
				}
			}, 0);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// 当我们调用invalidateOptionsMenu()方法时候会调到这里
		// 在这里可以根据drawerOpen是否打开了来隐藏右上角的按钮菜单
		boolean drawerOpen = gDrawerLayout.isDrawerOpen(gDrawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	private void selectItem(int position) {
		FragmentManager fragmentManager = getFragmentManager();
		String tag = null;
		/*if (currentItem == 1 && position != 1) {
			YoutubeVideoListFragment youtubeFragment = (YoutubeVideoListFragment) currentFragment;
			youtubeFragment.hideSearchFragment();
		}
		currentItem = position;*/
		Fragment toShow;
		switch (position) {
		case 0:
			tag = LOCAL_VIDEO_TAG;
			toShow = fragmentManager.findFragmentByTag(tag);
			if (toShow == null) {
				toShow = VideoListLocalFragment.newInstance();
			}
			break;
		case 1:
			/*tag = YOUTUBE_VIDEO_TAG;
			toShow = fragmentManager.findFragmentByTag(tag);
			if (toShow == null) {
				toShow = YoutubeVideoListFragment.newInstance();
			}
			break;
		case 2:*/
			tag = SAMPLE_PIC_TAG;
			toShow = fragmentManager.findFragmentByTag(tag);
			if (toShow == null) {
				toShow = SamplePictureFragment.newInstance();
			}
			break;
		default:
			tag = LOCAL_VIDEO_TAG;
			toShow = fragmentManager.findFragmentByTag(tag);
			if (toShow == null) {
				toShow = VideoListLocalFragment.newInstance();
			}
			break;
		}
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		
		if(toShow != currentFragment && currentFragment != null) {
			ft.hide(currentFragment);
		}
		currentFragment = toShow;
		if (toShow.isDetached()) {
			ft.attach(toShow);
        } else if (!toShow.isAdded()) {
        	ft.add(R.id.content_frame, toShow, tag);
        } else if (toShow.isHidden()) {
        	ft.show(toShow);
        }
		
		int result = ft.commitAllowingStateLoss();
        if (result < 0) {
            getSupportFragmentManager().executePendingTransactions();
        }

		gDrawerList.setItemChecked(position, true);
		setTitle(gMenuTitles[position]);
		gDrawerLayout.closeDrawer(gDrawerList);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (gDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setTitle(CharSequence title) {
		gTitle = title;
		if(getActionBar() != null) {
			getActionBar().setTitle(gTitle);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// 使用ActionBarDrawerToggle就必须在这里调用这个
		gDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// 使用ActionBarDrawerToggle就必须在这里调用这个
		gDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		if (currentItem == 1) {
			YoutubeVideoListFragment youtubeFragment = (YoutubeVideoListFragment) currentFragment;
			if (youtubeFragment.isShowingSearch()) {
				youtubeFragment.hideSearchFragment();
				return;
			}
		}
		if (currentItem != 0) {
			selectItem(0);
			return;
		}
		super.onBackPressed();
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private class LeftDrawerAdapter extends BaseAdapter {
		private Context gContext;
		private List<String> datas;

		private LeftDrawerAdapter(Context gContext, List<String> datas) {
			this.gContext = gContext;
			this.datas = datas;
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String titleStr = datas.get(position);
			convertView = LayoutInflater.from(gContext).inflate(R.layout.left_drawer_item, null);
			TextView title = (TextView) convertView.findViewById(R.id.menu_title);
			title.setText(titleStr);
			return convertView;
		}

	}

}
