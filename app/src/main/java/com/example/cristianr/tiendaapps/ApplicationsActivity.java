package com.example.cristianr.tiendaapps;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.cristianr.tiendaapps.adapters.ApplicationsAdapter;
import com.example.cristianr.tiendaapps.models.Application;
import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

public class ApplicationsActivity extends AppCompatActivity {

    private List<Application> applications;
    private String categorySelected = "";

    private SwipeRefreshLayout swipeRefreshLayout;

    private ApplicationsAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("DEBUG", "New application");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);

        // Get info from previous Intent
        Intent intent = getIntent();
        applications = (List<Application>) intent.getSerializableExtra(MainActivity.LIST_KEY);
        Bundle extras = intent.getExtras();
        if(extras != null)
            categorySelected = extras.getString(MainActivity.CATEGORY_KEY);

        // Get elements
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_applications);
        recyclerView = (RecyclerView) findViewById(R.id.applications_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // Set adapter with listener
        adapter = new ApplicationsAdapter(this, applications, new ApplicationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Application application) {
                Toast.makeText(ApplicationsActivity.this, application.getName(), Toast.LENGTH_SHORT);
                // TODO logic to display details
            }
        });

        recyclerView.setAdapter(adapter);

        // TODO swipe refresh logic
    }
}
