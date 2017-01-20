package com.example.cristianr.tiendaapps;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.example.cristianr.tiendaapps.fragments.ApplicationsFragment;
import com.example.cristianr.tiendaapps.fragments.CategoriesFragment;
import com.example.cristianr.tiendaapps.helpers.WebHelper;
import com.example.cristianr.tiendaapps.models.Application;
import com.example.cristianr.tiendaapps.models.Category;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

    // Shared Preferences and GSON serializer
    private Gson gson;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public static final String ALL_CATEGORY = "All";
    public static final String APPLICATION_KEY = "application";
    public static final String APPLICATIONS_LIST_KEY = "applications";
    public static final String CATEGORIES_LIST_KEY = "categories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // First know the kind of device
        isTablet = getResources().getBoolean(R.bool.isTablet);
        // If tablet use Landscape
        if(isTablet)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        isSelected = false;
        // Set initial category
        category = ALL_CATEGORY;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWindowAnimation();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_categories);

        gson = new Gson();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        categories = new ArrayList<>();
        applications = new ArrayList<>();
        categoryApplications = new ArrayList<>();

        updateInfo();

        // Create fragments
        categoriesFragment = new CategoriesFragment();
        categoriesFragment.setCategories(categories);

        applicationsFragment = new ApplicationsFragment();
        applicationsFragment.setApplications(categoryApplications);

        initFragments();

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

    private void initFragments(){
        // Set main_fragment <-> CategoriesFragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, categoriesFragment);
        // If is a tablet load the ApplicationFragment in the secondary_fragment_container
        if(isTablet){
            fragmentTransaction.replace(R.id.secondary_fragment_container, applicationsFragment);
        }
        fragmentTransaction.commit();
    }

    public void updateInfo(){
        if(WebHelper.checkInternet(this)){
            updateApplications();
        }
        else{
            Toast.makeText(this, "You don't have internet access", Toast.LENGTH_LONG).show();
            readLists();
        }
        // Stop refreshing
        swipeRefreshLayout.setRefreshing(false);
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
                updateCategoryApplications();
                // Save lists
                saveLists();

                Log.d("NOTIFY", "Data updated");
                // Notify new data
                notifyDataChanged();
            }
        });
    }

    // Update the List of unique Categories
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

    // Notify both adapters Applications and Categories
    private void notifyDataChanged(){
        categoriesFragment.notifyData();
        applicationsFragment.notifyData();
    }

    // Update List of Applications by Global Category
    public void updateCategoryApplications(){
        categoryApplications.clear();
        for(Application app : applications){
            if(category.equals(ALL_CATEGORY) || app.getCategoryName().equals(category)){
                categoryApplications.add(app);
            }
        }
    }

    // Pick category, update List and fragment
    public void selectCategory(String categoryName){
        isSelected = true;
        category = categoryName;
        updateCategoryApplications();

        // Replace fragment if is not a table
        if(!isTablet){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment_container, applicationsFragment);
            fragmentTransaction.commit();
        }
        // Update title
        getSupportActionBar().setTitle(category);

        notifyDataChanged();
    }

    // Save applications and categories list in SharedPreferences
    private void saveLists(){
        // Stringify to JSON applications array and categories array with GSON
        String jsonApplications = gson.toJson(applications);
        String jsonCategories = gson.toJson(categories);

        // Save preferences
        editor.putString(APPLICATIONS_LIST_KEY, jsonApplications);
        editor.putString(CATEGORIES_LIST_KEY, jsonCategories);
        editor.commit();
    }

    private void readLists(){
        Type applicationsListType = new TypeToken<List<Application>>(){}.getType();
        Type categoriesListType = new TypeToken<List<Category>>(){}.getType();

        // Read from preferences
        String jsonApplications = sharedPreferences.getString(APPLICATIONS_LIST_KEY, null);
        String jsonCategories = sharedPreferences.getString(CATEGORIES_LIST_KEY, null);
        applications = gson.fromJson(jsonApplications, applicationsListType);
        categories = gson.fromJson(jsonCategories, categoriesListType);
    }

    // Setup Enter and Exit transitions
    private void setupWindowAnimation(){
        overridePendingTransition(0, 0);
    }

}
