package com.intelliworkz.admin.admingujaratabroad;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shyam group on 6/17/2017.
 */

class AdvertiseAdapter extends RecyclerView.Adapter<AdvertiseAdapter.ViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> advertiseList;
    View v;

    public AdvertiseAdapter(Context context, ArrayList<HashMap<String, String>> advertiseList) {

        this.context = context;
        this.advertiseList = advertiseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.advertise_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String adWebUrl = advertiseList.get(position).get("addLink");
        final String adId = advertiseList.get(position).get("addId");

        final String adTitle = advertiseList.get(position).get("addTitle");
        holder.txtAdTitleView.setText(adTitle);


        final String urlImg = advertiseList.get(position).get("addImg");

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        ImageLoader imageLoader = ImageLoader.getInstance();
        int fallback = 0;
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback).build();

        imageLoader.displayImage(urlImg,holder.ivAdImgView, options);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context,ZoomAdActivity.class);
                i.putExtra("urlImg",urlImg);
                i.putExtra("adId",adId);
                i.putExtra("adWebUrl",adWebUrl);
                i.putExtra("adTitle",adTitle);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return advertiseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAdImgView;
        TextView txtAdTitleView;
        public ViewHolder(View v) {
            super(v);

            ivAdImgView = (ImageView)v.findViewById(R.id.ivAdImgView);
            txtAdTitleView = (TextView)v.findViewById(R.id.txtAdTitleView);
        }
    }
}
