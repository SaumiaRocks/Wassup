package com.example.designstudioiitr.wassup;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    android.support.v7.widget.Toolbar toolbar;
    ViewPager viewPager;
    MainPageAdapter mainPageAdapter;
    TabLayout mainPageTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.app_bar_main_activity);
        viewPager = findViewById(R.id.vpMainPage);
        mainPageAdapter = new MainPageAdapter(getSupportFragmentManager());
        mainPageTabLayout = findViewById(R.id.tabs_main_page);

        viewPager.setAdapter(mainPageAdapter);

        //setting up tool bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Wassup");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        //moving back to the home activity if not signed in
        if(currentUser == null) {
            displayWelcomeScreen();
        }

        Log.e(TAG, "onCreate: uid of current user : " + currentUser.getUid().toString());
    }

    private void displayWelcomeScreen() {

        //back to start
        Log.e(TAG, "displaying welcome screen from main activity");
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    //setting up top menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.logOut) {
            FirebaseAuth.getInstance().signOut();
            displayWelcomeScreen();
        }

        return true;
    }
}
