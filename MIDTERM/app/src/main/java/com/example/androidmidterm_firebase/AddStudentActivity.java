package com.example.androidmidterm_firebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddStudentActivity extends AppCompatActivity {
    TextInputLayout NameInputLayout,AddressInputLayout,AgeInputLayout,ClassNameInputLayout,ScoreInputLayout;
    TextInputEditText NameEditText,AddressEditText, AgeEditText,ClassNameEditText,ScoreEditText;
    Button btn_add_new_student;
    MaterialButton btn_add_certificate;
    Toolbar toolbar;
    String studentId = "";
    boolean checkErrorLength = false;
    private static final int REQUEST_CODE = 1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        toolbar = findViewById(R.id.toolbar);
        NameEditText = (TextInputEditText) findViewById(R.id.NameEditText);
        AddressEditText = (TextInputEditText) findViewById(R.id.AddressEditText);
        AgeEditText = (TextInputEditText) findViewById(R.id.AgeEditText);
        NameInputLayout = (TextInputLayout) findViewById(R.id.NameInputLayout);
        AddressInputLayout = (TextInputLayout) findViewById(R.id.AddressInputLayout);
        AgeInputLayout = (TextInputLayout) findViewById(R.id.AgeInputLayout);
        ClassNameEditText = (TextInputEditText) findViewById(R.id.ClassNameEditText);
        ScoreEditText = (TextInputEditText) findViewById(R.id.ScoreEditText);
        ClassNameInputLayout = (TextInputLayout) findViewById(R.id.ClassNameInputLayout);
        ScoreInputLayout = (TextInputLayout) findViewById(R.id.ScoreInputLayout);
        btn_add_new_student = findViewById(R.id.btn_add_student);
        btn_add_certificate = findViewById(R.id.btn_add_certificate);
        LoadEditTextAndInputLayout(NameInputLayout,NameEditText);
        LoadEditTextAndInputLayout(AddressInputLayout,AddressEditText);
        LoadEditTextAndInputLayout(AgeInputLayout,AgeEditText);
        LoadEditTextAndInputLayout(ClassNameInputLayout,ClassNameEditText);
        LoadEditTextAndInputLayout(ScoreInputLayout,ScoreEditText);
        LoadToolBar();
        btn_add_new_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = NameEditText.getText().toString();
                String age = AgeEditText.getText().toString();
                String address = AddressEditText.getText().toString();
                String score = ScoreEditText.getText().toString();
                String className = ClassNameEditText.getText().toString();
                if (name.isEmpty() || age.isEmpty() || address.isEmpty() || score.isEmpty() ||
                        className.isEmpty()){
                    Toast.makeText(AddStudentActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
                } else{
                    if (checkErrorLength){
                        Toast.makeText(AddStudentActivity.this, "Please check length of information", Toast.LENGTH_SHORT).show();
                    } else{
                        long  ageValue = 0;
                        double scoreValue = 0;
                        try{
                            scoreValue = Double.parseDouble(score);
                            ageValue = Long.parseLong(age);
                        }catch (Exception e){
                            Toast.makeText(AddStudentActivity.this, "Invalid score or age", Toast.LENGTH_SHORT).show();
                        }
                        if (scoreValue>0 && ageValue>0){
                            //Correct information, continue to add
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("students");
                            String newStudentId = usersRef.push().getKey();
                            studentId = newStudentId;
                            StudentModel student = new StudentModel(newStudentId,name,className,address,ageValue,scoreValue);
                            usersRef.child(newStudentId).setValue(student);
                            NameEditText.setText("");
                            AddressEditText.setText("");
                            AgeEditText.setText("");
                            ClassNameEditText.setText("");
                            ScoreEditText.setText("");
                            Toast.makeText(AddStudentActivity.this, "Add new student successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddStudentActivity.this, "Invalid score or age", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        btn_add_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studentId.equals("")){
                    Toast.makeText(AddStudentActivity.this, "Please add student first", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(AddStudentActivity.this, AddCertificateActivity.class);
                    intent.putExtra("studentId", studentId);
                    intent.putExtra("from","AddStudentActivity");
                    // Start the new activity with the Intent, expecting a result
                    startActivityForResult(intent,REQUEST_CODE);
                }
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
    public void LoadToolBar(){
        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            // Handle the Logout action
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AddStudentActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_profile) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            Intent intent = new Intent(AddStudentActivity.this, ProfileActivity.class);
            intent.putExtra("userName", email);
            startActivity(intent);
            return true;
        }  else if (itemId == R.id.action_history) {
            Intent intent = new Intent(AddStudentActivity.this, LoginHistoryActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_view_student_list) {
            Intent intent = new Intent(AddStudentActivity.this, StudentListActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_add_student) {
//            FirebaseAuth auth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = auth.getCurrentUser();
//            String email = currentUser.getEmail();
//            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
//                Intent intent = new Intent(AddUserActivity.this, AddStudentActivity.class);
//                startActivity(intent);
//            } else{
//                Toast.makeText(AddUserActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
//            }
            return true;
        } else if (itemId == R.id.action_add_user) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(AddStudentActivity.this, AddUserActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(AddStudentActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if (itemId == R.id.action_view_user_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(AddStudentActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(AddStudentActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_view_student_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(AddStudentActivity.this, StudentListActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(AddStudentActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }  else {
            return super.onOptionsItemSelected(item);
        }
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
        }
    }
}
