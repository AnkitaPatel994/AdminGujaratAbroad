package com.intelliworkz.admin.admingujaratabroad;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    Spinner spCatFilter;
    Button btnNewsFilter;
    TextView txtStartDate,txtEndDate;
    LinearLayout layStartDate,layEndDate;
    ArrayList<String> arrayCat=new ArrayList<>();
    ArrayList<String> arrayId=new ArrayList<>();
    ArrayList<NewsModel> newsArrayList=new ArrayList<>();
    String url=Common.SERVICE_URL;
    String catId,status,message,startDate,endDate;
    RecyclerView rvNewsFilter;
    int startYear=2017;
    int startMonth=5;
    int startDay=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        if(getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        spCatFilter = (Spinner)findViewById(R.id.spCatFilter);
        btnNewsFilter = (Button)findViewById(R.id.btnNewsFilter);
        txtStartDate = (TextView)findViewById(R.id.txtStartDate);
        txtEndDate = (TextView)findViewById(R.id.txtEndDate);
        layStartDate=(LinearLayout)findViewById(R.id.layStartDate);
        layEndDate=(LinearLayout)findViewById(R.id.layEndDate);

        GetspCatFilter spCateFilter=new GetspCatFilter();
        spCateFilter.execute();

        spCatFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                catId = arrayId.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        layStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(FilterActivity.this,new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth)
                    {
                        startYear=year;
                        startMonth=monthOfYear;
                        startDay=dayOfMonth;
                        txtStartDate.setText(dayOfMonth + " - " + (startMonth + 1) + " - " + startYear);
                        startDate = String.valueOf(startYear + "-" + (startMonth + 1) + "-"+dayOfMonth);
                    }
                }, startYear, startMonth, startDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        layEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(FilterActivity.this,new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth)
                    {
                        startYear=year;
                        startMonth=monthOfYear;
                        startDay=dayOfMonth;
                        txtEndDate.setText(dayOfMonth + " - " + (startMonth + 1) + " - " + startYear);
                        endDate = String.valueOf(startYear + "-" + (startMonth + 1) + "-"+dayOfMonth);
                    }
                }, startYear, startMonth, startDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        btnNewsFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvNewsFilter = (RecyclerView)findViewById(R.id.rvNewsFilter);
                rvNewsFilter.setHasFixedSize(true);

                RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(FilterActivity.this, LinearLayoutManager.VERTICAL, false);
                rvNewsFilter.setLayoutManager(rvLayoutManager);

                GetNewsList newsList = new GetNewsList();
                newsList.execute();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private class GetspCatFilter extends AsyncTask<String,Void,String> {
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

                        arrayCat.add(catName);
                        arrayId.add(catId);
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

            ArrayAdapter ad=new ArrayAdapter(FilterActivity.this,R.layout.support_simple_spinner_dropdown_item,arrayCat);
            spCatFilter.setAdapter(ad);
        }
    }

    private class GetNewsList extends AsyncTask<String,Void,String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(FilterActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            newsArrayList.clear();
            String langId = LoginActivity.str_language_Code;
            JSONObject joNews = new JSONObject();
            try {
                joNews.put("news_cat",catId);
                joNews.put("start_date",startDate);
                joNews.put("end_date",endDate);
                joNews.put("lang",langId);

                Postdata postdata = new Postdata();
                Log.d("Like", "data" + joNews.toString());
                String newslist = postdata.post(url+"fatch_newslist_bydate.php",joNews.toString());
                JSONObject jsonObject = new JSONObject(newslist);
                status = jsonObject.getString("status");
                if (status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    JSONArray newsarr=jsonObject.getJSONArray("tbl_newslist");
                    for(int i=0;i<newsarr.length();i++)
                    {
                        JSONObject newsJson=newsarr.getJSONObject(i);
                        String newsId=newsJson.getString("news_id");
                        String newsCatId=newsJson.getString("news_cat");
                        String newsTitle=newsJson.getString("news_title");
                        String newsDetails=newsJson.getString("news_desc");
                        String newsImg=newsJson.getString("news_img");
                        String newsDate=newsJson.getString("news_date");

                        NewsModel n=new NewsModel(newsId,newsCatId,newsTitle,newsDetails,newsImg,newsDate);
                        newsArrayList.add(n);
                    }
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
                RecyclerView.Adapter rvNewsAdapter=new NewsAdapter(FilterActivity.this,newsArrayList);
                rvNewsFilter.setAdapter(rvNewsAdapter);
            }
            else
            {
                Toast.makeText(getApplicationContext(),""+message,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
