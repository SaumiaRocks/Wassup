package com.example.designstudioiitr.wassup;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;



public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int GALLERY_PICK = 1;


    CircleImageView civProfilePicture;
    TextView tvProfileName, tvProfileStatus;
    Button btnUpdateStatus, btnChangeName;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
    String userId;
    FloatingActionButton fabChangeProfilePicture;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        civProfilePicture = findViewById(R.id.civProfilePicture);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileStatus = findViewById(R.id.tvProfileStatus);
        btnChangeName = findViewById(R.id.btnChangeName);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        fabChangeProfilePicture = findViewById(R.id.fabChangeProfilePicture);

        //initialising firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        //reference of current user database child
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_picture").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                tvProfileName.setText(name);
                tvProfileStatus.setText(status);

                Picasso.get().load(image).into(civProfilePicture);

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
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();

                        Intent intent = new Intent(ProfileActivity.this, ChangeNameActivity.class);
                        intent.putExtra("currentName", name);
                        startActivity(intent);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
                    }
                });
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

        fabChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference databaseProfilePic = databaseReference.child("image");

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, GALLERY_PICK);



            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();

            final DatabaseReference profilePicDatabase = databaseReference.child("image");

            StorageReference profilePicRef = mStorageRef.child(imageUri.getLastPathSegment());
            profilePicRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                        profilePicDatabase.setValue(task.getResult().getDownloadUrl().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()) {
                                  Toast.makeText(ProfileActivity.this, "Image Uploaded successfully!", Toast.LENGTH_SHORT);
                              }
                            }
                        });

                    }
                }


            });


//            Toast.makeText(this, "onActivityResult", Toast.LENGTH_SHORT).show();

            //enable image cropper
     /*       CropImage.activity(imageUri)
                    .start(ProfileActivity.this);
*/

        }
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