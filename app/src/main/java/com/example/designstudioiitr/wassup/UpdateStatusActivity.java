package com.example.designstudioiitr.wassup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UpdateStatusActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);


        //setting up tool bar
        toolbar = findViewById(R.id.update_status_activity_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Status");

    }
}