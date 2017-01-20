package com.example.cristianr.tiendaapps.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cristianr.tiendaapps.ApplicationDetailActivity;
import com.example.cristianr.tiendaapps.MainActivity;
import com.example.cristianr.tiendaapps.R;
import com.example.cristianr.tiendaapps.adapters.ApplicationsAdapter;
import com.example.cristianr.tiendaapps.models.Application;

import java.util.List;

public class ApplicationsFragment extends Fragment {

    private List<Application> applications;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ApplicationsAdapter adapter;

    public static final String LEFT_KEY = "left";
    public static final String TOP_KEY = "top";
    public static final String WIDTH_KEY = "width";
    public static final String HEIGHT_KEY = "heigth";


    public ApplicationsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_applications, container, false);

        // Get elements
        recyclerView = (RecyclerView) view.findViewById(R.id.applications_list);
        recyclerView.setHasFixedSize(false);
        // Use GridLayoutManager if tablet
        if(((MainActivity) getActivity()).isTablet){
            layoutManager = new GridLayoutManager(getActivity(), 2);
        }
        // If not use LinearLayour
        else {
            layoutManager = new LinearLayoutManager(getActivity());
        }
        recyclerView.setLayoutManager(layoutManager);
        // Set adapter with listener
        adapter = new ApplicationsAdapter(getContext(), applications, new ApplicationsAdapter.OnItemClickListener() {
            // Receive the application object and the template
            @Override
            public void onItemClick(Application application, View v) {
                // Get the image to make an effect
                ImageView image = (ImageView) v.findViewById(R.id.application_image);
                // Get location and size
                int[] screenLocation = new int[2];
                image.getLocationOnScreen(screenLocation);

                Intent intent = new Intent(getActivity(), ApplicationDetailActivity.class);
                intent.putExtra(MainActivity.APPLICATION_KEY, application);
                intent.putExtra(LEFT_KEY, screenLocation[0]);
                intent.putExtra(TOP_KEY, screenLocation[1]);
                intent.putExtra(WIDTH_KEY, image.getWidth());
                intent.putExtra(HEIGHT_KEY, image.getHeight());
                startActivity(intent);

            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void notifyData(){
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}
