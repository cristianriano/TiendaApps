package com.example.cristianr.tiendaapps.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cristianr.tiendaapps.R;
import com.example.cristianr.tiendaapps.adapters.CategoriesAdapter;
import com.example.cristianr.tiendaapps.models.Category;

import java.util.List;

public class CategoriesFragment extends Fragment {

    private List<Category> categories;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CategoriesAdapter adapter;

    public CategoriesFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        // Get elements
        recyclerView = (RecyclerView) view.findViewById(R.id.categories_list);
        recyclerView.setHasFixedSize(false);
        // Use linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        // Set adapter with listener
        adapter = new CategoriesAdapter(categories, new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Category category) {
                Toast.makeText(getActivity(), category.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void notifyData(){
        adapter.notifyDataSetChanged();
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
