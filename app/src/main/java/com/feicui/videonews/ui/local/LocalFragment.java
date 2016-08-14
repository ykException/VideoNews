package com.feicui.videonews.ui.local;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.feicui.videonews.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *在GridView中填充本地视频
 * 使用Loader进行数据的加载
 * 使用CursorAdapter进行数据的填充
 * Created by yukai on 2016/8/13.
 */
public class LocalFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.gridView)GridView Local_gridView;//注：使用BufferKnif时访问权限不能设为private，不能使用static修饰

    private Unbinder unbinder;
    private LocalAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new LocalAdapter(getContext());

        //初始化loader
        getLoaderManager().initLoader(0,null,this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_video,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this,view);//在非Activity中一定要加上view
        Local_gridView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();//ButterKnif进行释放
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        adapter.releaseResource();
    }

    //=======================初始化|loader需要重写的方法==================//
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] videoUri ={
                MediaStore.Video.Media._ID,//id
                MediaStore.Video.Media.DISPLAY_NAME,//视频名称
                MediaStore.Video.Media.DATA//视频的数据来源
        };
            return new CursorLoader(getContext(),
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    videoUri,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);//????
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
