package com.example.cristianr.tiendaapps.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cristianr.tiendaapps.R;
import com.example.cristianr.tiendaapps.models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cristianr on 14/01/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Category> categories;
    private Context context;

    public CategoriesAdapter(Context context, List<Category> categories){
        this.context = context;
        this.categories = categories;
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

        holder.categoryNameTextView.setText(Html.fromHtml(category.getName()));
        if(category.getUrl() != null){
            String htmlLink = String.format("<a href='%s'>Link</a>", category.getUrl().toString());
            holder.categoryUrlTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
            holder.categoryUrlTextView.setText(Html.fromHtml(htmlLink));
        }
        else{
            holder.categoryUrlTextView.setText("");
        }
    }

    // Get size
    @Override
    public int getItemCount() {
        return (null != categories ? categories.size() : 0);
    }


}
