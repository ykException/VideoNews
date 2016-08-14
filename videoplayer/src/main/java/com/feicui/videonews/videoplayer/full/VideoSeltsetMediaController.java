package com.feicui.videonews.videoplayer.full;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.feicui.videonews.videoplayer.R;

import io.vov.vitamio.widget.MediaController;

/**
 * Created by yukai on 2016/8/10.
 */
public class VideoSeltsetMediaController extends MediaController{

    private MediaPlayerControl mPlayerControl;

    private final AudioManager audioManager;//调整音量
    private Window window;//调整亮度

    private final int maxVolume;//最大音量
    private float currentLight;//当前亮度
    private int currentVolume;//当前音量

    public VideoSeltsetMediaController(Context context) {
        super(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        window = ((Activity)context).getWindow();
    }

    @Override
    protected View makeControllerView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_custom_video_controller,this);
        initView(view);
        return view;
    }

    //将MediaPlayerControl成员化 方便使用
    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        super.setMediaPlayer(player);
        this.mPlayerControl = player;
    }

    private void initView(View view) {
        ImageButton upSpeed = (ImageButton) view.findViewById(R.id.btnFastForward);
        ImageButton downSpeed = (ImageButton) view.findViewById(R.id.btnFastRewind);

        upSpeed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long pos = mPlayerControl.getCurrentPosition();
                pos += 5000;
                mPlayerControl.seekTo(pos);

            }
        });

        downSpeed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long pos = mPlayerControl.getCurrentPosition();
                if (pos > 0){
                    pos -= 5000;
                }
                mPlayerControl.seekTo(pos);
            }
        });

        //音量和亮度的调节
        final View adjustView = view.findViewById(R.id.adjustView);
        final GestureDetector gestureDetector = new GestureDetector(
                getContext(),new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {

                float startX = e1.getX();//开始水平滑动位置
                float startY = e1.getY();//开始垂直滑动位置
                float endX = e2.getX();//终止水平滑动位置
                float endY = e2.getY();//终止垂直滑动位置

                float viewWith = adjustView.getWidth();//视图宽度
                float viewHeight = adjustView.getHeight();//视图高度

                //滑动高度百分比
                float hPercent = distanceY / viewHeight;

                //滑动判定
                if (startX < viewWith / 2){
                    adjustlight(hPercent);
                    return true;
                }else if (startX > viewWith / 2){
                    adjustSound(hPercent);
                    return true;
                }
                return false;
            }
        });

        adjustView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    currentLight = window.getAttributes().screenBrightness;
                }
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    /*
    *pointVolume 目标音量
    * maxVolume 最大音量
    * currentVolume 当前音量
    * */
    private void adjustSound(float percent) {

        int pointVolume = (int) (percent * maxVolume )+ currentVolume;
        pointVolume = pointVolume > maxVolume ? maxVolume : pointVolume;
        pointVolume = pointVolume < 0 ? 0 : pointVolume;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,pointVolume,AudioManager.FLAG_SHOW_UI);
    }

    /*
    *pointLight 目标亮度
    *currentLight 当前亮度
    * */
    private void adjustlight(float percent) {
        float pointLight = percent + currentLight;
        pointLight = pointLight > 1.0f ? 1.0f : pointLight;
        pointLight = pointLight < 0.05f ? 0.05f : pointLight;

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = pointLight;
        window.setAttributes(layoutParams);
    }
}
