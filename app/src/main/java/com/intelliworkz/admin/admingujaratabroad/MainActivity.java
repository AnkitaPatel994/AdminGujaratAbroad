package com.intelliworkz.admin.admingujaratabroad;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        layout = (RelativeLayout)findViewById(R.id.layout);

        ConnectivityManager cm = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni!=null && ni.isConnected())
        {
            Thread background = new Thread()
            {
                public void run()
                {
                    try {
                        sleep(5*1000);
                        Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(i);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            background.start();
        }
        else
        {
            Snackbar.make(layout,"Network Unavailable",Snackbar.LENGTH_LONG).setAction("Action",null).show();
        }
    }
}
