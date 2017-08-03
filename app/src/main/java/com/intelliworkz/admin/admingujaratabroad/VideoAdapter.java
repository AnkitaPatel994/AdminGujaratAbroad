package com.intelliworkz.admin.admingujaratabroad;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shyam group on 6/19/2017.
 */

class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> videoList;
    View v;
    public VideoAdapter(Context context, ArrayList<HashMap<String, String>> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String videoIdView = videoList.get(position).get("video_id");

        final String videoTitleView = videoList.get(position).get("video_title");
        holder.txtVideoTitleView.setText(videoTitleView);

        final String videoUrlView = videoList.get(position).get("video_thumb");

        GetImgthumb imgthumb = new GetImgthumb(videoUrlView,holder);
        imgthumb.execute();

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,VideoViewActivity.class);
                i.putExtra("videoIdView",videoIdView);
                i.putExtra("videoTitleView",videoTitleView);
                i.putExtra("videoUrlLinkView",videoUrlView);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtVideoTitleView;
        ImageView ivVideoImgView;
        public ViewHolder(View v) {
            super(v);
            txtVideoTitleView= (TextView)v.findViewById(R.id.txtVideoTitleView);
            ivVideoImgView= (ImageView) v.findViewById(R.id.ivVideoImgView);
        }
    }

    private class GetImgthumb extends AsyncTask<String,Void,String>{

        String videoUrlView;
        Bitmap bitmap = null;
        ViewHolder holder;

        public GetImgthumb(String videoUrlView, ViewHolder holder) {
            this.videoUrlView = videoUrlView;
            this.holder = holder;
        }

        @Override
        protected String doInBackground(String... params) {


            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14) {
                    // no headers included
                    mediaMetadataRetriever.setDataSource(videoUrlView, new HashMap<String, String>());
                }
                else {
                    mediaMetadataRetriever.setDataSource(videoUrlView);
                }
                bitmap = mediaMetadataRetriever.getFrameAtTime();
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (mediaMetadataRetriever != null)
                    mediaMetadataRetriever.release();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            holder.ivVideoImgView.setImageBitmap(bitmap);
        }
    }
}
