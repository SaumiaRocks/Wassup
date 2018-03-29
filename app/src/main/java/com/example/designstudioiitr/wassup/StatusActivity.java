package com.example.designstudioiitr.wassup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StatusActivity extends AppCompatActivity {
    private static final String TAG = "StatusActivity";

    android.support.v7.widget.Toolbar toolbar;
    ListView lvSampleStatus;
    ArrayList<String> sampleStatus;
    ArrayAdapter<String> adapter;
    DatabaseReference databaseSampleStatus;
    ImageButton ibEditStatus;
    String currentStatus;
    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        ibEditStatus = findViewById(R.id.ibEditStatus);
        tvStatus = findViewById(R.id.tvStatus);

        //setting status in textview
        Intent intent = getIntent();
        currentStatus = intent.getStringExtra("currentStatus");
        tvStatus.setText(currentStatus);

        //setting edit status button
        ibEditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatusActivity.this, UpdateStatusActivity.class);
                intent.putExtra("currentStatus", currentStatus );
                startActivity(intent);
            }
        });

        databaseSampleStatus = FirebaseDatabase.getInstance().getReference().child("StatusChoices");

        lvSampleStatus = findViewById(R.id.lvSampleStatus);
        sampleStatus = new ArrayList<String>();

        inflateArrayListFromFirebase();

        //setting up tool bar
        toolbar = findViewById(R.id.status_activity_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void inflateArrayListFromFirebase() {

        databaseSampleStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> statusSnapshot = dataSnapshot.getChildren();

                for (DataSnapshot statusChoices : statusSnapshot) {
                    String status = statusChoices.getValue(String.class);
                    Log.e(TAG, "onDataChange: status :" + status);
                    sampleStatus.add(status);
                }

                for(int i=0; i<sampleStatus.size(); i++) {
                    Log.e(TAG, "onCreate: Sample Status " + i + " : " + sampleStatus.get(i) );
                }

                Log.e(TAG, "onDataChange: setting up array adapter");

                adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_primary_text, R.id.list_content ,sampleStatus);
                lvSampleStatus.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(StatusActivity.this, "Error loading Status choices...", Toast.LENGTH_SHORT);

            }
        });



    }

}
