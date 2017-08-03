package com.intelliworkz.admin.admingujaratabroad;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvertiseAddActivity extends AppCompatActivity {
    ImageView imgAdAdd;
    EditText txtadTitle,txtadUrl;
    Button btnAdImgChoose,btnAdAdd;
    String url=Common.SERVICE_URL;
    ProgressDialog dialog;
    TextInputLayout inputLayoutTitle,inputLayoutADUrl;
    String add_id,adTile,adUrl,status,status1,message,adUpId,adUpTitle,adUpLink,adUpImg;
    private String filePath = null;

    private static final int SELECTED_PICTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_advertise);
        setContentView(R.layout.activity_advertise_add);

        if(getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imgAdAdd = (ImageView)findViewById(R.id.imgAdAdd);
        btnAdImgChoose = (Button)findViewById(R.id.btnAdImgChoose);
        btnAdAdd = (Button)findViewById(R.id.btnAdAdd);
        txtadTitle = (EditText) findViewById(R.id.txtadTitle);
        txtadUrl = (EditText)findViewById(R.id.txtadUrl);

        inputLayoutTitle = (TextInputLayout) findViewById(R.id.input_layout_title);
        inputLayoutADUrl = (TextInputLayout) findViewById(R.id.input_layout_adUrl);

        txtadTitle.addTextChangedListener(new MyTextWatcher(txtadTitle));
        txtadUrl.addTextChangedListener(new MyTextWatcher(txtadUrl));

        btnAdImgChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((ContextCompat.checkSelfPermission(AdvertiseAddActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(AdvertiseAddActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AdvertiseAddActivity.this,
                            Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(AdvertiseAddActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i,SELECTED_PICTURE);
                    } else {
                        ActivityCompat.requestPermissions(AdvertiseAddActivity.this, new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE"}, 200);
                        // No explanation needed, we can request the permission.
                    }
                } else {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i,SELECTED_PICTURE);
                }
            }
        });

        if(getIntent().getExtras().getString("adId") != null)
        {
            adUpId = getIntent().getExtras().getString("adId");
            adUpTitle = getIntent().getExtras().getString("adTitle");
            adUpLink = getIntent().getExtras().getString("adWebUrl");
            adUpImg = getIntent().getExtras().getString("adImg");

            txtadTitle.setText(adUpTitle);
            txtadUrl.setText(adUpLink);
            btnAdAdd.setText(getString(R.string.btn_edit_Advertisement));

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

            imageLoader.displayImage(adUpImg,imgAdAdd, options);

            btnAdAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!txtadTitle.getText().toString().equals(""))
                    {
                        adTile=txtadTitle.getText().toString();
                        adUrl=txtadUrl.getText().toString();

                        GetUpdateAd updateAd = new GetUpdateAd();
                        updateAd.execute();
                    }
                    else if (!validateTitle())
                    {
                        return;
                    }
                }
            });

        }
        else if(getIntent().getExtras().getString("add").equals("add"))
        {
            btnAdAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!txtadTitle.getText().toString().equals("") && filePath != null)
                    {
                        adTile=txtadTitle.getText().toString();
                        adUrl=txtadUrl.getText().toString();

                        GetinsertAdd insertAdd = new GetinsertAdd();
                        insertAdd.execute();
                    }
                    else if (!validateTitle())
                    {
                        return;
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "!Please Choose Image....", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



    }

    private boolean validateTitle() {
        if (txtadTitle.getText().toString().trim().isEmpty())
        {
            inputLayoutTitle.setError(getString(R.string.err_msg_title));
            requestFocus(txtadTitle);
            return false;
        }
        else
        {
            inputLayoutTitle.setErrorEnabled(false);
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

                case R.id.txtadTitle:
                    validateTitle();
                    break;
            }
        }
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
                    //Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    imgAdAdd.setImageBitmap(bitmap);

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

                    imgAdAdd.setBackground(d);

                    imgAdAdd.setImageURI(uri);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private class GetinsertAdd extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(AdvertiseAddActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String add_title=adTile;
            String add_link=adUrl;

            JSONObject advertiseAdd = new JSONObject();
            try {
                advertiseAdd.put("add_title",add_title);
                advertiseAdd.put("add_link",add_link);

                Postdata postdata = new Postdata();
                Log.d("Like", "data" + advertiseAdd.toString());
                String cate = postdata.post(url+"insert_add.php", String.valueOf(advertiseAdd));
                JSONObject jsonObject = new JSONObject(cate);
                status = jsonObject.getString("status");
                if (status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    add_id=jsonObject.getString("add_id");
                }
                else
                {
                    message=jsonObject.getString("message");
                }

                File sourceFile = new File(filePath);
                String addId = add_id;

                Imageupload img = new Imageupload();

                String imgPathId = img.post(url+"upload_add_img.php",sourceFile,addId);
                JSONObject adImg = new JSONObject(imgPathId);
                status1 = adImg.getString("status");

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Retry",Toast.LENGTH_SHORT).show();
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

    private class GetUpdateAd extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(AdvertiseAddActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            JSONObject advertiseEdit = new JSONObject();
            try {
                advertiseEdit.put("add_id",adUpId);
                advertiseEdit.put("add_title",adTile);
                advertiseEdit.put("add_link",adUrl);

                Postdata postdata = new Postdata();
                Log.d("Like", "data" + advertiseEdit.toString());
                String adEdit = postdata.post(url+"edit_add.php", advertiseEdit.toString());
                JSONObject jsonObject = new JSONObject(adEdit);
                status = jsonObject.getString("status");
                if (status.equals("1"))
                {
                    Log.d("Like","Successfully");
                }
                else
                {
                    message=jsonObject.getString("message");
                }

                if(filePath != null){
                    File sourceFile = new File(filePath);
                    String addId = adUpId;

                    Imageupload img = new Imageupload();

                    String imgPathId = img.post(url+"upload_add_img.php",sourceFile,addId);
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
            if (status.equals("1")) {
                Log.d("Like","Successfully");
                Intent i = new Intent(getApplicationContext(),AdvertiseActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
