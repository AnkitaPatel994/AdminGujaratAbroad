package com.intelliworkz.admin.admingujaratabroad;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class FullNewsActivity extends AppCompatActivity {

    ImageView ivNewsViewImg;
    TextView txtNewsDate,txtNewsViewTitle,txtNewsViewInfo;
    String newsId,newsTitle,newsInfo,newsImg,status,message,newsCatId;
    String url=Common.SERVICE_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_news);

        if(getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ivNewsViewImg = (ImageView)findViewById(R.id.ivNewsViewImg);
        txtNewsDate = (TextView)findViewById(R.id.txtNewsDate);
        txtNewsViewTitle = (TextView)findViewById(R.id.txtNewsViewTitle);
        txtNewsViewInfo = (TextView)findViewById(R.id.txtNewsViewInfo);

        newsId = getIntent().getExtras().getString("newsId");
        newsCatId = getIntent().getExtras().getString("newsCatId");
        newsTitle = getIntent().getExtras().getString("newsTitle");
        newsInfo = getIntent().getExtras().getString("newsDetails");
        String newsDate = getIntent().getExtras().getString("newsDate");
        newsImg = getIntent().getExtras().getString("urlImg");

        txtNewsViewTitle.setText(newsTitle);
        txtNewsDate.setText(newsDate);
        txtNewsViewInfo.setText(newsInfo);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
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

        imageLoader.displayImage(newsImg,ivNewsViewImg, options);
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

            Intent i = new Intent(getApplicationContext(),NewsAddActivity.class);
            i.putExtra("newsId",newsId);
            i.putExtra("newsCatId",newsCatId);
            i.putExtra("newsTitle",newsTitle);
            i.putExtra("newsInfo",newsInfo);
            i.putExtra("newsImg",newsImg);
            startActivity(i);
        }
        else if (id == R.id.menu_delete) {
            AlertDialog ad;
            AlertDialog.Builder build = new AlertDialog.Builder(FullNewsActivity.this);
            build.setMessage("Are you sure want to delete?");
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GetnewsDelete newsDelete=new GetnewsDelete();
                    newsDelete.execute();
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

    private class GetnewsDelete extends AsyncTask<String,Void,String> {

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(FullNewsActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            JSONObject newsDelete = new JSONObject();
            try {
                newsDelete.put("news_id",newsId);
                Postdata postdata = new Postdata();
                Log.d("Like", "data" + newsDelete.toString());
                String newDelete = postdata.post(url+"delete_news.php", newsDelete.toString());
                JSONObject jsonObject = new JSONObject(newDelete);
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
                Intent i = new Intent(getApplicationContext(),NewsActivity.class);
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
