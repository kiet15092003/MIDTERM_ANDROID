package com.example.androidmidterm_firebase;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    ImageButton btnEditImg;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String PREFS_NAME = "MyPrefsFile";
    private Uri selectedImageUri2;
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
        //tvName.setText("abc");


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

    public void ChangeProfileImg(){
        imv_Img = findViewById(R.id.iv_imgSrc);
        btnEditImg = findViewById(R.id.iv_selectImgUser);
        btnEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Xử lý kết quả trả về từ trình chọn ảnh
            Uri selectedImageUri = data.getData();
            selectedImageUri2 = data.getData();
            if (selectedImageUri != null) {

                saveImageUri(selectedImageUri);

                // Hiển thị ảnh đã chọn lên ImageView
                Picasso.with(ProfileActivity.this)
                        .load(selectedImageUri.toString())
                        .into(imv_Img);
            }
        }
    }
    private void saveImageUri(Uri imageUri) {
        // Save the image URI using SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selectedImageUri", imageUri.toString());
        editor.apply();
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
                                ChangeProfileImg();
                                // Load the saved image URI when the activity is created
                                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                String savedImageUriString = preferences.getString("selectedImageUri", null);
                                //requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                                //tvName.setText(savedImageUriString);
                                if (savedImageUriString != null) {

                                    Picasso.with(ProfileActivity.this)
                                            .load(savedImageUriString)  // Load the saved image URI using Picasso
                                            .into(imv_Img);
                                }
                                else {
                                    Picasso.with(ProfileActivity.this)
                                            .load(imgSrc)
                                            .into(imv_Img);
                                }

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
