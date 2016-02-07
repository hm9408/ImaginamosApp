package com.hugo.imaginamosapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.App;

/**
 * Created by hm94__000 on 06-Feb-16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.AppVH>{


    ArrayList<App> apps;

    @Override
    public AppVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new AppVH(v);
    }

    @Override
    public void onBindViewHolder(AppVH holder, int position) {
        holder.name.setText(apps.get(position).getName());
        holder.artist.setText(apps.get(position).getArtist());
        String p = "";
        if(apps.get(position).getPrice()==0.0) p="Free";
        else p= "$"+apps.get(position).getPrice()+" "+apps.get(position).getCurrency();
        holder.price.setText(p);
        Picasso.with(AppListActivity.context).load(apps.get(position).getUrlImLarge()).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    RecyclerViewAdapter(ArrayList<App> applications){
        this.apps = applications;
    }

    // Clean all elements of the recycler
    public void clear() {
        apps.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(ArrayList<App> list) {
        apps.addAll(list);
        notifyDataSetChanged();
    }

    public class AppVH extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cv;
        TextView name;
        TextView artist;
        TextView price;
        ImageView icon;
        private final Context c;

        public AppVH(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardview);
            name = (TextView) itemView.findViewById(R.id.app_name);
            artist = (TextView) itemView.findViewById(R.id.app_artist);
            price = (TextView) itemView.findViewById(R.id.app_price);
            icon = (ImageView) itemView.findViewById(R.id.app_icon);
            itemView.setOnClickListener(this);
            c = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            final Intent intent;
            Log.d("Debugtext","Card with position " + getAdapterPosition() + " was touched.");
            intent = new Intent(c, AppDetailActivity.class);
            intent.putExtra("app",apps.get(getAdapterPosition()));
            c.startActivity(intent);
        }
    }


}
