package com.intelliworkz.admin.admingujaratabroad;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class VideoViewActivity extends AppCompatActivity {
    //VideoView vvVideo;
    UniversalVideoView vvVideo;
    UniversalMediaController mVideo;
    TextView txtVVTile;
    String videoIdView,videoTitleView,videoUrlLinkView,status,message;
    String url=Common.SERVICE_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_video);
        setContentView(R.layout.activity_video_view);

        if(getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        vvVideo = (UniversalVideoView)findViewById(R.id.vvVideo);
        mVideo = (UniversalMediaController)findViewById(R.id.media_controller);
        txtVVTile = (TextView)findViewById(R.id.txtVVTile);

        videoIdView = getIntent().getExtras().getString("videoIdView");

        videoTitleView = getIntent().getExtras().getString("videoTitleView");
        txtVVTile.setText(videoTitleView);

        videoUrlLinkView = getIntent().getExtras().getString("videoUrlLinkView");

        vvVideo.setMediaController(mVideo);
        vvVideo.setVideoURI(Uri.parse(videoUrlLinkView));
        vvVideo.requestFocus();
        vvVideo.start();

        vvVideo.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
            @Override
            public void onScaleChange(boolean isFullscreen) {
                if (isFullscreen) {
                    ViewGroup.LayoutParams layoutParams = vvVideo.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    vvVideo.setLayoutParams(layoutParams);

                    ViewGroup.LayoutParams layoutParams1 = mVideo.getLayoutParams();
                    layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams1.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    mVideo.setLayoutParams(layoutParams1);

                    txtVVTile.setVisibility(View.GONE);
                    getSupportActionBar().hide();

                } else {

                    ViewGroup.LayoutParams layoutParams = vvVideo.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = 300;
                    vvVideo.setLayoutParams(layoutParams);

                    ViewGroup.LayoutParams layoutParams1 = mVideo.getLayoutParams();
                    layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams1.height = 300;
                    mVideo.setLayoutParams(layoutParams1);

                    txtVVTile.setVisibility(View.VISIBLE);
                    getSupportActionBar().show();
                }
            }

            @Override
            public void onPause(MediaPlayer mediaPlayer) {

            }

            @Override
            public void onStart(MediaPlayer mediaPlayer) {

            }

            @Override
            public void onBufferingStart(MediaPlayer mediaPlayer) {

            }

            @Override
            public void onBufferingEnd(MediaPlayer mediaPlayer) {

            }
        });

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
            Intent i = new Intent(getApplicationContext(),VideoAddActivity.class);
            i.putExtra("videoIdView",videoIdView);
            i.putExtra("videoTitleView",videoTitleView);
            i.putExtra("videoUrlLinkView",videoUrlLinkView);
            startActivity(i);
        }
        else if (id == R.id.menu_delete) {
            AlertDialog ad;
            AlertDialog.Builder build = new AlertDialog.Builder(VideoViewActivity.this);
            build.setMessage("Are you sure want to delete?");
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GetvideoDelete videoDelete=new GetvideoDelete();
                    videoDelete.execute();
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

    private class GetvideoDelete extends AsyncTask<String,Void,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(VideoViewActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            JSONObject videoDelete = new JSONObject();
            try {
                videoDelete.put("video_id",videoIdView);
                Postdata postdata = new Postdata();
                Log.d("Like", "data" + videoDelete.toString());
                String vDelete = postdata.post(url+"delete_video.php", videoDelete.toString());
                JSONObject jsonObject = new JSONObject(vDelete);
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
                Intent i = new Intent(getApplicationContext(),VideoActivity.class);
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
