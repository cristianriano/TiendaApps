package com.example.cristianr.tiendaapps;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 2000;

    private ImageView imageViewLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set portrait orientation for Splash
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Also hide the status bar
        View viewDecor = getWindow().getDecorView();
        viewDecor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        // Don't show ActionBar (wasn't necessary due to Activity Style)
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();

        setContentView(R.layout.activity_splash);
        imageViewLogo = (ImageView) findViewById(R.id.splash_image);

        new IntentLauncher().start();

    }

    private class IntentLauncher extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(SPLASH_SCREEN_DELAY/2);
                animLogo();
                Thread.sleep(SPLASH_SCREEN_DELAY/2);
                // Start next activity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                // Finish activity so the user can't go back
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public  void animLogo() {
        runOnUiThread(new Runnable() {      //go count - show interface
            @Override
            public void run() {
                imageViewLogo.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_in);
                animation.reset();
                imageViewLogo.startAnimation(animation);
            }
        });
    }
}
