package org.zhangge.rbplayer.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zhangge.almightyzgbox_android.log.ZGLog;
import org.zhangge.almightyzgbox_android.utils.ZGPreference;
import org.zhangge.almightyzgbox_android.utils.ZGTask;
import org.zhangge.rbplayer.bmob.RBSwithcer;
import org.zhangge.rbplayer.camera.LocalPictureFragment;
import org.zhangge.rbplayer.utils.BaseConfig;
import org.zhangge.rbplayer.utils.Navigation;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	public static final String LOCAL_VIDEO_TAG = "local_video_tag";
	public static final String YOUTUBE_VIDEO_TAG = "youtube_video_tag";
	public static final String LOCAL_PIC_TAG = "local_pic_tag";
	public static final String SAMPLE_PIC_TAG = "sample_pic_tag";
	public static final String SBS_PATH_TAG = "sbs_path";
	public static final String SAMPLE_PATH = "samples";
	
	private int indexVideo = 0;
	private int indexLocalPic = 1;
	private int indexYoutubeVideo = 2;
	private int indexSample = 3;
	private int indexSBS = 4;

	private Context gContext;
	private DrawerLayout gDrawerLayout;
	private ListView gDrawerList;
	private ActionBarDrawerToggle gDrawerToggle;

	private CharSequence gDrawerTitle;
	private CharSequence gTitle;
	private String[] gMenuTitles;
	private List<String> gMenuTitlesList;

	private boolean gNoYoutube;
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

		gMenuTitlesList = Arrays.asList(gMenuTitles);
		gMenuTitlesList = modifyTitles(gMenuTitlesList);
		gDrawerList.setAdapter(new LeftDrawerAdapter(gContext, gMenuTitlesList));
		gDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		//开启ActionBar上APP ICON的功能  
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
			selectItem(2);
			selectItem(0);
		}

		boolean firstUse = ZGPreference.getInstance().getBoolean("firstUse", true);
		if(firstUse) {
			ZGPreference.getInstance().putBoolean("firstUse", false);
			ZGTask.getInstance().postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						String fileNames[] = getAssets().list("samples");
						String destDir = BaseConfig.getSBSPicturePath();
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
	
	private List<String> modifyTitles(List<String> titles) {
		List<String> returnLists = new ArrayList<String>();
		returnLists.addAll(titles);
		RBSwithcer switcer = BaseConfig.getSwitcher();
		boolean fouse = false;
		if (switcer == null) {
			fouse = true;
		}
		if(fouse || !switcer.isYoutubeswitcher()) {
			returnLists.remove(2);
			gNoYoutube = true;
			indexSample--;
			indexSBS--;
		}
		if(fouse || !switcer.isSampleswitcher()) {
			if(gNoYoutube) {
				returnLists.remove(2);
			} else {
				returnLists.remove(3);
			}
			indexSBS--;
		}
		return returnLists;
	}
	
	private void selectItem(int position) {
		FragmentManager fragmentManager = getFragmentManager();
		String tag = null;
		if(!gNoYoutube) {
			if (currentItem == 2 && position != 2) {
				YoutubeVideoListFragment youtubeFragment = (YoutubeVideoListFragment) currentFragment;
				youtubeFragment.hideSearchFragment();
			}
		}
		currentItem = position;
		Fragment toShow = null;
		if(position == indexVideo) {
			tag = LOCAL_VIDEO_TAG;
			toShow = fragmentManager.findFragmentByTag(tag);
			if (toShow == null) {
				toShow = VideoListLocalFragment.newInstance();
			}
		} else if(position == indexLocalPic) {
			tag = LOCAL_PIC_TAG;
			toShow = fragmentManager.findFragmentByTag(tag);
			if (toShow == null) {
				toShow = LocalPictureFragment.newInstance();
			}
		} else if(position == indexSBS) {
			tag = SBS_PATH_TAG;
			toShow = fragmentManager.findFragmentByTag(tag);
			if (toShow == null) {
				toShow = SBSPictureFragment.newInstance();
			}
		} else if(position == indexSample) {
			tag = SAMPLE_PIC_TAG;
			toShow = fragmentManager.findFragmentByTag(tag);
			if (toShow == null) {
				toShow = SamplePictureFragment.newInstance();
			}
		} else if(position == indexYoutubeVideo) {
			tag = YOUTUBE_VIDEO_TAG;
			toShow = fragmentManager.findFragmentByTag(tag);
			if (toShow == null) {
				toShow = YoutubeVideoListFragment.newInstance();
			}
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
		setTitle(gMenuTitlesList.get(position));
		gDrawerLayout.closeDrawer(gDrawerList);
	}

	@Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.  
        getMenuInflater().inflate(R.menu.main, menu);  
        return true;  
    } 

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// 当我们调用invalidateOptionsMenu()方法时候会调到这里
		// 在这里可以根据drawerOpen是否打开了来隐藏右上角的按钮菜单
		boolean drawerOpen = gDrawerLayout.isDrawerOpen(gDrawerList);
		menu.findItem(R.id.action_camera).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (gDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
        switch(item.getItemId()) {
        case R.id.action_camera:
        	Navigation.toCameraActivity(gContext, BaseConfig.getPicturePath());
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
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
		if(!gNoYoutube) {
			if (currentItem == 2) {
				YoutubeVideoListFragment youtubeFragment = (YoutubeVideoListFragment) currentFragment;
				if (youtubeFragment.isShowingSearch()) {
					youtubeFragment.hideSearchFragment();
					return;
				}
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
