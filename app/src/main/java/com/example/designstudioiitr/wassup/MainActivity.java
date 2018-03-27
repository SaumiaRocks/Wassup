package com.example.designstudioiitr.wassup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.app_bar_main_activity);

        //setting up tool bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Wassup");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        //moving back to the register activity if not signed in
        if(currentUser == null) {
            displayWelcomneScreen();
        }
    }

    private void displayWelcomneScreen() {

        //back to start
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
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
            displayWelcomneScreen();
        }

        return true;
    }
}
