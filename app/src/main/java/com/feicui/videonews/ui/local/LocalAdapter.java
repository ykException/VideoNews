package com.feicui.videonews.ui.local;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yukai on 2016/8/13.
 */
public class LocalAdapter extends CursorAdapter{

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    //缓存图片
    private LruCache<String,Bitmap> lruCache = new LruCache<String,Bitmap>(520 * 1024 * 1024){
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();//该图片所占内存
        }
    };

    public LocalAdapter(Context context) {
        super(context, null, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new LocalVideoView(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final LocalVideoView localVideoView = (LocalVideoView) view;
        //将游标传到LocalVideoView中进行相应的设置
        Log.d("cursor","==================" + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
        localVideoView.getDataFromAdapter(cursor);

        final String filePath = localVideoView.getFailPath();
        Bitmap bitmap = lruCache.get(filePath);

        //判断缓存中是否缓存有图片数据
        if (bitmap != null){
            localVideoView.setPreview(bitmap);
        }else {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    //获取预览缩略图 耗时操作
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    localVideoView.setPreview(filePath,bitmap);
                    lruCache.put(filePath,bitmap);//存入缓存
                }
            });
        }

    }

    /*
    * 注意：线程池用完后释放掉
    * */
    public void releaseResource(){
        executorService.shutdown();
    }
}
