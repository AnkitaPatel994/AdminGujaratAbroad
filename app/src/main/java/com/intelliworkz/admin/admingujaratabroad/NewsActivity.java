package com.intelliworkz.admin.admingujaratabroad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;
    private ViewPager pager;

    ArrayList<String> tabTitles=new ArrayList<>();
    public static ArrayList<String> tabTitlesId=new ArrayList<>();
    Pager adapter;
    String url=Common.SERVICE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),NewsAddActivity.class);
                i.putExtra("add","add");
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
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        tabTitles.clear();
        tabTitlesId.clear();

        pager = (ViewPager) findViewById(R.id.container);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager);

        GetTabCategrory tabCat=new GetTabCategrory();
        tabCat.execute();
        
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filtertoolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_filter) {

            Intent i = new Intent(getApplicationContext(),FilterActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
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
        else if (id == R.id.nav_videos)
        {
            Intent i = new Intent(getApplicationContext(),VideoActivity.class);
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

    private class GetTabCategrory extends AsyncTask<String,Void,String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(NewsActivity.this);
            dialog.setTitle("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String response;
            HttpHandler h=new HttpHandler();
            response= h.serverConnection(url+"fatch_news_category.php");
            if(response!=null)
            {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray category=jsonObject.getJSONArray("tbl_category");
                    for (int i=0;i<category.length();i++)
                    {
                        JSONObject j=category.getJSONObject(i);

                        String catName=j.getString("cat_name");
                        String catId=j.getString("cat_id");

                        tabTitlesId.add(catId);
                        tabTitles.add(catName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Server Connection Not Found..",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            adapter = new Pager(getSupportFragmentManager());
            for (int i = 0; i < tabTitles.size(); i++) {
                adapter.addFrag(new NewsListFragment(), tabTitles.get(i).trim());
            }
            pager.setAdapter(adapter);

        }
    }
}
