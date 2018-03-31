package com.example.designstudioiitr.wassup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    RecyclerView rvAllUsers;
    Toolbar toolbar;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        rvAllUsers = findViewById(R.id.rvAllUsers);
        toolbar = findViewById(R.id.all_users_activity_app_bar);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");

        rvAllUsers.setHasFixedSize(true);
        rvAllUsers.setLayoutManager(new LinearLayoutManager(AllUsersActivity.this));

        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.layout_all_users,
                UserViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getImage());

                final String userId = getRef(position).getKey();

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intenet = new Intent(AllUsersActivity.this, SingleUserActivity.class);
                        intenet.putExtra("userId", userId);
                        startActivity(intenet);
                    }
                });
            }
        };
        rvAllUsers.setAdapter(firebaseRecyclerAdapter);
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {

        View view;

        public UserViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name) {
            TextView tvName = view.findViewById(R.id.tvUserName);
            tvName.setText(name);
        }
        public  void setStatus(String status) {
            TextView tvStatus = view.findViewById(R.id.tvUserStatus);
            tvStatus.setText(status);
        }
        public void setImage(final String image) {
            final CircleImageView civProfilePic = view.findViewById(R.id.civUserProfilePic);
//            Picasso.get().load(image).into(civProfilePic);

            Picasso.get()
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.mipmap.deafult_profile_round)
                    .into(civProfilePic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get()
                                    .load(image)
                                    .placeholder(R.mipmap.deafult_profile_round)
                                    .error(R.mipmap.deafult_profile_round)
                                    .into(civProfilePic);
                        }

                    });

        }
    }


}
