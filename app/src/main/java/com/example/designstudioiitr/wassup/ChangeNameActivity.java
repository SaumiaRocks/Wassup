package com.example.designstudioiitr.wassup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeNameActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    EditText etNewName;
    Button btnChangeNameOk, btnChangeNameCancel;
    String currentName;
    String userId;
    DatabaseReference databaseReference, nameDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        etNewName = findViewById(R.id.etNewName);
        btnChangeNameCancel = findViewById(R.id.btnChangeNameCancel);
        btnChangeNameOk = findViewById(R.id.btnChangeNameOk);

        //setting up firebase
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        nameDatabase = databaseReference.child("name");

        //setting current name
        Intent intent = getIntent();
        currentName = intent.getStringExtra("currentName");
        etNewName.setText(currentName, TextView.BufferType.SPANNABLE);

        //setting up tool bar
        toolbar = findViewById(R.id.change_name_activity_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Enter your name");

        btnChangeNameOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = etNewName.getText().toString();
                nameDatabase.setValue(newName);
                finish();
            }
        });

        btnChangeNameCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}
