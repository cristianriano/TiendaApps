package com.example.cristianr.tiendaapps.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cristianr.tiendaapps.R;
import com.example.cristianr.tiendaapps.models.Category;

import java.util.List;

/**
 * Created by cristianr on 14/01/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private final List<Category> categories;
    private final OnItemClickListener listener;

    public CategoriesAdapter(List<Category> categories, OnItemClickListener listener){
        this.categories = categories;
        this.listener = listener;
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView categoryNameTextView;
        protected TextView categoryUrlTextView;

        public ViewHolder(View v){
            super(v);
            this.categoryNameTextView = (TextView) v.findViewById(R.id.category_name);
            this.categoryUrlTextView = (TextView) v.findViewById(R.id.category_url);
        }

        public void bind(final Category category, final OnItemClickListener listener) {
            categoryNameTextView.setText(category.getName());
            if(category.getUrl() != null){
                String htmlLink = String.format("<a href='%s'>Link</a>", category.getUrl());
                categoryUrlTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
                categoryUrlTextView.setText(Html.fromHtml(htmlLink));
            }
            else{
                categoryUrlTextView.setText("");
            }
            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    listener.onItemClick(category);
                }
            });
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_template, null);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get selected position
        Category category = categories.get(position);

        // Set listener
        holder.bind(category, listener);
    }

    // Get size
    @Override
    public int getItemCount() {
        return (null != categories ? categories.size() : 0);
    }

    public interface OnItemClickListener{
        void onItemClick(Category category);
    }


}
