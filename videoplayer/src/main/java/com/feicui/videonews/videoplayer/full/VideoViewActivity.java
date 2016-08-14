package com.feicui.videonews.videoplayer.full;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.feicui.videonews.videoplayer.R;

import java.util.Locale;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 *使用open方法，传入视频路径，启动Activity
 * Created by yukai on 2016/8/10.
 */
public class VideoViewActivity extends AppCompatActivity{


    private ImageView mImageView;
    private TextView mTextView;//显示下载速度，缓冲百分比
    private int downLoadSpeed;//下载速度
    private int bufferPercent;//缓冲百分比

    private MediaPlayer mMediaPlayer;
    private VideoView mVideoView;
    private static final String KEY_VIDEO_PATH = "KEY_VIDEO_PATH";

    public static void open(Context context,String videoPath){
        Intent intent = new Intent(context,VideoViewActivity.class);
        intent.putExtra(KEY_VIDEO_PATH,videoPath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置背景颜色
        getWindow().setBackgroundDrawableResource(android.R.color.black);
        Vitamio.isInitialized(this);
        setContentView(R.layout.activity_video_view);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        initBufferViews();//初始化视图
        initVideoView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.setVideoPath(getIntent().getStringExtra(KEY_VIDEO_PATH));

    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.stopPlayback();
    }

    private void initBufferViews() {
        mImageView = (ImageView) findViewById(R.id.ivLoading);
        mTextView = (TextView) findViewById(R.id.tvBufferInfo);
        mImageView.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
    }

    private void initVideoView() {
        mVideoView = (VideoView) findViewById(R.id.videoView);

        mVideoView.setMediaController(new VideoSeltsetMediaController(this));
        mVideoView.setKeepScreenOn(true);//保持屏幕亮度
        mVideoView.requestFocus();//获取焦点
        //准备时的监听
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            //设置缓冲区的大小 默认是1M
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer = mp;
                mMediaPlayer.setBufferSize(512 * 1024);
            }
        });

        //缓冲更新时的监听
        mVideoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                bufferPercent = percent;
                updateBufferViews();
            }
        });

        //“状态”信息的监听
        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what){
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        showBufferViews();
                        if (mVideoView.isPlaying()){
                            mVideoView.pause();
                        }
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        hideBufferViews();
                        mVideoView.start();
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        downLoadSpeed = extra;
                        updateBufferViews();
                        break;
                }
                return true;
            }
        });
    }

    private void hideBufferViews() {
        mImageView.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
    }

    private void updateBufferViews() {
        String info = String.format(Locale.CHINA,"%d%%,%dkb/s",bufferPercent,downLoadSpeed);
        mTextView.setText(info);
    }

    //开始缓冲
    private void showBufferViews() {
        mImageView.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.VISIBLE);
        downLoadSpeed = 0;
        bufferPercent = 0;
    }
}
