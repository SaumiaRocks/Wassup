package com.example.designstudioiitr.wassup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    TextInputLayout tilName, tilEmail, tilPassword;
    EditText etEmail, etPassword, etName;
    Button btnCreateAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
    android.support.v7.widget.Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        toolbar = findViewById(R.id.app_bar_register_activity);

        //setting up tool bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();


                //code to detect empty fields

                signUpNewUser(email, password);
            }
        });

    }


    private void signUpNewUser(String email, String password) {

        //setting up progressDialog
        progressDialog.setTitle("Registering User");
        progressDialog.setMessage("Please wait while we create your account...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            //saving data to firebase
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(user.getUid());

                            String name = etName.getText().toString();
                            HashMap<String, String> userDetails = new HashMap<String, String>();

                            Log.e(TAG, "onComplete: name : " + name);

                            userDetails.put("name", name);
                            userDetails.put("status", "Hey there! I'm using Wassup!");
                            userDetails.put("image", "default");
                            userDetails.put("thumb_image", "default");

                            databaseReference.setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                        progressDialog.dismiss();
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, "Authentication failed. Please try again...", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed. Please try again...",
                                    LENGTH_SHORT).show();
//                            updateUI(null);

                            progressDialog.hide();
                        }
                        // ...
                    }
                });
    }


/*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }*/


}
