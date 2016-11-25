package com.itheima.googleplay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewTreeObserver;

import com.astuetz.PagerSlidingTabStripExtends;
import com.itheima.googleplay.base.BaseFragment;
import com.itheima.googleplay.base.LoadingPager;
import com.itheima.googleplay.factory.FragmentFactory;
import com.itheima.googleplay.utils.LogUtils;
import com.itheima.googleplay.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.main_tabs)
    PagerSlidingTabStripExtends mMainTabs;

    @InjectView(R.id.main_viewpager)
    ViewPager mMainViewpager;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private String[] mMainTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initActionBar();
        initView();
        initActionBarDrawerToggle();
        initData();
        initListener();//去除HomeFragment
    }


    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerLayout);
    }

    private void initActionBar() {
        //得到actionBar的实例
//        getActionBar();//android sdk actionbar
        ActionBar supportActionBar = getSupportActionBar();//v4包中

        //设置标题
        supportActionBar.setTitle("GooglePlay");
//        supportActionBar.setSubtitle("副标题");

        //设置图标
        supportActionBar.setIcon(R.drawable.ic_launcher);
        supportActionBar.setLogo(R.mipmap.ic_action_call);

        //显示logo/icon(图标)
        supportActionBar.setDisplayShowHomeEnabled(false);//默认是false,默认是隐藏图标

        //修改icon和logo显示的优先级
        supportActionBar.setDisplayUseLogoEnabled(true);//默认是false,默认是没用logo,用的icon

        //显示回退部分
        supportActionBar.setDisplayHomeAsUpEnabled(true);//默认是false,默认隐藏了回退部分
    }

    private void initActionBarDrawerToggle() {
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        //同步状态方法-->替换默认回退部分的UI效果
        mToggle.syncState();

        //设置drawerLayout的监听-->DrawerLayout拖动的时候,toggle可以跟着改变ui
        mDrawerLayout.setDrawerListener(mToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                Toast.makeText(getApplicationContext(), "点击了回退部分", Toast.LENGTH_SHORT).show();
                //点击toggle可以控制drawerlayout的打开和关闭
                mToggle.onOptionsItemSelected(item);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initData() {
        //模拟数据集
        mMainTitles = UIUtils.getStrings(R.array.main_titles);

        //为viewPager设置适配器
//        MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        MainFragmentStatePagerAdapter adapter = new MainFragmentStatePagerAdapter(getSupportFragmentManager());
        mMainViewpager.setAdapter(adapter);

        // Bind the tabs to the ViewPager
        mMainTabs.setViewPager(mMainViewpager);
    }

    public void initListener() {
        final MyOnpageChangeListener myOnpageChangeListener = new MyOnpageChangeListener();

        //监听viewpager页面的切换
        mMainTabs.setOnPageChangeListener(myOnpageChangeListener);

        mMainViewpager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //ViewPager已经展示给用户看,说明Fragment已经创建好了
                //手动选择第一页,触发加载数据
                myOnpageChangeListener.onPageSelected(0);
                mMainViewpager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /*
      PagerAdapter-->View
      FragmentStatePagerAdapter-->Fragment
      FragmentPagerAdapter-->Fragment
     */
    class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        public MainFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {//指定Position所对应的页面的Fragment内容
            LogUtils.s("初始化->" + mMainTitles[position]);
            Fragment fragment = FragmentFactory.createFragment(position);
            return fragment;
        }

        @Override
        public int getCount() {//决定ViewPager页数的总和
            if (mMainTitles != null) {
                return mMainTitles.length;
            }
            return 0;
        }

        /**
         * 必须覆写一个方法:getPageTitle
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mMainTitles[position];
        }
    }

    class MainFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

        public MainFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {//指定Position所对应的页面的Fragment内容
            LogUtils.s("初始化->" + mMainTitles[position]);
            Fragment fragment = FragmentFactory.createFragment(position);
            return fragment;
        }

        @Override
        public int getCount() {//决定ViewPager页数的总和
            if (mMainTitles != null) {
                return mMainTitles.length;
            }
            return 0;
        }

        /**
         * 必须覆写一个方法:getPageTitle
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mMainTitles[position];
        }
    }

    class MyOnpageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //根据position找到fragment
            BaseFragment baseFragment = FragmentFactory.mCacheFragments.get(position);
            //拿到Fragment里面的loadingpager
            LoadingPager loadingPager = baseFragment.getLoadingPager();
            //触发加载数据
            loadingPager.triggerLoadData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}

