package com.hugo.imaginamosapp;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import Model.App;

/**
 * Created by hm94__000 on 04-Feb-16.
 */
public class AppDetailActivity extends AppCompatActivity {

    private boolean isTablet;
    private App app;
    public static Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        //CHECK WHETHER THE DEVICE IS A TABLET OR A PHONE
        isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet()) { //it's a tablet
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else { //it's a phone, not a tablet
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_app_detail);
        Intent i = getIntent();
        app= i.getParcelableExtra("app");
        setTitle("App Details");
        ImageView icon = (ImageView) findViewById(R.id.app_detail_icon);
        Picasso.with(context).load(app.getUrlImLarge()).into(icon);

        TextView name = (TextView) findViewById(R.id.app_detail_name);
        name.setText(app.getName());

        TextView artist = (TextView) findViewById(R.id.app_detail_artist);
        artist.setText(app.getArtist());

        TextView price = (TextView) findViewById(R.id.app_detail_price);
        String p = "";
        if(app.getPrice()==0.0) p="Free";
        else p= "$"+app.getPrice()+" "+app.getCurrency();
        price.setText(p);

        TextView rights = (TextView) findViewById(R.id.app_detail_rights);
        rights.setText(app.getRights());

        TextView type = (TextView) findViewById(R.id.app_detail_type);
        String t = app.getType()+" ("+app.getCategory()+")";
        type.setText(t);

        TextView releaseDate = (TextView) findViewById(R.id.app_detail_releasedate);
        DateFormat df = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        String rd = "Released on "+df.format(app.getReleaseDate());
        releaseDate.setText(rd);

        TextView summary = (TextView) findViewById(R.id.app_detail_summary);
        summary.setText(app.getSummary());

    }


    public boolean isTablet() {
        return isTablet;
    }
}
