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

public class AddCertificateActivity extends AppCompatActivity {
    TextInputLayout StudentNameInputLayout,CertificateInputLayout,ScoreInputLayout;
    TextInputEditText StudentNameEditText,CertificateEditText,ScoreEditText;
    MaterialButton btn_back_to_student;
    Button btn_add_certificate;
    boolean checkErrorLength = false;
    String studentId;
    private static final int REQUEST_CODE = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificate);
        StudentNameInputLayout = (TextInputLayout) findViewById(R.id.StudentNameInputLayout);
        CertificateInputLayout = (TextInputLayout)  findViewById(R.id.CertificateInputLayout);
        ScoreInputLayout = (TextInputLayout)  findViewById(R.id.ScoreInputLayout);
        StudentNameEditText = (TextInputEditText) findViewById(R.id.StudentNameEditText);
        CertificateEditText = (TextInputEditText) findViewById(R.id.CertificateEditText);
        ScoreEditText = (TextInputEditText) findViewById(R.id.ScoreEditText);
        btn_back_to_student = findViewById(R.id.btn_back_to_student);
        btn_add_certificate = findViewById(R.id.btn_add_certificate);
        LoadEditTextAndInputLayout(CertificateInputLayout,CertificateEditText);
        LoadEditTextAndInputLayout(ScoreInputLayout,ScoreEditText);
        Intent intent = getIntent();
        // Retrieve the data from the Intent using the key
        String intentValue = intent.getStringExtra("studentId");
        String intentFrom = intent.getStringExtra("from");
        studentId = intentValue;
        LoadStudentNameByStudentId(studentId);
        btn_add_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add certificate
                String certificateName = CertificateEditText.getText().toString();
                String score = ScoreEditText.getText().toString();
                if (certificateName.isEmpty() || score.isEmpty()){
                    Toast.makeText(AddCertificateActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkErrorLength) {
                        Toast.makeText(AddCertificateActivity.this, "Please check length of information", Toast.LENGTH_SHORT).show();
                    } else {
                        double scoreValue = 0;
                        try {
                            scoreValue = Double.parseDouble(score);
                        } catch (Exception e) {
                            Toast.makeText(AddCertificateActivity.this, "Invalid score", Toast.LENGTH_SHORT).show();
                        }
                        if (scoreValue > 0)
                        {
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("certificates");
                            String newCertificateId = usersRef.push().getKey();
                            CertificateModel certificate = new CertificateModel(certificateName,studentId,scoreValue);
                            usersRef.child(newCertificateId).setValue(certificate);
                            CertificateEditText.setText("");
                            ScoreEditText.setText("");
                            Toast.makeText(AddCertificateActivity.this, "Add certificate successful", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(AddCertificateActivity.this, "Invalid score", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        btn_back_to_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if intent fro add student
                if (intentFrom.equals("AddStudentActivity")){
                    Intent intent = new Intent(AddCertificateActivity.this, AddStudentActivity.class);
                    startActivity(intent);
                } else{
                    // if intent from detail
                    Intent intent = new Intent(AddCertificateActivity.this, StudentDetailActivity.class);
                    intent.putExtra("studentId", studentId);
                    // Start the new activity with the Intent, expecting a result
                    startActivityForResult(intent,REQUEST_CODE);
                }
            }
        });
    }
    public void LoadStudentNameByStudentId(String stdId){
        DatabaseReference studentReference = FirebaseDatabase.getInstance().getReference().child("students").child(stdId);
        studentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("name").getValue(String.class);
                    StudentNameEditText.setText(name);
                }
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
