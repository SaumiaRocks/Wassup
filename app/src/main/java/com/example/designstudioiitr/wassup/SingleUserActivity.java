package com.example.designstudioiitr.wassup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingleUserActivity extends AppCompatActivity {

    ImageView ivSingleUserProfile;
    Button btnSendFriendRequest, btnDeleteRequest;
    TextView tvSingleUserName, tvSingleUserStatus, tvNumFriends, tvNumMutualFriends;
    String userId;
    DatabaseReference databaseReference, friendRequestDatatbase;
    ProgressDialog progressDialog;
    String currentState = "not_friends";

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
        btnSendFriendRequest = findViewById(R.id.btnAddFriend);
        btnDeleteRequest = findViewById(R.id.btnDeleteRequest);

        //setting up Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we load user credentials");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        friendRequestDatatbase = FirebaseDatabase.getInstance().getReference().child("FriendRequest");

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
        btnSendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentState.equals("not_friends")) {
                    btnSendFriendRequest.setEnabled(false);
                    friendRequestDatatbase.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(userId).setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                friendRequestDatatbase.child(userId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue("recieved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {

                                            Toast.makeText(SingleUserActivity.this, "Friend Request sent", Toast.LENGTH_SHORT).show();
                                            currentState="request_sent";
                                            btnSendFriendRequest.setText("Cancel Friend Request");
                                            btnSendFriendRequest.setEnabled(true);

                                        }
                                        else{
                                            Toast.makeText(SingleUserActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                                            btnSendFriendRequest.setEnabled(true);
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(SingleUserActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                                btnSendFriendRequest.setEnabled(true);
                            }
                        }
                    });
                }
                else if(currentState.equals("request_sent")) {
                    btnSendFriendRequest.setEnabled(false);
                    friendRequestDatatbase.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                friendRequestDatatbase.child(userId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {

                                            Toast.makeText(SingleUserActivity.this, "Friend request deleted successfully", Toast.LENGTH_SHORT).show();
                                            currentState="not_friends";
                                            btnSendFriendRequest.setText("Send Friend Request");
                                            btnSendFriendRequest.setEnabled(true);

                                        }
                                        else{
                                            Toast.makeText(SingleUserActivity.this, "Could not delete friend request. Please try again...", Toast.LENGTH_SHORT).show();
                                            btnSendFriendRequest.setEnabled(true);
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(SingleUserActivity.this, "Could not delete friend request. Please try again...", Toast.LENGTH_SHORT).show();
                                btnSendFriendRequest.setEnabled(true);
                            }
                        }
                    });
                }




            }
        });

        //deleting Friend Request
        btnDeleteRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
