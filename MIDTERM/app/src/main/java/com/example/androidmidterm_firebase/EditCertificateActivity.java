package com.example.androidmidterm_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditCertificateActivity extends AppCompatActivity {
    TextInputLayout StudentNameInputLayout,CertificateInputLayout,ScoreInputLayout;
    TextInputEditText StudentNameEditText,CertificateEditText,ScoreEditText;
    MaterialButton btn_back_to_student;
    Button btn_update_certificate;
    boolean checkErrorLength = false;
    String certificateName, studentId;
    Double score;
    private static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_certificate);
        StudentNameInputLayout = (TextInputLayout) findViewById(R.id.StudentNameInputLayout);
        CertificateInputLayout = (TextInputLayout)  findViewById(R.id.CertificateInputLayout);
        ScoreInputLayout = (TextInputLayout)  findViewById(R.id.ScoreInputLayout);
        StudentNameEditText = (TextInputEditText) findViewById(R.id.StudentNameEditText);
        CertificateEditText = (TextInputEditText) findViewById(R.id.CertificateEditText);
        ScoreEditText = (TextInputEditText) findViewById(R.id.ScoreEditText);
        btn_back_to_student = findViewById(R.id.btn_back_to_student);
        btn_update_certificate = findViewById(R.id.btn_update_certificate);
        LoadEditTextAndInputLayout(CertificateInputLayout,CertificateEditText);
        LoadEditTextAndInputLayout(ScoreInputLayout,ScoreEditText);
        Intent intent = getIntent();
        // Retrieve the data from the Intent using the key
        certificateName = intent.getStringExtra("certificateName");
        studentId = intent.getStringExtra("studentId");
        score = Double.parseDouble(intent.getStringExtra("score"));
        //Toast.makeText(EditCertificateActivity.this, score, Toast.LENGTH_SHORT).show();
        //certificateName = intentValue;
        LoadAllInformation();
        btn_update_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String certificateName = CertificateEditText.getText().toString();
                String scoreStr = ScoreEditText.getText().toString();
                if (certificateName.isEmpty() || scoreStr.isEmpty()){
                    Toast.makeText(EditCertificateActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkErrorLength) {
                        Toast.makeText(EditCertificateActivity.this, "Please check length of information", Toast.LENGTH_SHORT).show();
                    } else {
                        double scoreValue = 0;
                        try {
                            scoreValue = Double.parseDouble(scoreStr);
                        } catch (Exception e) {
                            Toast.makeText(EditCertificateActivity.this, "Invalid score", Toast.LENGTH_SHORT).show();
                        }
                        if (scoreValue > 0)
                        {
                            // Update here
                            DatabaseReference certificatesRef = FirebaseDatabase.getInstance().getReference().child("certificates");
                            certificatesRef.orderByChild("studentId").equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot certificateSnapshot : snapshot.getChildren()){
                                        String keys = certificateSnapshot.getKey();
                                        DatabaseReference certificateRef = FirebaseDatabase.getInstance().getReference().child("certificates").child(keys);
                                        certificateRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String certificateNames = snapshot.child("certificateName").getValue(String.class);
                                                if (certificateNames.equals(certificateName)){
                                                    certificateRef.child("certificateName").setValue(certificateName);
                                                    certificateRef.child("score").setValue(Double.parseDouble(scoreStr));
                                                    Toast.makeText(EditCertificateActivity.this, "Edit certificate for student complete", Toast.LENGTH_SHORT).show();
                                                    //Move to student detail
                                                    Intent intent = new Intent(EditCertificateActivity.this, StudentDetailActivity.class);
                                                    intent.putExtra("studentId", studentId);
                                                    startActivityForResult(intent,REQUEST_CODE);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            Toast.makeText(EditCertificateActivity.this, "Invalid score", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    public void LoadAllInformation(){
        CertificateEditText.setText(certificateName);
        ScoreEditText.setText(String.valueOf(score));
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("students").child(studentId);
        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String studentName =  snapshot.child("name").getValue(String.class);
                StudentNameEditText.setText(studentName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void LoadEditTextAndInputLayout(TextInputLayout textInputLayout, TextInputEditText textInputEditText){
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputEditText.setText("");
            }
        });
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > textInputLayout.getCounterMaxLength()){
                    textInputLayout.setError("Max character length is " + textInputLayout.getCounterMaxLength());
                    checkErrorLength = true;
                }
                else{
                    textInputLayout.setError(null);
                    checkErrorLength=false;
                }
            }
        });
    }
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
        }
    }
}
