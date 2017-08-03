package com.intelliworkz.admin.admingujaratabroad;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by pc-6 on 6/21/2017.
 */

class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    Context context;
    ArrayList<NewsModel> newsArrayList;
    String url=Common.SERVICE_URL;
    View v;

    public NewsAdapter(Context context, ArrayList<NewsModel> newsArrayList) {
        this.context=context;
        this.newsArrayList=newsArrayList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v=LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        final String newsId =newsArrayList.get(position).getNewsId();
        final String newsCatId = newsArrayList.get(position).getNewsCatId();
        final String newsDetails =newsArrayList.get(position).getNewsDetails();
        final String newsDate =newsArrayList.get(position).getNewsDate();

        final String newsTitle =newsArrayList.get(position).getNewsTitle();
        holder.txtNewsTitleList.setText(newsTitle);

        String imgName = newsArrayList.get(position).getNewsImg();
        final String urlImg = url+"news_img/"+imgName;

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

        imageLoader.displayImage(urlImg,holder.ivImgNewsList, options);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context,FullNewsActivity.class);
                i.putExtra("newsId",newsId);
                i.putExtra("newsCatId",newsCatId);
                i.putExtra("newsDetails",newsDetails);
                i.putExtra("newsDate",newsDate);
                i.putExtra("newsTitle",newsTitle);
                i.putExtra("urlImg",urlImg);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImgNewsList;
        TextView txtNewsTitleList;
        public ViewHolder(View v) {
            super(v);
            ivImgNewsList=(ImageView)v.findViewById(R.id.ivImgNewsList);
            txtNewsTitleList=(TextView)v.findViewById(R.id.txtNewsTitleList);
        }
    }
}
