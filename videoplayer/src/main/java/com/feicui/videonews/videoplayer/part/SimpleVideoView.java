package com.feicui.videonews.videoplayer.part;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.feicui.videonews.videoplayer.R;
import com.feicui.videonews.videoplayer.full.VideoViewActivity;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.widget.VideoView;

/**
 * 分析：自定义VideoView，使用MediaPlayer+SurfaceView实现视频的播放
 * 1、初始化MediaPlayer和SurfaceView initControllerView()，initSuerfaceView()；
 * initControllerView：设置视频控制：播放暂停、进度更随、快进快退、全屏播放等；
 * 2、获取播放路径 setVideoPath();
 * 3、核心：对外提供onResume和onPause方法，更随Activity生命周期来设置播放的控制
 * 4、onResume：初始化播放，准备播放（包含开始播放）
 * 5、onPause:暂停播放、释放Mediaplayer更新状态
 * Created by yukai on 2016/8/10.
 */
public class SimpleVideoView extends FrameLayout{

    private MediaPlayer mediaPlayer;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private ImageButton btnToggle,btnFullScreen;
    private ImageView ivPreview;
    private ProgressBar progressBar;

    private String videoPath;

    private boolean isPrepared;//是否准备
    private boolean isPlaying;//是否在播放

    private static final int MAX_PROGRESS = 1000;//最大进度

    public SimpleVideoView(Context context) {
        this(context,null);
    }

    public SimpleVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SimpleVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    //====================================初始化数据================================//
    /*
    * 初始化视图SurfaceView、MediaPlayer
    * */
    private void initView() {
        //实例化Vitamio？？？（作用未知）
        Vitamio.isInitialized(getContext());

        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_video_player,this);

        initSuerfaceView();
        initControllerView();
    }

    //利用handler不断地改变进度
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isPlaying){
                long current = mediaPlayer.getCurrentPosition();//当前进度位置
                long mDuration = mediaPlayer.getDuration();//视频总的进度
                int progress = (int) (current * MAX_PROGRESS / mDuration);//当前进度

                progressBar.setProgress(progress);
                handler.sendEmptyMessageDelayed(0,200);
            }
        }
    };

    /*
    * 初始化视频的控制器
    * */
    private void initControllerView() {
        //预览视图
        ivPreview = (ImageView) findViewById(R.id.ivPreview);

        //播放或暂停
        btnToggle = (ImageButton) findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    pauseMediaPlayer();
                }else if (isPrepared){
                    startMediaPlayer();
                }else {
                    Toast.makeText(getContext(),"Failed To Loading!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(MAX_PROGRESS);//设置最大进度

        //全频切换
        btnFullScreen = (ImageButton) findViewById(R.id.btnFullScreen);
        btnFullScreen.setOnClickListener(new OnClickListener() {
            //跳转到VideoViewActivity中实现
            @Override
            public void onClick(View v) {
                VideoViewActivity.open(getContext(),videoPath);
            }
        });
    }

    /*
    * 初始化SurfaceView???
    * */
    private void initSuerfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();//SurfaceHolder？？？
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);//format??
    }

    //=====================================onResume===================================//
    /*
   * 更随Activity的生命周期onResume继续时MediaPlayer的操作
   * */
    public void onResume(){
        //初始化MediaPlayer
        initMediaPlayer();
        //准备播放
        preparedMediaplayer();
    }

    /*
    * 初始化MediaPlayer
    * */
    private void initMediaPlayer() {

        mediaPlayer = new MediaPlayer(getContext());
        mediaPlayer.setDisplay(surfaceHolder);//设置显示视频

        //设置MediaPlayer的播放准备时的监听
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
               isPrepared = true;
                //开始播放视频
               startMediaPlayer();
            }
        });

        //播放器异常处理
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                releaseMediaPlayer();
                return false;
            }
        });

        //类似于“状态”信息的监听
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_FILE_OPEN_OK){
                    long bufferSize = mediaPlayer.audioTrackInit();
                    mediaPlayer.audioInitedOk(bufferSize);
                    return true;
                }
                return false;
            }
        });

        //适配频幕的宽高
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                int videoWidth = surfaceView.getWidth();
                int videoHeight = videoWidth * height / width;

                ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
                layoutParams.width = videoWidth;
                layoutParams.height = videoHeight;
                surfaceView.setLayoutParams(layoutParams);
            }
        });
    }

    /*
    * 开始播放
    * */
    private void startMediaPlayer() {
        if (isPrepared){
            mediaPlayer.start();
        }
        isPlaying = true;
        handler.sendEmptyMessage(0);
        btnToggle.setImageResource(R.drawable.ic_pause);
    }

    /*
  *准备MediaPlayer
  * */
    private void preparedMediaplayer() {
        try {
            mediaPlayer.reset();
            //设置播放资源
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.setLooping(true);
            //注：异步准备
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //=====================================onPause====================================//
    /*
    * 更随Activity的生命周期onPause暂停时MediaPlayer的操作
    * */
    public void onPause(){
        //暂停播放
        pauseMediaPlayer();
        //释放MediaPlayer
        releaseMediaPlayer();
    }

    private void pauseMediaPlayer() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        isPlaying = false;
        handler.removeMessages(0);
        btnToggle.setImageResource(R.drawable.ic_play_arrow);
    }

    //释放MediaPlayer??
    private void releaseMediaPlayer() {
        mediaPlayer.release();
        mediaPlayer = null;
        isPrepared = false;
        isPlaying = false;
    }

    /*
    * 播放资源或路径 注：一定要在onResume之前调用
    * */
    public void setVideoPath(String videoPath){
        this.videoPath = videoPath;
    }

}
