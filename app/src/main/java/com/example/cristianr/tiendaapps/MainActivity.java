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

import com.example.cristianr.tiendaapps.fragments.ApplicationsFragment;
import com.example.cristianr.tiendaapps.fragments.CategoriesFragment;
import com.example.cristianr.tiendaapps.helpers.WebHelper;
import com.example.cristianr.tiendaapps.models.Application;
import com.example.cristianr.tiendaapps.models.Category;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private List<Application> applications;
    private List<Application> categoryApplications;
    private List<Category> categories;
    private String category;

    private SwipeRefreshLayout swipeRefreshLayout;

    // Fragment
    private CategoriesFragment categoriesFragment;
    private ApplicationsFragment applicationsFragment;


    private static AsyncHttpClient client;
    public boolean isTablet;
    public boolean isSelected;

    public static final String ALL_CATEGORY = "All";
    public static final String APPLICATION_KEY = "application";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // First know the kind of device
        isTablet = getResources().getBoolean(R.bool.isTablet);
        isSelected = false;
        // Set initial category
        category = ALL_CATEGORY;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categories = new ArrayList<>();
        applications = new ArrayList<>();
        categoryApplications = new ArrayList<>();

        updateInfo();

        // Create fragments
        categoriesFragment = new CategoriesFragment();
        categoriesFragment.setCategories(categories);

        applicationsFragment = new ApplicationsFragment();
        applicationsFragment.setApplications(categoryApplications);

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
        if(!isTablet)
            bar.setTitle("Categories");
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!isTablet && isSelected){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment_container, categoriesFragment);
            fragmentTransaction.commit();
            isSelected = false;
            // Update title
            getSupportActionBar().setTitle("Categories");
        }
        else{
            super.onBackPressed();
        }
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
                applications.clear();
                for(int i=0; i<jsonApps.length(); i++){
                    try{
                        JSONObject json = jsonApps.getJSONObject(i);
                        app = Application.parseFromJSON(json);
                        app.setCategory(Category.parseFromJSON(json.getJSONObject(WebHelper.CATEGRY_OBJECT_KEY)));
                        applications.add(app);
                    }
                    catch (JSONException ex){
                        ex.printStackTrace();
                        Log.e("JSON PARSE", "Couldn't parse App");
                    }
                }
                // Set an array of categories
                updateCategoriesList();
                // Update applications
                selectApplications();

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
        all.setName(ALL_CATEGORY);
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
        applicationsFragment.notifyData();
    }

    public void selectApplications(){
        categoryApplications.clear();
        for(Application app : applications){
            if(category == ALL_CATEGORY || app.getCategoryName().equals(category)){
                categoryApplications.add(app);
            }
        }
    }

    public void selectCategory(String categoryName){
        isSelected = true;
        category = categoryName;
        selectApplications();

        // Replace fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, applicationsFragment);
        fragmentTransaction.commit();

        // Update Title
        if(!isTablet)
            getSupportActionBar().setTitle(category);

        notifyDataChanged();
    }

}
