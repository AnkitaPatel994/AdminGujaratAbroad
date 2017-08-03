package com.intelliworkz.admin.admingujaratabroad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class CatAddActivity extends AppCompatActivity {
    EditText txtAddcat;
    Button btnAddcat;
    String addCat,status,message;
    ProgressDialog dialog;
    String url=Common.SERVICE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_category);
        setContentView(R.layout.activity_cat_add);

        if(getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        txtAddcat = (EditText)findViewById(R.id.txtAddcat);
        btnAddcat = (Button) findViewById(R.id.btnAddcat);

        btnAddcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtAddcat.getText().toString().equals(""))
                {
                    addCat = txtAddcat.getText().toString();
                    GetCat getCat=new GetCat();
                    getCat.execute();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"!Please Not Black....",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private class GetCat extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(CatAddActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String cat_name = addCat;
            JSONObject data_favorite = new JSONObject();

            try {
                data_favorite.put("cat_name",cat_name);
                Postdata postdata = new Postdata();
                Log.d("Like", "data" + data_favorite.toString());
                String cate = postdata.post(url+"insert_category.php", String.valueOf(data_favorite));
                JSONObject jsonObject = new JSONObject(cate);
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
                Log.d("Like","Successfully");
                Intent i=new Intent(getApplicationContext(),CategoryActivity.class);
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
