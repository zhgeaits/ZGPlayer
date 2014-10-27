package org.zhangge.rbplayer.ui;

import java.util.Arrays;
import java.util.List;

import org.zhangge.rbplayer.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    private Context gContext;
    private DrawerLayout gDrawerLayout;
    private ListView gDrawerList;
    private ActionBarDrawerToggle gDrawerToggle;

    private CharSequence gDrawerTitle;
    private CharSequence gTitle;
    private String[] gMenuTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gContext = this;
        setContentView(R.layout.activity_main);

        gTitle = gDrawerTitle = getTitle();
        gMenuTitles = getResources().getStringArray(R.array.menu_array);
        gDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        gDrawerList = (ListView) findViewById(R.id.left_drawer);

        //设置阴影效果
        gDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        gDrawerList.setAdapter(new LeftDrawerAdapter(gContext, Arrays.asList(gMenuTitles)));
        gDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        gDrawerToggle = new ActionBarDrawerToggle(
                this,
                gDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(gTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(gDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        gDrawerLayout.setDrawerListener(gDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //当我们调用invalidateOptionsMenu()方法时候会调到这里
        //在这里可以根据drawerOpen是否打开了来隐藏右上角的按钮菜单
        boolean drawerOpen = gDrawerLayout.isDrawerOpen(gDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = VideoListLocalFragment.getInstance();
                break;
            default:
                fragment = VideoListLocalFragment.getInstance();
                break;
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        gDrawerList.setItemChecked(position, true);
        setTitle(gMenuTitles[position]);
        gDrawerLayout.closeDrawer(gDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        gTitle = title;
        getActionBar().setTitle(gTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //使用ActionBarDrawerToggle就必须在这里调用这个
        gDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //使用ActionBarDrawerToggle就必须在这里调用这个
        gDrawerToggle.onConfigurationChanged(newConfig);
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
