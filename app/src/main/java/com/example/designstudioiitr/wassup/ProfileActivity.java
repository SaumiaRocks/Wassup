package com.example.designstudioiitr.wassup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    CircleImageView civProfilePicture;
    TextView tvProfileName, tvProfileStatus;
    Button btnUpdateStatus, btnChangeName;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        civProfilePicture = findViewById(R.id.civProfilePicture);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileStatus = findViewById(R.id.tvProfileStatus);
        btnChangeName = findViewById(R.id.btnChangeName);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        //initialising firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        //reference of current user database child
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                tvProfileName.setText(name);
                tvProfileStatus.setText(status);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
            }
        });

        //moving back to the home activity if user doesn't exist
        if (currentUser == null) {
            displayWelcomeScreen();
        }


        //setting change name button
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //setting update status button
        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.child("status").getValue().toString();

                        Intent intent = new Intent(ProfileActivity.this, StatusActivity.class);
                        intent.putExtra("currentStatus", status);
                        startActivity(intent);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);

                    }


                });
            }
        });

    }


    private void displayWelcomeScreen() {

        //back to start
        Log.e(TAG, "displaying welcome screen from main activity");
        Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}