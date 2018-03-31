package com.example.designstudioiitr.wassup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingleUserActivity extends AppCompatActivity {

    ImageView ivSingleUserProfile;
    Button btnAddFriend;
    TextView tvSingleUserName, tvSingleUserStatus, tvNumFriends, tvNumMutualFriends;
    String userId;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        ivSingleUserProfile = findViewById(R.id.ivSingleUserImage);
        tvSingleUserStatus = findViewById(R.id.tvSingleUserStatus);
        tvSingleUserName = findViewById(R.id.tvSingleUserName);
        tvNumFriends = findViewById(R.id.tvNumFriends);
        tvNumMutualFriends = findViewById(R.id.tvNumMutualFriends);
        btnAddFriend = findViewById(R.id.btnAddFriend);

        //setting up Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we load user credentials");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                tvSingleUserName.setText(name);
                tvSingleUserStatus.setText(status);

                if(!image.equals("default")) {
                    Picasso.get().load(Uri.parse(image)).placeholder(R.mipmap.deafult_profile).into(ivSingleUserProfile);
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //sending Friend Request
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
