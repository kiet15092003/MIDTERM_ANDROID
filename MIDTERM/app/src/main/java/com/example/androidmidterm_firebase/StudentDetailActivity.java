package com.example.androidmidterm_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentDetailActivity extends AppCompatActivity {
    TextView tvName,tv_class,tv_address,tv_age,tv_score;
    RecyclerView rv_certificate;
    Button btnBackToList;
    MaterialButton btn_add_certificate;
    String studentId;
    CircleImageView imv_Img;
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_EDIT = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_student);
        tvName = findViewById(R.id.tvName);
        tv_class = findViewById(R.id.tv_class);
        tv_address = findViewById(R.id.tv_address);
        tv_age = findViewById(R.id.tv_age);
        tv_score = findViewById(R.id.tv_score);
        btnBackToList = findViewById(R.id.btnBackToList);
        btn_add_certificate = findViewById(R.id.btn_add_certificate);
        imv_Img = findViewById(R.id.iv_imgSrc);
        Intent intent = getIntent();
        // Retrieve the data from the Intent using the key
        String intentValue = intent.getStringExtra("studentId");
        studentId = intentValue;
        LoadAllInformation(studentId);
        btnBackToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentDetailActivity.this, StudentListActivity.class);
                startActivity(intent);
            }
        });
        imv_Img.getLayoutParams().width = 250;
        imv_Img.getLayoutParams().height = 250;
        Picasso.with(StudentDetailActivity.this)
                .load("https://static.vecteezy.com/system/resources/previews/015/278/806/original/the-avatar-of-the-graduate-student-icon-illustration-in-a-flat-style-isolated-on-a-white-background-vector.jpg")
                .into(imv_Img);
        btn_add_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                String email = currentUser.getEmail();
                if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                    Intent intent = new Intent(StudentDetailActivity.this, AddCertificateActivity.class);
                    intent.putExtra("studentId", studentId);
                    intent.putExtra("from","StudentDetailActivity");
                    // Start the new activity with the Intent, expecting a result
                    startActivityForResult(intent,REQUEST_CODE);
                } else{
                    Toast.makeText(StudentDetailActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void LoadAllInformation(String studentId) {
        rv_certificate = findViewById(R.id.rv_certificate);
        rv_certificate.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("students").child(studentId);
        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String className = snapshot.child("className").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String age = String.valueOf(snapshot.child("age").getValue(Long.class));
                    String score = String.valueOf(snapshot.child("score").getValue(Double.class));
                    //Load textView
                    tvName.setText(name);
                    tv_class.setText(className);
                    tv_address.setText(address);
                    tv_age.setText(age);
                    tv_score.setText(score);
                    //Load Recycler view
                    DatabaseReference certificateRefs = FirebaseDatabase.getInstance().getReference().child("certificates");
                    certificateRefs.orderByChild("studentId").equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<CertificateModel> certificates = new ArrayList<>();
                            for (DataSnapshot certificateSnapshot : snapshot.getChildren()) {
                                CertificateModel certificateModel  = certificateSnapshot.getValue(CertificateModel.class);
                                certificates.add(certificateModel);
                            }
                            CertificatesAdapter adapter = new CertificatesAdapter(certificates,StudentDetailActivity.this);
                            rv_certificate.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }
        }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            };
    });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if the result is for the correct request code
        if (requestCode == REQUEST_CODE) {
            // Check if the operation was successful
            if (resultCode == RESULT_OK) {
                // Retrieve the result data from the Intent
                String resultData = data.getStringExtra("result_key");
                // Do something with the result data
            } else {
                // Handle the case where the operation was not successful
            }
        } else if (requestCode == REQUEST_CODE_EDIT){
            if (resultCode == RESULT_OK) {
                // Retrieve the result data from the Intent
                String resultData = data.getStringExtra("result_key");
                // Do something with the result data
            } else {
                // Handle the case where the operation was not successful
            }
        }
    }
}
