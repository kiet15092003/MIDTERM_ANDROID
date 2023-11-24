package com.example.androidmidterm_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    TextView tvName;
    TextView profilePhone;
    TextView profileAge;
    TextView profileStatus;
    MaterialButton btnHistory;
    MaterialButton btnLogout;
    Button btnBackToList;
    CircleImageView imv_Img;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        tvName = findViewById(R.id.tvName);
        profilePhone = findViewById(R.id.profilePhone);
        profileStatus = findViewById(R.id.profileStatus);
        profileAge = findViewById(R.id.profileAge);
        btnHistory = findViewById(R.id.btn_ViewHistory);
        btnLogout = findViewById(R.id.btn_Logout);
        btnBackToList = findViewById(R.id.btnBackToList);
        imv_Img = findViewById(R.id.iv_imgSrc);
        Intent intent = getIntent();
        // Retrieve the data from the Intent using the key
        String userName = intent.getStringExtra("userName");
        EditUserInformation(userName);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnBackToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LoginHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
    public void EditUserInformation(String accountName){
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        usersReference.orderByChild("userName").equalTo(accountName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId =  userSnapshot.getKey();
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Check if the dataSnapshot exists and contains data
                            if (dataSnapshot.exists()) {
                                // Retrieve the user data from the dataSnapshot
                                String name = dataSnapshot.child("name").getValue(String.class);
                                String phoneNum = dataSnapshot.child("phoneNum").getValue(String.class);
                                String imgSrc = dataSnapshot.child("imgSrc").getValue(String.class);
                                Long age = dataSnapshot.child("age").getValue(Long.class);
                                Long status =  dataSnapshot.child("status").getValue(Long.class);
                                // Update the EditText fields with the retrieved data
                                tvName.setText(name);
                                profilePhone.setText(phoneNum);
                                profileAge.setText(String.valueOf(age));
                                if (status==1){
                                    profileStatus.setText("Normal");
                                } else{
                                    profileStatus.setText("Locked");
                                }
                                imv_Img.getLayoutParams().width = 250;
                                imv_Img.getLayoutParams().height = 250;
                                Picasso.with(ProfileActivity.this)
                                        .load(imgSrc)
                                        .into(imv_Img);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
