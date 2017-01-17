package com.example.cristianr.tiendaapps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.example.cristianr.tiendaapps.models.Application;
import com.example.cristianr.tiendaapps.widgets.SquareImageView;
import com.squareup.picasso.Picasso;

public class ApplicationDetailActivity extends AppCompatActivity {

    private Application application;

    private SquareImageView image;
    private TextView nameTextView;
    private TextView developerTextView;
    private TextView priceTextView;
    private TextView summaryTextView;
    private TextView urlTextView;

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
        // Temporally load image with Internet
        Picasso.with(this).load(application.getBigImageUrl())
                .error(R.drawable.square_grey)
                .placeholder(R.drawable.square_grey)
                .into(image);

        setApplicationValues();
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
}
