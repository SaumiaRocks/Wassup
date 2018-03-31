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

import java.text.DateFormat;
import java.util.Date;

public class SingleUserActivity extends AppCompatActivity {

    private static final String CANCEL_FRIEND_REQUEST = "Cancel Friend Request";
    private static final String SEND_FRIEND_REQUEST = "Send Friend Request";
    private static final String ACCEPT_FRIEND_REQUEST = "Accept Friend Request";
    private static final String UNFRIEND = "Unfriend";
    private static final String REQUEST_SENT = "request_sent";
    private static final String NOT_FRIENDS = "not_friends";
    private static final String FRIENDS = "friends";
    private static final String REQUEST_RECEIVED = "request_received";

    ImageView ivSingleUserProfile;
    Button btnSendFriendRequest, btnDeleteRequest;
    TextView tvSingleUserName, tvSingleUserStatus, tvNumFriends, tvNumMutualFriends;
    String userId, currentUserId;
    DatabaseReference databaseReference, friendRequestDatabase, friendDatabase;
    ProgressDialog progressDialog;
    String currentState = NOT_FRIENDS;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        friendRequestDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userId)) {
                    String requestType = dataSnapshot.child(userId).getValue().toString();

                    if(requestType.equals(REQUEST_SENT)) {
                        currentState = REQUEST_SENT;
                        btnSendFriendRequest.setText(CANCEL_FRIEND_REQUEST);
                    }
                    else if(requestType.equals(REQUEST_RECEIVED)) {
                        currentState = REQUEST_RECEIVED;
                        btnSendFriendRequest.setText(ACCEPT_FRIEND_REQUEST);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

                if(currentState.equals(NOT_FRIENDS)) {

                    //button text changed to Send Friend Request

                    btnSendFriendRequest.setEnabled(false);
                    friendRequestDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(userId).setValue(REQUEST_SENT).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                friendRequestDatabase.child(userId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(REQUEST_RECEIVED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {

                                            Toast.makeText(SingleUserActivity.this, "Friend Request sent", Toast.LENGTH_SHORT).show();
                                            currentState=REQUEST_SENT;
                                            btnSendFriendRequest.setText(CANCEL_FRIEND_REQUEST);
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

                else if(currentState.equals(REQUEST_SENT)) {

                    //button text changed to Cancel Friend Request

                    btnSendFriendRequest.setEnabled(false);
                    friendRequestDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                friendRequestDatabase.child(userId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {

                                            Toast.makeText(SingleUserActivity.this, "Friend request deleted successfully", Toast.LENGTH_SHORT).show();
                                            currentState=NOT_FRIENDS;
                                            btnSendFriendRequest.setText(SEND_FRIEND_REQUEST    );
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

                else if(currentState.equals(REQUEST_RECEIVED)) {

                    //button text changed to Accept Friend Request

                    btnSendFriendRequest.setEnabled(false);
                    final String date = DateFormat.getDateTimeInstance().format(new Date());

                    friendDatabase.child(userId).child(currentUserId).setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()) {

                               friendDatabase.child(currentUserId).child(userId).setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()) {

                                           friendRequestDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                   .child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if(task.isSuccessful()) {
                                                       friendRequestDatabase.child(userId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                               .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<Void> task) {
                                                               if(task.isSuccessful()) {

                                                                   databaseReference.addValueEventListener(new ValueEventListener() {
                                                                       @Override
                                                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                                                           name = dataSnapshot.child("name").getValue().toString();

                                                                       }

                                                                       @Override
                                                                       public void onCancelled(DatabaseError databaseError) {
                                                                           name = "this person";

                                                                       }
                                                                   });

                                                                   Toast.makeText(SingleUserActivity.this, "You are now friends with " + name, Toast.LENGTH_SHORT).show();
                                                                   currentState=FRIENDS;
                                                                   btnSendFriendRequest.setText(UNFRIEND);
                                                                   btnSendFriendRequest.setEnabled(true);

                                                               }
                                                               else{
                                                                   Toast.makeText(SingleUserActivity.this, "Error Accepting Friend Request...", Toast.LENGTH_SHORT).show();
                                                                   btnSendFriendRequest.setEnabled(true);
                                                               }
                                                           }
                                                       });
                                                   }
                                                   else {
                                                       Toast.makeText(SingleUserActivity.this, "Error Accepting Friend Request...", Toast.LENGTH_SHORT).show();
                                                       btnSendFriendRequest.setEnabled(true);
                                                   }
                                               }
                                           });


                                       }
                                       else {
                                           Toast.makeText(SingleUserActivity.this, "Error Accepting Friend Request...", Toast.LENGTH_SHORT).show();
                                           btnSendFriendRequest.setEnabled(true);
                                       }

                                   }
                               });

                           }
                           else {
                               Toast.makeText(SingleUserActivity.this, "Error Accepting Friend Request...", Toast.LENGTH_SHORT).show();
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
