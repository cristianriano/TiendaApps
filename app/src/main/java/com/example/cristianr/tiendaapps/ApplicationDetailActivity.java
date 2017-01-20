package com.example.cristianr.tiendaapps;

import android.animation.TimeInterpolator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cristianr.tiendaapps.fragments.ApplicationsFragment;
import com.example.cristianr.tiendaapps.helpers.CacheHelper;
import com.example.cristianr.tiendaapps.helpers.WebHelper;
import com.example.cristianr.tiendaapps.models.Application;
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
    private CardView cardView;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout topLevelLayout;

    public static final String FREE_LABEL = "FREE";
    public boolean isTablet;

    // Animation variables
    int mLeftDelta;
    int mTopDelta;
    float mWidthScale;
    float mHeightScale;
    private static final int ANIM_DURATION = 500;
    private static final TimeInterpolator decelerator = new DecelerateInterpolator();

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
        cardView = (CardView) findViewById(R.id.application_detail_card);
        topLevelLayout = (CoordinatorLayout) findViewById(R.id.activity_application_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            topLevelLayout.setBackground(new ColorDrawable(Color.WHITE));
        }

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

        // Get extras
        Bundle bundle = intent.getExtras();
        final int thumbnailTop = bundle.getInt(ApplicationsFragment.TOP_KEY);
        final int thumbnailLeft = bundle.getInt(ApplicationsFragment.LEFT_KEY);
        final int thumbnailWidth = bundle.getInt(ApplicationsFragment.WIDTH_KEY);
        final int thumbnailHeight = bundle.getInt(ApplicationsFragment.HEIGHT_KEY);

        // Only run animation if we are coming from the parent activity
        if(savedInstanceState == null){
            ViewTreeObserver observer = image.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    image.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    image.getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) thumbnailWidth / image.getWidth();
                    mHeightScale = (float) thumbnailHeight / image.getHeight();

                    runEnterAnimation();

                    // Continue with rendering
                    return true;
                }
            });
        }

        setApplicationValues();
    }

    public void runEnterAnimation(){
        final long duration = (long) (ANIM_DURATION);

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        image.setPivotX(0);
        image.setPivotY(0);
        image.setScaleX(mWidthScale);
        image.setScaleY(mHeightScale);
        image.setTranslationX(mLeftDelta);
        image.setTranslationY(mTopDelta);

        cardView.setAlpha(0);


        image.animate().setDuration(duration)
                    .scaleX(1).scaleY(1)
                    .translationX(0).translationY(0)
                    .setInterpolator(decelerator);

        cardView.setTranslationY(-cardView.getHeight());
        cardView.animate().setDuration(duration)
                .translationY(0).alpha(1)
                .setInterpolator(decelerator);

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
