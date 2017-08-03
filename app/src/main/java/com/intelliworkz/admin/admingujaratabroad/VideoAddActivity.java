package com.intelliworkz.admin.admingujaratabroad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class VideoAddActivity extends AppCompatActivity {

    EditText txtVideoTitle;
    ImageView ivVideoImgAdd;
    Button btnVideoUpload,btnVideoAdd;
    ProgressDialog dialog;
    String status="0",message,videoTitle,video_id,status1,videoIdView,videoTitleView,videoUrlLinkView;
    String url=Common.SERVICE_URL;
    private String filePath = null;
    TextInputLayout inputLayoutVideoTitle;
    private static final int SELECTED_Video = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_video);
        setContentView(R.layout.activity_video_add);

        if(getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        txtVideoTitle = (EditText)findViewById(R.id.txtVideoTitle);
        ivVideoImgAdd = (ImageView) findViewById(R.id.ivVideoImgAdd);
        btnVideoUpload = (Button) findViewById(R.id.btnVideoUpload);
        btnVideoAdd = (Button) findViewById(R.id.btnVideoAdd);

        inputLayoutVideoTitle = (TextInputLayout) findViewById(R.id.input_layout_videoTitle);

        txtVideoTitle.addTextChangedListener(new MyTextWatcher(txtVideoTitle));

        btnVideoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,SELECTED_Video);

            }
        });
        if(getIntent().getExtras().getString("videoIdView") != null)
        {
            videoIdView = getIntent().getExtras().getString("videoIdView");
            videoTitleView = getIntent().getExtras().getString("videoTitleView");
            videoUrlLinkView = getIntent().getExtras().getString("videoUrlLinkView");

            txtVideoTitle.setText(videoTitleView);
            btnVideoAdd.setText(getString(R.string.btn_edit_Video));

            Bitmap bitmap = null;
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14) {
                    // no headers included
                    mediaMetadataRetriever.setDataSource(videoUrlLinkView, new HashMap<String, String>());
                }
                else {
                    mediaMetadataRetriever.setDataSource(videoUrlLinkView);
                }
                bitmap = mediaMetadataRetriever.getFrameAtTime();
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (mediaMetadataRetriever != null)
                    mediaMetadataRetriever.release();
            }
            ivVideoImgAdd.setImageBitmap(bitmap);

            btnVideoAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!txtVideoTitle.getText().toString().equals(""))
                    {
                        videoTitle=txtVideoTitle.getText().toString();

                        GetUpdateVideo updateVideo=new GetUpdateVideo();
                        updateVideo.execute();
                    }
                    else if (!validateTitle())
                    {
                        return;
                    }
                }
            });
        }
        else if(getIntent().getExtras().getString("videoAdd").equals("videoAdd"))
        {
            btnVideoAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!txtVideoTitle.getText().toString().equals("") && filePath != null)
                    {
                        videoTitle=txtVideoTitle.getText().toString();

                        GetInsertVideo insertVideo=new GetInsertVideo();
                        insertVideo.execute();
                    }
                    else if (!validateTitle())
                    {
                        return;
                    }
                }
            });
        }
    }

    private boolean validateTitle() {
        if (txtVideoTitle.getText().toString().trim().isEmpty())
        {
            inputLayoutVideoTitle.setError(getString(R.string.err_msg_videoTitle));
            requestFocus(txtVideoTitle);
            return false;
        }
        else
        {
            inputLayoutVideoTitle.setErrorEnabled(false);
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

                case R.id.txtVideoTitle:
                    validateTitle();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECTED_Video:
                if(resultCode == RESULT_OK)
                {
                    Uri uri = data.getData();
                    String[]projection={MediaStore.Video.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri,projection,null,null,null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
                    ivVideoImgAdd.setImageBitmap(bmThumbnail);

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

    private class GetInsertVideo extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(VideoAddActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String langId = LoginActivity.str_language_Code;
            JSONObject videoAdd = new JSONObject();
            try {
                videoAdd.put("video_title",videoTitle);
                videoAdd.put("lang",langId);

                Postdata postdata = new Postdata();
                Log.d("Like", "data" + videoAdd.toString());
                String videoIn = postdata.post(url+"insert_video.php",videoAdd.toString());
                JSONObject jsonObject = new JSONObject(videoIn);
                status = jsonObject.getString("status");
                if (status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    video_id=jsonObject.getString("video_id");
                }
                else
                {
                    message=jsonObject.getString("message");
                }
                String videoId = video_id;
                File sourceFile = new File(filePath);
                videoupload vu = new videoupload();
                String videoPathId = vu.post(url+"upload_video.php",sourceFile,videoId);
                JSONObject addVideo = new JSONObject(videoPathId);
                status1 = addVideo.getString("status");

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
                status = "0";
                Log.d("Like","Successfully");
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

    private class GetUpdateVideo extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(VideoAddActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String langId = LoginActivity.str_language_Code;
            JSONObject videoUp = new JSONObject();
            try {
                videoUp.put("video_id",videoIdView);
                videoUp.put("video_title",videoTitle);
                videoUp.put("lang",langId);

                Postdata postdata = new Postdata();
                Log.d("Like", "data" + videoUp.toString());
                String videoIn = postdata.post(url+"edit_video.php",videoUp.toString());
                JSONObject jsonObject = new JSONObject(videoIn);
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
                    videoupload vu = new videoupload();
                    String videoPathId = vu.post(url+"upload_video.php",sourceFile,videoIdView);
                    JSONObject addVideo = new JSONObject(videoPathId);
                    status1 = addVideo.getString("status");
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
