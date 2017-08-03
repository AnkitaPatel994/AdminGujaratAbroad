package com.intelliworkz.admin.admingujaratabroad;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by PC2 on 02-Feb-17.
 */

public class HttpHandler {

    public String serverConnection(String url) {
        String response=null;
        try
        {
            URL u=new URL(url);
            HttpURLConnection connection=(HttpURLConnection)u.openConnection();
            connection.setRequestMethod("GET");
            InputStream is=new BufferedInputStream(connection.getInputStream());
            response=convertToString(is);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String convertToString(InputStream is)
    {

        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        StringBuilder sb=new StringBuilder();
        String line;

        try
        {
            while((line=br.readLine())!=null)
            {
                sb.append(line).append("\n");

            }
            br.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
}

