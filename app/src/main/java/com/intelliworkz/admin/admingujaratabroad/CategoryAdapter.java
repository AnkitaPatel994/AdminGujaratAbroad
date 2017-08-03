package com.intelliworkz.admin.admingujaratabroad;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{
    Context context;
    ArrayList<HashMap<String, String>> catList;
    View v;
    public CategoryAdapter(Context context, ArrayList<HashMap<String, String>> catList) {
        this.context=context;
        this.catList=catList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtCat.setText(catList.get(position).get("catName"));

    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCat;
        public ViewHolder(View v) {
            super(v);
            txtCat=(TextView)v.findViewById(R.id.txtCat);

        }
    }
}