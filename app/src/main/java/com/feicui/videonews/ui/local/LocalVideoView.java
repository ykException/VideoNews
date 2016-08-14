package com.feicui.videonews.ui.local;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.feicui.videonews.R;
import com.feicui.videonews.videoplayer.full.VideoViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yukai on 2016/8/13.
 */
public class LocalVideoView extends FrameLayout{

    @BindView(R.id.ivPreview)ImageView ivPreview; // 视频预览图
    @BindView(R.id.tvVideoName)TextView tvVideoName; // 视频名称

    private String filePath;//视频文件的路径

    public LocalVideoView(Context context) {
        super(context,null);
    }

    public LocalVideoView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public LocalVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_local_video,this,true);
        ButterKnife.bind(this);
    }

    //将localAdapter中的游标拿出来 获取对应的视频数据
    public void getDataFromAdapter(Cursor cursor){
        Log.d("cursor","==================" + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
        filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        String videoName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
        tvVideoName.setText(videoName);
        //清楚之前的预览图片
        ivPreview.setImageBitmap(null);
        cursor.close();
    }

    /*
    *设置预览图片
    * */
    @UiThread
    public void setPreview(@NonNull Bitmap bitmap){
        ivPreview.setImageBitmap(bitmap);
    }

    public void setPreview(final String filePath, final Bitmap bitmap){
        if (!filePath.equals(this.filePath)){
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                if (!filePath.equals(LocalVideoView.this.filePath)){
                    ivPreview.setImageBitmap(bitmap);
                }
            }
        });
    }

    /*
    * 点击当前视图 进入全频播放页面
    * */
    @OnClick
    public void onClick(){
        VideoViewActivity.open(getContext(),filePath);
    }

    public String getFailPath() {
        return filePath;
    }
}
