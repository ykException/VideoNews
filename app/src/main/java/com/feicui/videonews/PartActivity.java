package com.feicui.videonews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.feicui.videonews.videoplayer.part.SimpleVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PartActivity extends AppCompatActivity {

    @BindView(R.id.simpleVideoView) SimpleVideoView simpleVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        simpleVideoView.setVideoPath(getTestVideo1());
    }

    private String getTestVideo1(){
        return "http://o9ve1mre2.bkt.clouddn.com/raw_%E6%B8%A9%E7%BD%91%E7%94%B7%E5%8D%95%E5%86%B3%E8%B5%9B.mp4";
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleVideoView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleVideoView.onPause();
    }
}
