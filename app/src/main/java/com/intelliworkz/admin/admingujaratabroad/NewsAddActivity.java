package com.intelliworkz.admin.admingujaratabroad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ScrollingTabContainerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class NewsAddActivity extends AppCompatActivity {
    EditText txtNewsTitle,txtNewsInfo;
    Button btnNewsAdd,btnImgChoose;
    ImageView imgNewsAdd;
    Spinner spCat;
    private static final int SELECTED_PICTURE = 1;
    ArrayList<String> arrayCat=new ArrayList<>();
    ArrayList<String> arrayId=new ArrayList<>();
    String url=Common.SERVICE_URL;
    ProgressDialog dialog;
    String catId,news_id,status1,filePath = null;
    String NewsTitle,NewsDetail,status,message,newsId,newsInfo,newsTitle,urlNewsImg;
    TextInputLayout inputLayoutNewsTitle,inputLayoutNewsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_news);
        setContentView(R.layout.activity_news_add);

        if(getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        btnNewsAdd=(Button)findViewById(R.id.btnNewsAdd);
        btnImgChoose=(Button)findViewById(R.id.btnImgChoose);
        txtNewsInfo=(EditText)findViewById(R.id.txtNewsInfo);
        txtNewsTitle=(EditText)findViewById(R.id.txtNewsTitle);
        imgNewsAdd=(ImageView)findViewById(R.id.imgNewsAdd);
        spCat=(Spinner)findViewById(R.id.spCat);

        inputLayoutNewsTitle = (TextInputLayout) findViewById(R.id.input_layout_newsTitle);
        inputLayoutNewsInfo = (TextInputLayout) findViewById(R.id.input_layout_newsInfo);

        txtNewsTitle.addTextChangedListener(new MyTextWatcher(txtNewsTitle));
        txtNewsInfo.addTextChangedListener(new MyTextWatcher(txtNewsInfo));

        final GetspCategory spCategory=new GetspCategory();
        spCategory.execute();

        spCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                catId = arrayId.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(getIntent().getExtras().getString("newsId") != null)
        {
            newsId = getIntent().getExtras().getString("newsId");



            newsInfo = getIntent().getExtras().getString("newsInfo");
            newsTitle = getIntent().getExtras().getString("newsTitle");
            urlNewsImg = getIntent().getExtras().getString("newsImg");

            txtNewsTitle.setText(newsTitle);
            txtNewsInfo.setText(newsInfo);
            btnNewsAdd.setText(getString(R.string.btn_edit_news));

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

            imageLoader.displayImage(urlNewsImg,imgNewsAdd, options);

            btnNewsAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!txtNewsTitle.getText().toString().equals("") && !txtNewsInfo.getText().toString().equals(""))
                    {
                        NewsTitle=txtNewsTitle.getText().toString();
                        NewsDetail=txtNewsInfo.getText().toString();
                        GetNewsUpdate getNewsUpdate=new GetNewsUpdate();
                        getNewsUpdate.execute();
                    }
                    else if (!validateNewsTitle())
                    {
                        return;
                    }
                    else if (!validateNewsInfo())
                    {
                        return;
                    }

                }
            });


        }
        else if(getIntent().getExtras().getString("add").equals("add"))
        {

            btnNewsAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!txtNewsTitle.getText().toString().equals("") && !txtNewsInfo.getText().toString().equals("") && filePath != null)
                    {
                        NewsTitle=txtNewsTitle.getText().toString();
                        NewsDetail=txtNewsInfo.getText().toString();
                        GetNewsAdd getNewsAdd=new GetNewsAdd();
                        getNewsAdd.execute();
                    }
                    else if (!validateNewsTitle())
                    {
                        return;
                    }
                    else if (!validateNewsInfo())
                    {
                        return;
                    }

                }
            });
        }

        btnImgChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,SELECTED_PICTURE);
            }
        });
    }

    private boolean validateNewsTitle() {
        if (txtNewsTitle.getText().toString().trim().isEmpty())
        {
            inputLayoutNewsTitle.setError(getString(R.string.err_msg_newsTitle));
            requestFocus(txtNewsTitle);
            return false;
        }
        else
        {
            inputLayoutNewsTitle.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateNewsInfo() {
        if (txtNewsInfo.getText().toString().trim().isEmpty())
        {
            inputLayoutNewsInfo.setError(getString(R.string.err_msg_newsInfo));
            requestFocus(txtNewsInfo);
            return false;
        }
        else
        {
            inputLayoutNewsInfo.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.txtNewsTitle:
                    validateNewsTitle();
                    break;
                case R.id.txtNewsInfo:
                    validateNewsInfo();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECTED_PICTURE:
                if(resultCode == RESULT_OK)
                {
                    File f = new File(Environment.getExternalStorageDirectory().toString());
                    if (f != null) {

                        for (File temp : f.listFiles()) {
                            if (temp.getName().equals("temp.jpg")) {
                                f = temp;
                                break;
                            }
                        }
                    }
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    imgNewsAdd.setImageBitmap(bitmap);
                    filePath = Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(filePath, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Uri uri = data.getData();
                    String[]projection={MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri,projection,null,null,null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap yourSelectedImg = BitmapFactory.decodeFile(filePath);
                    Drawable d = new BitmapDrawable(yourSelectedImg);

                    imgNewsAdd.setBackground(d);
                }
                break;
            default:
                break;
        }
    }

    private class GetspCategory extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ArrayAdapter ad=new ArrayAdapter(NewsAddActivity.this,R.layout.support_simple_spinner_dropdown_item,arrayCat);
            spCat.setAdapter(ad);
            if(getIntent().getExtras().getString("newsId") != null)
            {
                String newsCatId = getIntent().getExtras().getString("newsCatId");
                spCat.setSelection(Integer.parseInt(newsCatId)-1);
            }

        }
    }

    private class GetNewsAdd extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(NewsAddActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String news_cat=catId;
            String news_title=NewsTitle;
            String news_desc=NewsDetail;
            String langId = LoginActivity.str_language_Code;

            JSONObject NewsAdd=new JSONObject();

            try {
                NewsAdd.put("news_cat",news_cat);
                NewsAdd.put("news_title",news_title);
                NewsAdd.put("news_desc",news_desc);
                NewsAdd.put("lang",langId);

                Postdata postdata = new Postdata();
                Log.d("Like", "data" + NewsAdd.toString());
                String cate = postdata.post(url+"insert_news.php",String.valueOf(NewsAdd));
                JSONObject jsonObject = new JSONObject(cate);
                status = jsonObject.getString("status");
                if (status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    news_id=jsonObject.getString("news_id");
                }
                else
                {
                    message=jsonObject.getString("message");
                }

                File sourceFile = new File(filePath);
                String newsId = news_id;

                Imageuploadnews imgNews = new Imageuploadnews();
                String imgPathId = imgNews.post(url+"upload_new_img.php",sourceFile,newsId);
                JSONObject adImg = new JSONObject(imgPathId);
                status1 = adImg.getString("status");


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
                Intent i=new Intent(getApplicationContext(),NewsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            else
            {
                Toast.makeText(getApplicationContext(),""+message,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetNewsUpdate extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(NewsAddActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String langId = LoginActivity.str_language_Code;
            JSONObject NewsUp=new JSONObject();
            try {

                NewsUp.put("news_id",newsId);
                NewsUp.put("news_cat",catId);
                NewsUp.put("news_title",NewsTitle);
                NewsUp.put("news_desc",NewsDetail);
                NewsUp.put("lang",langId);

                Postdata postdata = new Postdata();
                Log.d("Like", "data" + NewsUp.toString());
                String newss = postdata.post(url+"edit_news.php",NewsUp.toString());
                JSONObject jsonObject = new JSONObject(newss);
                status = jsonObject.getString("status");
                if (status.equals("1"))
                {
                    Log.d("Like","Successfully");
                }
                else
                {
                    message=jsonObject.getString("message");
                }
                if(filePath != null) {
                    File sourceFile = new File(filePath);
                    String newId = newsId;

                    Imageuploadnews imgNews = new Imageuploadnews();

                    String imgPathId = imgNews.post(url + "upload_new_img.php", sourceFile, newId);
                    JSONObject adImg = new JSONObject(imgPathId);
                    status1 = adImg.getString("status");
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
                Intent i=new Intent(getApplicationContext(),NewsActivity.class);
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
