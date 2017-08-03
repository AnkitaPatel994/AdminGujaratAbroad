package com.intelliworkz.admin.admingujaratabroad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView rvVideo;
    RecyclerView.LayoutManager rvVideoManager;
    RecyclerView.Adapter rvVideoAdapter;
    ArrayList<HashMap<String,String>> videoListArray=new ArrayList<>();
    String url=Common.SERVICE_URL;
    ProgressDialog dialog;
    String status,message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),VideoAddActivity.class);
                i.putExtra("videoAdd","videoAdd");
                startActivity(i);
            }
        });
        fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorFloating));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        rvVideo=(RecyclerView)findViewById(R.id.rvVideo);
        rvVideo.setHasFixedSize(true);

        rvVideoManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvVideo.setLayoutManager(rvVideoManager);

        GetrvVideo rvVideo=new GetrvVideo();
        rvVideo.execute();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home)
        {
            Intent i = new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(i);
            finish();
        }
        if (id == R.id.nav_newscat)
        {
            Intent i = new Intent(getApplicationContext(),CategoryActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_news)
        {
            Intent i = new Intent(getApplicationContext(),NewsActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_advertisement)
        {
            Intent i = new Intent(getApplicationContext(),AdvertiseActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_logout)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GetrvVideo extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(VideoActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String langId = LoginActivity.str_language_Code;
            JSONObject videoList=new JSONObject();
            try {
                videoList.put("lang",langId);

                Postdata postdata=new Postdata();
                String videopd=postdata.post(url+"fatch_videolist.php",videoList.toString());
                JSONObject j=new JSONObject(videopd);
                status=j.getString("status");

                if(status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    message = j.getString("message");
                    JSONArray video=j.getJSONArray("tbl_newslist");
                    for (int i=0;i<video.length();i++)
                    {
                        HashMap<String,String > videofe = new HashMap<>();
                        JSONObject jo=video.getJSONObject(i);

                        String video_id=jo.getString("video_id");
                        String video_title=jo.getString("video_title");
                        String video_thumb=jo.getString("video_thumb");

                        videofe.put("video_id",video_id);
                        videofe.put("video_title",video_title);
                        videofe.put("video_thumb",url+"video/"+video_thumb);

                        videoListArray.add(videofe);
                    }
                }
                else
                {
                    message = j.getString("message");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            rvVideoAdapter=new VideoAdapter(VideoActivity.this,videoListArray);
            rvVideo.setAdapter(rvVideoAdapter);
        }
    }
}
