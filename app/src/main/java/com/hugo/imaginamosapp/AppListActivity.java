package com.hugo.imaginamosapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Model.App;

public class AppListActivity extends AppCompatActivity {

    private ArrayList<App> apps;
    private int columns;


    private String root = Environment.getExternalStorageDirectory().toString();

    public static final String file_url = "https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json";
    public static final String file_name = "/imaginamos/apps.json";
    private boolean isTablet;
    private static String KEY_FIRST_RUN = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RecyclerViewAdapter rvadapter;

    public static Context context;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        //CHECK WHETHER THE DEVICE IS A TABLET OR A PHONE
        isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet()) { //it's a tablet
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            columns = 2;
        } else { //it's a phone, not a tablet
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            columns = 1;
        }

        setContentView(R.layout.activity_app_list);

        //SwipeContainer SETUP
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                getApps();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //ArrayList and RecyclerView initialization
        apps = new ArrayList<App>();

        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.ItemAnimator anim = rv.getItemAnimator();
        anim.setChangeDuration(300);
        rv.setHasFixedSize(true);
        GridLayoutManager gridlm = new GridLayoutManager(getApplicationContext(),columns);
        rv.setLayoutManager(gridlm);
        rvadapter = new RecyclerViewAdapter(apps, context);
        rv.setAdapter(rvadapter);


        //Checks if it is app's first time use
        sharedPreferences = getPreferences(MODE_PRIVATE);

        if (!sharedPreferences.contains("KEY_FIRST_RUN")) {
            KEY_FIRST_RUN = "first";
            //Makes necessary directories
            Log.d("Debugtext","First time run.");

            //Creates the necessary folder for the app
            File myDir = new File(root + "/imaginamos");
            myDir.mkdirs();
        }
        //Updates the app list
        getApps();
        editor = sharedPreferences.edit();
        editor.putString("KEY_FIRST_RUN", KEY_FIRST_RUN);
        editor.commit();

    }

    public void getApps(){
        //Initially, there must be a JSON on the device's external data
        //Log.d("Debugtext","Actualizando lista...");
        Toast.makeText(AppListActivity.this, "Actualizando lista...", Toast.LENGTH_LONG).show();
        String toastText = "";
        if(checkNetwork())
        {
            //Download JSON
            //Log.d("Debugtext","Hay internet.");
            new GetJSONAsync().execute();
        }
        else {
            //Log.d("Debugtext","No hay internet.");
            toastText = "No hay conexión.";
            File f = new File(root+file_name);
            if(f.exists()){
                //Log.d("Debug","Existe el archivo.");
                parseJson(readFileApps());
                toastText += "\nSe cargó la lista del archivo offline.";
                Toast.makeText(AppListActivity.this, toastText, Toast.LENGTH_SHORT).show();
            }
            else{
                //Log.d("Debugtext","No hay internet, no hay archivo.");
                toastText += "\nNo se puede descargar el archivo por primera vez";
                Toast.makeText(AppListActivity.this, toastText, Toast.LENGTH_SHORT).show();
            }
        }

    }

    //Checks whether the device has network connection
    public boolean checkNetwork()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Gets the boolean resource from the values folder
    public boolean isTablet() {
        return isTablet;
    }

    //Parses a JSONObject created from a given String
    public void parseJson(String content){
        rvadapter.clear();
        JSONObject j;
        try{
            //Log.d("Debugtext","Parsing JSON.");
            j = new JSONObject(content);

            //parse json
            JSONArray entries = j.getJSONObject("feed").getJSONArray("entry");
            //Log.d("Debugtext","entries number= "+entries.length());
            for(int i=0;i<entries.length();i++){
                //the Entry
                JSONObject entry = entries.getJSONObject(i);
                ////////////////////////////////////////////////////
                //App name
                JSONObject imName = entry.getJSONObject("im:name");
                String name = imName.getString("label"); //im:name/label
                //////////////////////////
                //App image URLs
                JSONArray images = entry.getJSONArray("im:image");
                String urlImSmall = images.getJSONObject(0).getString("label"); //im:image/label[0]
                String urlImMed = images.getJSONObject(1).getString("label"); //im:image/label[1]
                String urlImLarge = images.getJSONObject(2).getString("label"); //im:image/label[2]
                //////////////////////////
                //App summary
                JSONObject sum = entry.getJSONObject("summary");
                String summary = sum.getString("label"); //summary/label
                //////////////////////////
                //App price and currency
                JSONObject imPrice = entry.getJSONObject("im:price").getJSONObject("attributes");
                double price = imPrice.getDouble("amount"); //im:price/attributes/amount
                String currency = imPrice.getString("currency"); //im:price/attributes/currency
                //////////////////////////
                //App contentType
                JSONObject imContentType = entry.getJSONObject("im:contentType");
                String type = imContentType.getJSONObject("attributes").getString("label"); //im:contentType/attributes/label
                //////////////////////////
                //App rights
                JSONObject right = entry.getJSONObject("rights");
                String rights = right.getString("label"); //rights/label
                //////////////////////////
                //App title
                JSONObject titl = entry.getJSONObject("title");
                String title = titl.getString("label"); //title/label
                //////////////////////////
                //App link
                JSONObject lin = entry.getJSONObject("link").getJSONObject("attributes");
                String link = lin.getString("href"); //link/attributes/href
                //////////////////////////
                //App ID and attributes
                JSONObject id = entry.getJSONObject("id");
                String idLabel = id.getString("label"); //id/label
                String idNumber = id.getJSONObject("attributes").getString("im:id"); //id/attributes/im:id
                String bundleId = id.getJSONObject("attributes").getString("im:bundleId"); //id/attributes/im:bundleId
                //////////////////////////
                //App artist and artist link
                JSONObject art = entry.getJSONObject("im:artist");
                String artist = art.getString("label"); //im:artist/label
                String artistLink= art.getJSONObject("attributes").getString("href"); //im:artist/attributes/href
                //////////////////////////
                //App category
                JSONObject categ = entry.getJSONObject("category").getJSONObject("attributes");
                String category = categ.getString("label"); //category/attributes/label
                String categoryId = categ.getString("im:id"); //category/attributes/im:id
                String scheme = categ.getString("scheme"); //category/attributes/scheme
                //////////////////////////
                //App release date
                JSONObject imdate = entry.getJSONObject("im:releaseDate").getJSONObject("attributes");
                String dateString  = imdate.getString("label");
                DateFormat df = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                Date releaseDate = null; //im:releaseDate/attributes/label
                try {
                    releaseDate = df.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final App newApp = new App(name,urlImSmall,urlImMed,urlImLarge,summary,price,currency,type,rights,title,link,idLabel,idNumber,bundleId,artist,artistLink,
                        category,categoryId,scheme,releaseDate);
                apps.add(newApp);
            }

            //Updates the adapter
            rvadapter.notifyDataSetChanged();
            //Stops the refreshing
            swipeContainer.setRefreshing(false);
            //Toast.makeText(context,"Lista actualizada.",Toast.LENGTH_SHORT).show();
            //Log.d("Debugtext","se notifico al adapter, numelementos= "+rvadapter.getItemCount());
        }
        catch(Exception e){
            Log.e("ImaginamosApp Error:", e.getMessage(), e);
            Toast.makeText(context,"Problema al leer el archivo descargado.",Toast.LENGTH_SHORT).show();
        }
    }

    public String readFileApps(){

        //Read a file and write it onto a String, to later build a JSONObject
        String content="";

        File f = new File(root+file_name);
        if(f.exists()) {
            try {

                InputStream is = new FileInputStream(f);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                boolean done = false;

                while (!done) {
                    String line = br.readLine();
                    done = (line == null);
                    if (line != null) {
                        sb.append(line);
                    }
                }
                br.close();
                is.close();
                content = sb.toString();
            } catch (FileNotFoundException e) {
                Log.e("ImaginamosApp Error:", e.getMessage(), e);
                Toast.makeText(context,"Problema al leer el archivo descargado.",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("ImaginamosApp Error:", e.getMessage(), e);
                Toast.makeText(context,"Problema al leer el archivo descargado.",Toast.LENGTH_SHORT).show();
            }
        }
        return content;
    }



    public class GetJSONAsync extends AsyncTask<String, String, String> {

        /**
         * Downloading JSON file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                //Log.d("Debugtext","Downloading");
                URL url = new URL(file_url);
                URLConnection conection = url.openConnection();
                conection.connect();

                //InputStream to read file
                InputStream input = new BufferedInputStream(url.openStream(), 1024);

                // Output stream to write file
                File f = new File(root + file_name);
                OutputStream output = new FileOutputStream(f);
                byte data[] = new byte[1024];
                while ((count = input.read(data)) != -1) {
                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                //System.out.println("does file exist? "+ f.exists() + " length is " +f.length());


            } catch (Exception e) {
                Log.e("ImaginamosApp Error:", e.getMessage(), e);
                Toast.makeText(context,"Problema al descargar la lista.",Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute (String file_url){
            //Parse JSON
            parseJson(readFileApps());
            Toast.makeText(getApplicationContext(),"Lista cargada.",Toast.LENGTH_SHORT);
        }
    }
}