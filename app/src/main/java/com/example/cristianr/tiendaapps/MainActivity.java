package com.example.cristianr.tiendaapps;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.cristianr.tiendaapps.adapters.CategoriesAdapter;
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

    private List<Application> apps;
    private List<Category> categories;

    private SwipeRefreshLayout swipeRefreshLayout;

    private CategoriesAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private static AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categories = new ArrayList<>();
        apps = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.categories_list);
        // Improve performance if changes in content don't change layout size
        recyclerView.setHasFixedSize(true);
        // Use linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // Set adapter
        adapter = new CategoriesAdapter(categories, new CategoriesAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(Category category) {
                Toast.makeText(MainActivity.this, category.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        updateInfo();

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
                        apps.add(app);
                    }
                    catch (JSONException ex){
                        ex.printStackTrace();
                        Log.e("JSON PARSE", "Couldn't parse App");
                    }
                }
                // Set an array of categories
                // First clear already existing
                categories.clear();
                Category all = new Category();
                all.setName("All");
                categories.add(all);
                // Add new categories to array
                ArrayList<String> temp = new ArrayList<>();
                for(Application a : apps){
                    if(!temp.contains(a.getCategory().getName())){
                        categories.add(a.getCategory());
                        temp.add(a.getCategory().getName());
                    }

                }

                Log.d("NOTIFY", "Data updated");
                // Notify new data
                adapter.notifyDataSetChanged();
                // Stop refreshing
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
