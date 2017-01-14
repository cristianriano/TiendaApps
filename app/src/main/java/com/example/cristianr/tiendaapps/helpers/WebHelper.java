package com.example.cristianr.tiendaapps.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.example.cristianr.tiendaapps.models.Application;
import com.example.cristianr.tiendaapps.models.Category;
import com.example.cristianr.tiendaapps.models.Developer;

/**
 * Created by cristianr on 13/01/2017.
 */

public class WebHelper {

    public final static String APPS_URL = "https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json";
    public final static String APPS_JSON_KEY = "entry";
    public final static String MAIN_JSON_KEY = "feed";
    public final static String CATEGRY_OBJECT_KEY = "category";
    public final static String DEVELOPER_OBJECT_KEY = "im:artist";

    private static AsyncHttpClient client;

    public static boolean checkInternet(Context context){
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if(i == null)
            return false;
        if(!i.isAvailable())
            return false;
        if(!i.isConnected())
            return false;
        return true;
    }

    public static ArrayList<Application> getApplications(){
        final ArrayList<Application> applications = new ArrayList<>();
        client = new AsyncHttpClient();
        client.get(APPS_URL, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                JSONArray jsonApps;
                try{
                    jsonApps = response.getJSONObject(MAIN_JSON_KEY).getJSONArray(APPS_JSON_KEY);
                }
                catch (JSONException ex){
                    ex.printStackTrace();
                    Log.e("JSON PARSE", "Couldn't parse JSONArray for apps");
                    return;
                }
                Application app;
                for(int i=0; i<jsonApps.length(); i++){
                    try{
                        JSONObject json = jsonApps.getJSONObject(i);
                        app = Application.parseFromJSON(json);
                        app.setCategory(Category.parseFromJSON(json.getJSONObject(CATEGRY_OBJECT_KEY)));
                        app.setDeveloper(Developer.parseFromJSON(json.getJSONObject(DEVELOPER_OBJECT_KEY)));
                        applications.add(app);
                    }
                    catch (JSONException ex){
                        ex.printStackTrace();
                        Log.e("JSON PARSE", "Couldn't parse App");
                    }
                }
            }
        });

        return applications;
    }
}
