package com.intelliworkz.admin.admingujaratabroad;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ZoomAdActivity extends AppCompatActivity {
    ImageView ivZoomAdView;
    String adImg,adId,status,message,adTitle,adWebUrl;
    ProgressDialog dialog;
    TextView txtTitleAd;
    String url=Common.SERVICE_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_advertise);
        setContentView(R.layout.activity_zoom_ad);

        if(getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ivZoomAdView =(ImageView)findViewById(R.id.ivZoomAdView);
        txtTitleAd = (TextView)findViewById(R.id.txtTitleAd);

        adImg = getIntent().getExtras().getString("urlImg");
        adTitle = getIntent().getExtras().getString("adTitle");
        adId = getIntent().getExtras().getString("adId");
        adWebUrl = getIntent().getExtras().getString("adWebUrl");

        txtTitleAd.setText(adTitle);

        ivZoomAdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(adWebUrl));

                if(!MyStartActivity(i)){

                    i.setData(Uri.parse(adWebUrl));
                    /*if(!MyStartActivity(i)){
                        Toast.makeText(getApplicationContext(),"Could not Open Browser",Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
        });

        GetzoomAdImg zoomAdImg=new GetzoomAdImg();
        zoomAdImg.execute();

    }

    private boolean MyStartActivity(Intent intent)
    {
        try {
            startActivity(intent);
            return true;
        }catch (ActivityNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home)
            finish();

        int id = item.getItemId();

        if (id == R.id.menu_edit) {

            Intent i = new Intent(getApplicationContext(),AdvertiseAddActivity.class);
            i.putExtra("adId",adId);
            i.putExtra("adTitle",adTitle);
            i.putExtra("adImg",adImg);
            i.putExtra("adWebUrl",adWebUrl);
            startActivity(i);
        }
        else if (id == R.id.menu_delete) {
            AlertDialog ad;
            AlertDialog.Builder build = new AlertDialog.Builder(ZoomAdActivity.this);
            build.setMessage("Are you sure want to delete?");
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GetadImgDelete adImgDelete=new GetadImgDelete();
                    adImgDelete.execute();
                }
            });
            build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            ad = build.create();
            ad.show();

        }
        return super.onOptionsItemSelected(item);
    }

    private class GetzoomAdImg extends AsyncTask<Void,Void,Void> {
        ImageLoader imageLoader;
        DisplayImageOptions options;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(ZoomAdActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true).cacheInMemory(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .displayer(new FadeInBitmapDisplayer(300)).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .memoryCache(new WeakMemoryCache())
                    .discCacheSize(100 * 1024 * 1024).build();

            ImageLoader.getInstance().init(config);

            imageLoader = ImageLoader.getInstance();
            int fallback = 0;
            options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(fallback)
                    .showImageOnFail(fallback)
                    .showImageOnLoading(fallback).build();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            imageLoader.displayImage(adImg,ivZoomAdView, options);
        }
    }

    private class GetadImgDelete extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(ZoomAdActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {

            JSONObject adDeleteImg = new JSONObject();
            try {
                adDeleteImg.put("add_id",adId);
                Postdata postdata = new Postdata();
                Log.d("Like", "data" + adDeleteImg.toString());
                String imgDelete = postdata.post(url+"delete_add.php", String.valueOf(adDeleteImg));
                JSONObject jsonObject = new JSONObject(imgDelete);
                status = jsonObject.getString("status");
                if (status.equals("1"))
                {
                    Log.d("Like","Successfully");
                }
                else
                {
                    message=jsonObject.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if(status.equals("1"))
            {
                Intent i = new Intent(getApplicationContext(),AdvertiseActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            else
            {
                Toast.makeText(getApplicationContext(),""+message,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
