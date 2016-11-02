package com.example.demo.gank;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class LocalAlbumFragment extends Fragment{
    private List<String> mData = new ArrayList<String>();
    private RecyclerView mReyclerView;
    private GankAdapter mAdapter;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 2:
                    loadData();
                    break;
            }
        }
    };

    public LocalAlbumFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list,container,false);
        mReyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mReyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new GankAdapter(getActivity(),mData);
        mReyclerView.setAdapter(mAdapter);
        loadAlbum();
        return v;
    }

    private void loadAlbum(){
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Cursor  c = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns.DATA},null,null, MediaStore.Images.ImageColumns.DATE_TAKEN+" desc ");
                if(null != c && c.getCount() > 0 && c.moveToFirst()){
                    while (c.moveToNext()){
                        mData.add(c.getString(c.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mHandler.sendEmptyMessage(2);

            }
        };

        asyncTask.execute();

    }

    private void loadData(){
        mAdapter.notifyDataSetChanged();
    }
}