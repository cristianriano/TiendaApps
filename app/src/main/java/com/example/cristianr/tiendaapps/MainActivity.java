package com.example.cristianr.tiendaapps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import Helpers.WebHelper;
import Models.Application;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Application> apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(WebHelper.checkInternet(this)){
            apps = WebHelper.getApplications();
        }
    }



}
