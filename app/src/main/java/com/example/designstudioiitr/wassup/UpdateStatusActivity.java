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

public class UpdateStatusActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    EditText etNewStatus;
    Button btnOk, btnCancel;
    String currentStatus;
    String userId;
    DatabaseReference databaseReference, statusDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);

        etNewStatus = findViewById(R.id.etNewStatus);
        btnCancel = findViewById(R.id.btnCancel);
        btnOk = findViewById(R.id.btnOk);

        //setting up firebase
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        statusDatabase = databaseReference.child("status");

        //setting current status
        Intent intent = getIntent();
        currentStatus = intent.getStringExtra("currentStatus");
        etNewStatus.setText(currentStatus, TextView.BufferType.SPANNABLE);

        //setting up tool bar
        toolbar = findViewById(R.id.update_status_activity_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Status");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newStatus = etNewStatus.getText().toString();
                statusDatabase.setValue(newStatus);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

    }
}
