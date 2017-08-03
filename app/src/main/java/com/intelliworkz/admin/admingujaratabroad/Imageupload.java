package com.intelliworkz.admin.admingujaratabroad;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by DELL on 09-10-2016.
 */

public class Imageupload {

    private final OkHttpClient client = new OkHttpClient();
    Response response;
    String result;

    public static boolean isEmpty(CharSequence str)
    {
        if(str == null || str.length() == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    String post(String serverURL, File file, String id) {
        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image

        client.setConnectTimeout(1000, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(1000, TimeUnit.SECONDS);

        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("add_id", id)
                .build();

        Request request = new Request.Builder()
                .url(serverURL)
                .post(requestBody)
                .build();

        try {

            response = client.newCall(request).execute();

            result = response.body().string();

            //Log.i("data",result);
        } catch (Exception e) {

            e.printStackTrace();
            return result = "0";

        }

        if(isEmpty(result))
        {
            result = "0";
        }

        Log.i("result", result);

        return result;
    }
}
