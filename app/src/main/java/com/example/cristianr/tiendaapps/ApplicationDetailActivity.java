package com.example.cristianr.tiendaapps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.cristianr.tiendaapps.models.Application;
import com.example.cristianr.tiendaapps.widgets.SquareImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ApplicationDetailActivity extends AppCompatActivity {

    private Application application;

    private SquareImageView image;
    private TextView nameTextView;
    private TextView developerTextView;
    private TextView priceTextView;
    private TextView summaryTextView;
    private TextView urlTextView;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    public static final String FREE_LABEL = "FREE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);

        image = (SquareImageView) findViewById(R.id.application_detail_image);
        nameTextView = (TextView) findViewById(R.id.application_detail_name);
        developerTextView = (TextView) findViewById(R.id.application_detail_developer);
        priceTextView = (TextView) findViewById(R.id.application_detail_price);
        summaryTextView = (TextView) findViewById(R.id.application_detail_summary);
        urlTextView = (TextView) findViewById(R.id.application_detail_url);

        // Set values and get application
        Intent intent = getIntent();
        application = (Application) intent.getSerializableExtra(MainActivity.APPLICATION_KEY);

        // Toolbar settings
        setSupportActionBar((Toolbar) findViewById(R.id.application_detail_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.application_detail_collapsing_bar);
        collapsingToolbarLayout.setTitle(application.getName());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        // Temporally load image with Internet
        Picasso.with(this).load(application.getBigImageUrl())
                .error(R.drawable.square_grey)
                .placeholder(R.drawable.square_grey)
                .into(image, new Callback() {
                    // When image is load execute a callback to get colors palette
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                        Palette .from(bitmap).generate(new Palette.PaletteAsyncListener() {
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

        setApplicationValues();
    }

    private void applyPalette(Palette palette){
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
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
