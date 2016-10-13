package cn.ucai.superwechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SWApplication;
import cn.ucai.superwechat.fragment.FragmentContacts;
import cn.ucai.superwechat.fragment.FragmentProfile;
import cn.ucai.superwechat.fragment.FragmentSearch;
import cn.ucai.superwechat.fragment.FragmentWeixin;
import cn.ucai.superwechat.utils.ToastUtil;


public class MainActivity extends AppCompatActivity implements
        RadioGroup.OnCheckedChangeListener {

    List<Fragment> mFragments;
    FragmentWeixin mFragmentWeixin;
    FragmentContacts mFragmentContacts;
    FragmentSearch mFragmentSearch;
    FragmentProfile mFragmentProfile;

    private int index = 0; //默认页面下标

    @ViewInject(R.id.tabs)
    RadioGroup mRadioGroup;
    @ViewInject(R.id.fragment_vp)
    ViewPager fragment_vp;
    private MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(MainActivity.this);
        if (!SWApplication.hasLogined()) { //!SWApplication.hasLogined()
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        initView();
    }

    public void initView() {
        mFragmentWeixin = new FragmentWeixin();
        mFragmentContacts = new FragmentContacts();
        mFragmentSearch = new FragmentSearch();
        mFragmentProfile = new FragmentProfile();
        mFragments = new ArrayList<Fragment>();
        mFragments.add(mFragmentWeixin);
        mFragments.add(mFragmentContacts);
        mFragments.add(mFragmentSearch);
        mFragments.add(mFragmentProfile);
        //初始化RadioGroup
        mRadioGroup.setOnCheckedChangeListener(this);
        //初始化viewPager
        fragment_vp.setOffscreenPageLimit(4);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFragments);
        fragment_vp.setAdapter(mAdapter);
        fragment_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //默认显示第一页
        ((RadioButton) mRadioGroup.getChildAt(index)).setChecked(true);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rbWeixin:
                index = 0;
                break;
            case R.id.rbContacts:
                index = 1;
                break;
            case R.id.rbSearch:
                index = 2;
                break;
            case R.id.rbProfile:
                index = 3;
                break;
            default:
                index = 0;
                break;
        }
        if (fragment_vp.getCurrentItem() == index) {
            return;
        }
        fragment_vp.setCurrentItem(index);
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                ToastUtil.show(this, "查找");
                break;
            case R.id.menu_add:
                ToastUtil.show(this, "添加");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // 返回桌面
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }
}
