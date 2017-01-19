package com.example.cristianr.tiendaapps;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cristianr.tiendaapps.helpers.CacheHelper;
import com.example.cristianr.tiendaapps.helpers.WebHelper;
import com.example.cristianr.tiendaapps.models.Application;
import com.example.cristianr.tiendaapps.widgets.SquareImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ApplicationDetailActivity extends AppCompatActivity {

    private Application application;

    private ImageView image;
    private TextView nameTextView;
    private TextView developerTextView;
    private TextView priceTextView;
    private TextView summaryTextView;
    private TextView urlTextView;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    public static final String FREE_LABEL = "FREE";
    public boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isTablet = getResources().getBoolean(R.bool.isTablet);
        // If tablet use Landscape and use theme with Bar
        if(isTablet){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setTheme(R.style.AppTheme);
        }
        else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Set Normal theme when tablet

        setContentView(R.layout.activity_application_detail);

        image = (ImageView) findViewById(R.id.application_detail_image);
        nameTextView = (TextView) findViewById(R.id.application_detail_name);
        developerTextView = (TextView) findViewById(R.id.application_detail_developer);
        priceTextView = (TextView) findViewById(R.id.application_detail_price);
        summaryTextView = (TextView) findViewById(R.id.application_detail_summary);
        urlTextView = (TextView) findViewById(R.id.application_detail_url);

        // Set values and get application
        Intent intent = getIntent();
        application = (Application) intent.getSerializableExtra(MainActivity.APPLICATION_KEY);

        // Toolbar settings
        if(!isTablet){
            setSupportActionBar((Toolbar) findViewById(R.id.application_detail_toolbar));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.application_detail_collapsing_bar);
            collapsingToolbarLayout.setTitle(application.getName());
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        }
        else{
            ActionBar bar = getSupportActionBar();
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(application.getName());
        }

        // If file doesn't exists locally (downloaded previously) then use Picasso
        if(!CacheHelper.checkIfFileExists(application.getBigImageUrl()) && WebHelper.checkInternet(this)){
            Picasso.with(this).load(application.getBigImageUrl())
                    .error(R.drawable.square_grey)
                    .placeholder(R.drawable.square_grey)
                    .into(image, new Callback() {
                        // When image is load execute a callback to get colors palette
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                            CacheHelper.saveImage(bitmap, application.getBigImageUrl());
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    applyPalette(palette);
                                }
                            });
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        // If file exists or there is no Internet connection, then read it from cache
        else{
            // If the big image wasn't downloaded yet, try with the small one
            String imageUrl = (CacheHelper.checkIfFileExists(application.getBigImageUrl())) ? application.getBigImageUrl() : application.getImageUrl();
            File file = CacheHelper.getFile(imageUrl);
            if(file != null){
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                image.setImageBitmap(bitmap);
                // Also apply palette when loading
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        applyPalette(palette);
                    }
                });
            }
            // Set a grey square in case neither file exists
            else{
                image.setImageResource(R.drawable.square_grey);
            }
        }

        setApplicationValues();
    }

    private void applyPalette(Palette palette){
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        if(!isTablet){
            collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
            collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        }
        else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(palette.getMutedColor(primary)));
        }
        supportPostponeEnterTransition();
    }

    private void setApplicationValues(){
        nameTextView.setText(application.getName());
        String developedBy = String.format("By: %s", application.getDeveloperName());
        developerTextView.setText(developedBy);
        // Put FREE in case the price is cero (always)
        if(application.getPrice() == 0.0)
            priceTextView.setText(FREE_LABEL);
        else{
            String price = String.format("%d %s", application.getPrice(), application.getCurrency());
            priceTextView.setText(price);
        }
        summaryTextView.setText(application.getSummary());
        // URL
        String htmlLink = String.format("See more <a href='%s'>here</a>", application.getUrl());
        urlTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        urlTextView.setText(Html.fromHtml(htmlLink));
    }

    // Behavior when home up is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Because there is no option menu in the Bar it always terminate the event
        finish();
        return true;
    }
}
