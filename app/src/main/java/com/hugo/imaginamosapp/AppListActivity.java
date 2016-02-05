package com.hugo.imaginamosapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

public class AppListActivity extends AppCompatActivity {

    private ArrayList<App> apps;
    private ArrayList<Card> cards;
    private CardArrayAdapter mCardArrayAdapter;
    private CardListView mListView;


    private String root = Environment.getExternalStorageDirectory().toString();

    private static String file_url = "https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json";
    private static String file_name = "/imaginamos/apps.json";
    private boolean isTablet;
    private static String KEY_FIRST_RUN = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet()) { //it's a tablet
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else { //it's a phone, not a tablet
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        //if (shouldAskPPermisson()) {
        //    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        //    requestPermissions(perms, 200);
        //}
        setContentView(R.layout.activity_app_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(R.color.colorPrimaryDark);

        //ArrayList and Adapters initialization
        apps = new ArrayList<App>();
        cards = new ArrayList<Card>();
        mListView = (CardListView) findViewById(R.id.carddemo_list_gplaycard);
        mCardArrayAdapter = new CardArrayAdapter(AppListActivity.this.getApplicationContext(), cards);


        if(mListView != null){
            mListView.setAdapter(mCardArrayAdapter);
        }


        //checks if it is app's first time use
        sharedPreferences = getPreferences(MODE_PRIVATE);

        if(savedInstanceState == null){
            Log.d("Debugtext","No es cambio de orientacion.");
            if (!sharedPreferences.contains("KEY_FIRST_RUN")) {
                KEY_FIRST_RUN = "something";
                //getjson
                Log.d("Debugtext","First time run.");
                File myDir = new File(root + "/imaginamos");
                myDir.mkdirs();
            }
            Toast.makeText(AppListActivity.this, "Actualizando lista...", Toast.LENGTH_SHORT).show();
            /////////
            //downloads json and parses it
            getApps();


            editor = sharedPreferences.edit();
            editor.putString("KEY_FIRST_RUN", KEY_FIRST_RUN);
            editor.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getApps(){
        //Initially, there must be a JSON on the device's external data (?)
        Log.d("Debugtext","Actualizando lista...");
        Toast.makeText(AppListActivity.this, "Actualizando lista...", Toast.LENGTH_LONG).show();
        String toastText = "";

        //Check if there is network access

        if(checkNetwork())
        {
            //Download JSON
            Log.d("Debugtext","Hay internet.");
            new GetJSONAsync().execute();
        }
        else {
            Log.d("Debugtext","No hay internet.");
            toastText = "No hay conexión.";
            File f = new File(root+file_name);
            if(f.exists()){
                Log.d("Debug","Existe el archivo.");
                parseJson(readFileApps());
                toastText = "Se cargó la lista del archivo offline.";
                Toast.makeText(AppListActivity.this, toastText, Toast.LENGTH_SHORT).show();
            }
            else{
                Log.d("Debugtext","No hay internet, no hay archivo.");
                Toast.makeText(AppListActivity.this, "No hay conectividad a internet\nNo se puede descargar el archivo por primera vez", Toast.LENGTH_SHORT).show();
            }
        }
        mCardArrayAdapter.notifyDataSetChanged();

    }

    public boolean checkNetwork()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isTablet() {
        return isTablet;
    }

    public void parseJson(String content){

        JSONObject j;
        try{
            Log.d("Debugtext","Parsing JSON.");
            j = new JSONObject(content);

            //parse json
            JSONArray entries = j.getJSONObject("feed").getJSONArray("entry");
            Log.d("Debugtext","entries number= "+entries.length());
            for(int i=0;i<entries.length();i++){
                //the Entry
                JSONObject entry = entries.getJSONObject(i);
                ////////////////////////////////////////////////////

                JSONObject imName = entry.getJSONObject("im:name");
                String name = imName.getString("label"); //im:name/label
                //////////////////////////
                JSONArray images = entry.getJSONArray("im:image");
                String urlImSmall = images.getJSONObject(0).getString("label"); //im:image/label[0]
                String urlImMed = images.getJSONObject(1).getString("label"); //im:image/label[1]
                String urlImLarge = images.getJSONObject(2).getString("label"); //im:image/label[2]
                //////////////////////////
                JSONObject sum = entry.getJSONObject("summary");
                String summary = sum.getString("label"); //summary/label
                //////////////////////////
                JSONObject imPrice = entry.getJSONObject("im:price").getJSONObject("attributes");
                double price = imPrice.getDouble("amount"); //im:price/attributes/amount
                String currency = imPrice.getString("currency"); //im:price/attributes/currency
                //////////////////////////
                JSONObject imContentType = entry.getJSONObject("im:contentType");
                String type = imContentType.getJSONObject("attributes").getString("label"); //im:contentType/attributes/label
                //////////////////////////
                JSONObject right = entry.getJSONObject("rights");
                String rights = right.getString("label"); //rights/label
                //////////////////////////
                JSONObject titl = entry.getJSONObject("title");
                String title = titl.getString("label"); //title/label
                //////////////////////////
                JSONObject lin = entry.getJSONObject("link").getJSONObject("attributes");
                String link = lin.getString("href"); //link/attributes/href
                //////////////////////////
                JSONObject id = entry.getJSONObject("id");
                String idLabel = id.getString("label"); //id/label
                String idNumber = id.getJSONObject("attributes").getString("im:id"); //id/attributes/im:id
                String bundleId = id.getJSONObject("attributes").getString("im:bundleId"); //id/attributes/im:bundleId
                //////////////////////////
                JSONObject art = entry.getJSONObject("im:artist");
                String artist = art.getString("label"); //im:artist/label
                String artistLink= art.getJSONObject("attributes").getString("href"); //im:artist/attributes/href
                //////////////////////////
                JSONObject categ = entry.getJSONObject("category").getJSONObject("attributes");
                String category = categ.getString("label"); //category/attributes/label
                String categoryId = categ.getString("im:id"); //category/attributes/im:id
                String scheme = categ.getString("scheme"); //category/attributes/scheme
                //////////////////////////
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


                CustomCard c = new CustomCard(getApplicationContext(), newApp);
                c.setOnClickListener(new Card.OnCardClickListener() {
                    @Override
                    public void onClick(Card card, View view) {
                        Intent i = new Intent(AppListActivity.this, AppDetailActivity.class);
                        //send selected application on intent
                        i.putExtra("myapp",newApp);
                    }
                });
                cards.add(c);
                //System.out.println("se agrego una card con la app "+c.getApp().getName());


            }
            Log.d("Debugtext","se notifico al adapter, numelementos= "+mCardArrayAdapter.getCount());
        }
        catch(Exception e){
            Log.e("Error: ", e.getMessage(), e);
        }
    }

    public String readFileApps(){
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

                //System.out.println("CONTENT= \n"+content);
                parseJson(content);

                //System.out.println(apps.size());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }


    /**
     * Created by hm94__000 on 04-Feb-16.
     */
    public class GetJSONAsync extends AsyncTask<String, String, String> {

        /**
         * Downloading file in background thread
         */
        //private String result = "";
        private String root = Environment.getExternalStorageDirectory().toString();

        //private ArrayList<App> apps = new ArrayList<App>();


        private final String file_url = "https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json";
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                Log.d("Debugtext","Downloading");
                URL url = new URL(file_url);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                File f = new File(root + file_name);
                OutputStream output = new FileOutputStream(f);
                byte data[] = new byte[10000];
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
                Log.e("Error: ", e.getMessage(), e);
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