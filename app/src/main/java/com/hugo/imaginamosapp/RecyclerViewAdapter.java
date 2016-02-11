package com.hugo.imaginamosapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.App;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.AppVH>{


    ArrayList<App> apps;
    Context context;

    @Override
    //Creates a ViewHolder for each Card
    public AppVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new AppVH(v);
    }

    @Override
    //Loads the attributes from the App Object into the ViewHolder
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
    //Returns the size of the App array instead of the default 0
    public int getItemCount() {
        return apps.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    //Returns the App array
    RecyclerViewAdapter(ArrayList<App> applications, Context context){

        this.apps = applications;
        this.context = context;
    }

    // Clean all elements of the recycler
    public void clear() {
        apps.clear();
        notifyDataSetChanged();
    }

    //Inner Class representing each Card (ViewHolder)
    public class AppVH extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cv;
        TextView name;
        TextView artist;
        TextView price;
        ImageView icon;

        public AppVH(View itemView) {
            super(itemView);
            //Initializes the Views
            cv = (CardView) itemView.findViewById(R.id.cardview);
            name = (TextView) itemView.findViewById(R.id.app_name);
            artist = (TextView) itemView.findViewById(R.id.app_artist);
            price = (TextView) itemView.findViewById(R.id.app_price);
            icon = (ImageView) itemView.findViewById(R.id.app_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Intent intent;
            //Opens the AppDetailActivity showing the selected App Card
            //Log.d("Debugtext","Card with position " + getAdapterPosition() + " was touched.");
            intent = new Intent(context, AppDetailActivity.class);
            intent.putExtra("app",apps.get(getAdapterPosition()));

            //Adds the animation to the activity Bundle.
            //The context is casted in order to enable the animation
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, v, "appcard");
            ((Activity)context).startActivity(intent, options.toBundle());
        }
    }


}
