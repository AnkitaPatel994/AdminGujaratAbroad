package com.intelliworkz.admin.admingujaratabroad;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsListFragment extends Fragment {
    public NewsListFragment() {
        // Required empty public constructor
    }

    View view;
    public String title;
    public int page;

    RecyclerView rvNewsList;
    String status,message;
    ArrayList<NewsModel> newsArrayList=new ArrayList<>();
    String url="http://www.codeclinic.in/gujratabroad_webservice/";

    public static NewsListFragment newInstance(int page, String title) {
        NewsListFragment fragmentFirst = new NewsListFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_news_list, container, false);

        //Toast.makeText(getActivity(), "pos:\n"+title, Toast.LENGTH_SHORT).show();
        /*tv = (TextView) view.findViewById(R.id.tv_id);
        tv.setText("Selected Tab :\n\t"+ page + " -- " + title);*/

        rvNewsList=(RecyclerView)view.findViewById(R.id.rvNewsList);
        rvNewsList.setHasFixedSize(true);

        RecyclerView.LayoutManager rvLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rvNewsList.setLayoutManager(rvLayoutManager);

        GetNewsList getNewsList=new GetNewsList();
        getNewsList.execute();

        return view;
    }

    private class GetNewsList extends AsyncTask<String,Void,String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(getActivity());
            dialog.setTitle("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            newsArrayList.clear();
            String langId = LoginActivity.str_language_Code;
            String news_cat=title;
            String last_news_id="0";
            JSONObject newsList=new JSONObject();
            try {
                newsList.put("lang",langId);
                newsList.put("news_cat",news_cat);
                newsList.put("last_news_id",last_news_id);
                Postdata postdata=new Postdata();
                String news=postdata.post(url+"fatch_newslist.php",newsList.toString());
                JSONObject j=new JSONObject(news);
                status=j.getString("status");
                if(status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    message = j.getString("message");
                    JSONArray newsarr=j.getJSONArray("tbl_newslist");
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
                    message = j.getString("message");
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
            RecyclerView.Adapter rvNewsAdapter=new NewsAdapter(getActivity(),newsArrayList);
            rvNewsList.setAdapter(rvNewsAdapter);
        }
    }
}
