package com.example.demo.gank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class GankAdapter extends RecyclerView.Adapter<GankAdapter.ViewHolder>{
    private List<String> mItems;
    private Context mContext;
    public GankAdapter(Context context,List<String> items) {
        super();
        mItems = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String url = mItems.get(position);
        Log.e("tag","============onBindViewHolder url: "+url);
        Glide.with(mContext)
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                //.bitmapTransform(new CropCircleTransformation(mContext))  //如果想使用变换效果，这个注释可以打开
                .into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext,PreviewImageActivity.class);
                intent.putExtra("url",url);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
         ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.image);
        }
    }
}