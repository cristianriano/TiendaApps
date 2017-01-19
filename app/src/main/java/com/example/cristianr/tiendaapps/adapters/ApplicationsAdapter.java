package com.example.cristianr.tiendaapps.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cristianr.tiendaapps.R;
import com.example.cristianr.tiendaapps.helpers.CacheHelper;
import com.example.cristianr.tiendaapps.helpers.WebHelper;
import com.example.cristianr.tiendaapps.models.Application;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by cristianr on 14/01/2017.
 */

public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.AppViewHolder> {

    private final List<Application> applications;
    private final OnItemClickListener listener;
    private Context context;

    public ApplicationsAdapter(Context context, List<Application> applications, OnItemClickListener listener){
        this.applications = applications;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ApplicationsAdapter.AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.application_template, parent, false);
        AppViewHolder viewHolder = new AppViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ApplicationsAdapter.AppViewHolder holder, int position) {
        final Application application = applications.get(position);

        // If the file doesn't exists locally (downloaded previously) then use Picasso
        if(!CacheHelper.checkIfFileExists(application.getImageUrl()) && WebHelper.checkInternet(context)){
            // Load Image
            Picasso.with(context).load(application.getImageUrl())
                    .error(R.drawable.circle_grey)
                    .placeholder(R.drawable.circle_grey)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) holder.imageView.getDrawable()).getBitmap();
                            CacheHelper.saveImage(bitmap, application.getImageUrl());
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        // If file exists or there is no Internet connection, then read it from cache
        else{
            File file = CacheHelper.getFile(application.getImageUrl());
            if(file != null){
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                holder.imageView.setImageBitmap(bitmap);
            }
        }

        holder.bind(application, listener);
    }

    @Override
    public int getItemCount() {
        return (applications == null) ? 0 : applications.size();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder{

        protected ImageView imageView;
        protected TextView appNameTextView;
        protected TextView appCategoryTextView;
        protected TextView appDeveloperTextView;
        protected TextView appSummaryTextView;

        public AppViewHolder(View v){
            super(v);
            imageView = (ImageView) v.findViewById(R.id.application_image);
            appNameTextView = (TextView) v.findViewById(R.id.application_name);
            appCategoryTextView = (TextView) v.findViewById(R.id.application_category);
            appDeveloperTextView = (TextView) v.findViewById(R.id.application_developer);
            appSummaryTextView = (TextView) v.findViewById(R.id.application_summary);
        }

        public void bind(final Application application, final OnItemClickListener listener) {
            appNameTextView.setText(application.getName());
            appCategoryTextView.setText(application.getCategoryName());
            String devlopedBy = String.format("By: %s", application.getDeveloperName());
            appDeveloperTextView.setText(devlopedBy);
            // Slice summary to display first 100 characters
            String summary = application.getSummary().substring(0,100);
            summary = summary + "...";
            appSummaryTextView.setText(summary);

            // Set listener
            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    listener.onItemClick(application);
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Application application);
    }
}
