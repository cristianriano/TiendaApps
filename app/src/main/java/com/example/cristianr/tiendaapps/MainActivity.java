package com.example.cristianr.tiendaapps;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.cristianr.tiendaapps.fragments.CategoriesFragment;
import com.example.cristianr.tiendaapps.helpers.WebHelper;
import com.example.cristianr.tiendaapps.models.Application;
import com.example.cristianr.tiendaapps.models.Category;
import com.example.cristianr.tiendaapps.models.Developer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private List<Application> applications;
    private List<Category> categories;

    private SwipeRefreshLayout swipeRefreshLayout;

    // Fragment
    private CategoriesFragment categoriesFragment;

    private static AsyncHttpClient client;
    public boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // First know the kind of device
        isTablet = getResources().getBoolean(R.bool.isTablet);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categories = new ArrayList<>();
        applications = new ArrayList<>();

        updateInfo();

        // Create fragments
        categoriesFragment = new CategoriesFragment();
        categoriesFragment.setCategories(categories);

        // Set fragments
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, categoriesFragment);
        fragmentTransaction.commit();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_categories);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateInfo();
            }
        });
        int primaryDarkColor = getResources().getColor(R.color.primary_dark);
        swipeRefreshLayout.setColorSchemeColors(primaryDarkColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setTitle("Categories");
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // TODO manage back button
    }

    public void updateInfo(){
        if(WebHelper.checkInternet(this)){
            updateApplications();
        }
        else{
            Toast.makeText(this, "You don't have internet access", Toast.LENGTH_LONG).show();
            // TODO Implement cache recovering
        }
    }

    public void updateApplications(){
        client = new AsyncHttpClient();
        client.get(WebHelper.APPS_URL, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                JSONArray jsonApps;
                try{
                    jsonApps = response.getJSONObject(WebHelper.MAIN_JSON_KEY).getJSONArray(WebHelper.APPS_JSON_KEY);
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
                        app.setCategory(Category.parseFromJSON(json.getJSONObject(WebHelper.CATEGRY_OBJECT_KEY)));
                        app.setDeveloper(Developer.parseFromJSON(json.getJSONObject(WebHelper.DEVELOPER_OBJECT_KEY)));
                        applications.add(app);
                    }
                    catch (JSONException ex){
                        ex.printStackTrace();
                        Log.e("JSON PARSE", "Couldn't parse App");
                    }
                }
                // Set an array of categories
                updateCategoriesList();

                Log.d("NOTIFY", "Data updated");
                // Notify new data
                notifyDataChanged();
                // Stop refreshing
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateCategoriesList(){
        // First clear already existing
        categories.clear();
        // Create All category
        Category all = new Category();
        all.setName("All");
        categories.add(all);
        // Add new categories to array
        ArrayList<String> temp = new ArrayList<>();
        for(Application a : applications){
            if(!temp.contains(a.getCategory().getName())){
                categories.add(a.getCategory());
                temp.add(a.getCategory().getName());
            }

        }
    }

    private void notifyDataChanged(){
        categoriesFragment.notifyData();
    }

}
