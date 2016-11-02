package com.example.demo.gank;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GankFragment extends Fragment{
    public List<String> mUrls =new ArrayList<String>();
    private RecyclerView mReyclerView;
    private GankAdapter mAdapter;
    private OkHttpClient mClient;
    private int index = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 2:
                    setAdapter();
                    break;
            }
        }
    };

    public GankFragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list,container,false);
        mClient = new OkHttpClient();
        mReyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mReyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new GankAdapter(getActivity(),mUrls);
        mReyclerView.setAdapter(mAdapter);
        loadApi(index);

        mReyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isScrollToEnd(mReyclerView)){
                    Log.e("tag","============scroll to end");
                    index += 1;
                    loadApi(index);
                }
            }
        });

        return v;
    }

    private void setAdapter(){
        mAdapter.notifyDataSetChanged();
    }

    private boolean isScrollToEnd(RecyclerView view){
        if (view == null) return false;
        if (view.computeVerticalScrollExtent() + view.computeVerticalScrollOffset() >= view.computeVerticalScrollRange())
            return true;
        return false;
    }

    private void loadApi(int page){
        Request request = new Request.Builder().url("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/"+page).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("tag","loading failure ");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String result = response.body().string();
                    try {
                        JSONObject json = new JSONObject(result);
                        JSONArray array = new JSONArray(json.getString("results"));
                        for(int i = 0;i<array.length();i++){
                            JSONObject ob = array.getJSONObject(i);
                            mUrls.add(ob.getString("url"));
                            Log.e("tag","========== url: "+ob.getString("url"));
                        }

                        mHandler.sendEmptyMessage(2);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}