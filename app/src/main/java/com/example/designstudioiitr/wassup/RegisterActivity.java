package com.example.designstudioiitr.wassup;

import android.app.ProgressDialog;
import android.content.Intent;
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

import static android.widget.Toast.LENGTH_SHORT;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    TextInputLayout tilName, tilEmail, tilPassword;
    EditText etEmail, etPassword;
    Button btnCreateAcoount;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnCreateAcoount = findViewById(R.id.btnCreateAccount);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        btnCreateAcoount.setOnClickListener(new View.OnClickListener() {
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
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(RegisterActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();

//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                        progressDialog.dismiss();
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
