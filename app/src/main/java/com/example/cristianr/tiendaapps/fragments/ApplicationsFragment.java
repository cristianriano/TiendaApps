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
            @Override
            public void onItemClick(Application application) {
                Intent intent = new Intent(getActivity(), ApplicationDetailActivity.class);
                intent.putExtra(MainActivity.APPLICATION_KEY, application);
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
