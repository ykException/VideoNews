package com.feicui.videonews;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.feicui.videonews.ui.local.LocalFragment;
import com.feicui.videonews.videoplayer.full.VideoViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.viewPager)ViewPager viewPager;
    @BindView(R.id.btnLikes)Button btnLikes;
    @BindView(R.id.btnLocal)Button btnLocal;
    @BindView(R.id.btnNews)Button btnNews;

    private final FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new LocalFragment();
                case 1:
                    return new LocalFragment();
                case 2:
                    return new LocalFragment();
                default:
                    throw new RuntimeException("Err Dates!");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
        btnNews.setSelected(true);//设置在线为默认选中
    }

    private String getTestVideo1(){
        return "http://o9ve1mre2.bkt.clouddn.com/raw_%E6%B8%A9%E7%BD%91%E7%94%B7%E5%8D%95%E5%86%B3%E8%B5%9B.mp4";
    }

    /*
    * 点击按钮切换到对应的页面
    * */
    @OnClick({R.id.btnNews,R.id.btnLocal,R.id.btnLikes})
    public void chooseFragment(View view){
        switch (view.getId()){
            case R.id.btnNews:
                viewPager.setCurrentItem(0,false);//切换到的页面 false：动作为瞬时的
                return;
            case R.id.btnLocal:
                viewPager.setCurrentItem(1,false);
                return;
            case R.id.btnLikes:
                viewPager.setCurrentItem(2,false);
                return;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /*
    * 切换ViewPager时下放按钮更随切换
    * */
    @Override
    public void onPageSelected(int position) {
        btnNews.setSelected(position == 0);
        btnLocal.setSelected(position == 1);
        btnLikes.setSelected(position == 2);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
