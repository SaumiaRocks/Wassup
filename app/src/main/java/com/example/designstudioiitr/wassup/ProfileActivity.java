package com.example.designstudioiitr.wassup;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
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

                if(!image.equals("default")) {
                    Picasso.get().load(image).placeholder(R.mipmap.deafult_profile_round).into(civProfilePicture);
                }

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
                Log.e(TAG, "onClickListener");

                startActivityForResult(intent, GALLERY_PICK);



                Log.e(TAG, "onClickListener starting activity");



            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "onActivityResult: entered");

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Log.e(TAG, "onActivityResult: Result OK");
//            Instrumentation.ActivityResult result = new

//            try {

                Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAllowRotation(true)
                    .setAutoZoomEnabled(true)
                    .setMultiTouchEnabled(true)
                    .setAspectRatio(1,1)
                    .start(this);



          /*  } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "onActivityResult: catching exception");
                Toast.makeText(this, "Unexpected error occured....Redirecting", Toast.LENGTH_SHORT).show();
            }*/

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            //storing image
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                DatabaseReference profilePicDatabase = databaseReference.child("image");
                StorageReference profilePicRef = mStorageRef.child(resultUri.getLastPathSegment());

                profilePicDatabase.setValue(resultUri.toString());
                profilePicRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Profile picture changed successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



                //unable to compress images

           /*     File thumbImage = new File(resultUri.getPath());

                Bitmap compressedImageBitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumbImage);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumbImageByte = baos.toByteArray();

                profilePicRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {

                            StorageReference thumbProfilePicRef = mStorageRef.child(task.getResult().getDownloadUrl().getLastPathSegment()).child("thumb_image");

                            UploadTask uploadTask = thumbProfilePicRef.putBytes(thumbImageByte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        profilePicDatabase.setValue(task.getResult().getDownloadUrl().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    Toast.makeText(ProfileActivity.this, "Image Uploaded successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            });

                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                            Uri downloadUrl = taskSnapshot.getDownloadUrl();




                        }
                    }
                });*/



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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