package com.hugo.imaginamosapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(SplashActivity.this, AppListActivity.class);
        try {
            //Deliberately waits  ms in order to show the icon more
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //starts the AppListActivity
        startActivity(intent);
        finish();
    }
}
