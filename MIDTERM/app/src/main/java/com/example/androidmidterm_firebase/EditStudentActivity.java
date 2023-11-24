package com.example.androidmidterm_firebase;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditStudentActivity extends AppCompatActivity {
    String studentId;
    boolean checkErrorLength = false;
    Toolbar toolbar;
    TextInputLayout NameInputLayout,AddressInputLayout,AgeInputLayout,ClassNameInputLayout,ScoreInputLayout;
    TextInputEditText NameEditText,AddressEditText, AgeEditText,ClassNameEditText,ScoreEditText;
    Button btn_update_student;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);
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
        LoadEditTextAndInputLayout(NameInputLayout, NameEditText);
        LoadEditTextAndInputLayout(AddressInputLayout, AddressEditText);
        LoadEditTextAndInputLayout(AgeInputLayout, AgeEditText);
        LoadEditTextAndInputLayout(ClassNameInputLayout, ClassNameEditText);
        LoadEditTextAndInputLayout(ScoreInputLayout, ScoreEditText);
        LoadToolBar();
        Intent intent = getIntent();
        // Retrieve the data from the Intent using the key
        String intentValue = intent.getStringExtra("studentId");
        studentId = intentValue;
        btn_update_student = findViewById(R.id.btn_update_student);
        LoadAllInformation(studentId);
        btn_update_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = NameEditText.getText().toString();
                String age = AgeEditText.getText().toString();
                String address = AddressEditText.getText().toString();
                String score = ScoreEditText.getText().toString();
                String className = ClassNameEditText.getText().toString();
                if (name.isEmpty() || age.isEmpty() || address.isEmpty() || score.isEmpty() ||
                        className.isEmpty()){
                    Toast.makeText(EditStudentActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkErrorLength) {
                        Toast.makeText(EditStudentActivity.this, "Please check length of information", Toast.LENGTH_SHORT).show();
                    } else {
                        long ageValue = 0;
                        double scoreValue = 0;
                        try {
                            scoreValue = Double.parseDouble(score);
                            ageValue = Long.parseLong(age);
                        } catch (Exception e) {
                            Toast.makeText(EditStudentActivity.this, "Invalid score or age", Toast.LENGTH_SHORT).show();
                        }
                        if (scoreValue > 0 && ageValue > 0) {
                            //Update student here
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("students").child(studentId);
                            usersRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    usersRef.child("name").setValue(name);
                                    usersRef.child("className").setValue(className);
                                    usersRef.child("address").setValue(address);
                                    usersRef.child("score").setValue(Double.valueOf(score));
                                    usersRef.child("age").setValue(Long.valueOf(age));
                                    Toast.makeText(EditStudentActivity.this, "Edit student complete", Toast.LENGTH_SHORT).show();
                                    //Move to list student
                                    Intent intent = new Intent(EditStudentActivity.this, StudentListActivity.class);
                                    startActivity(intent);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else{
                            Toast.makeText(EditStudentActivity.this, "Invalid score or age", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    public void LoadAllInformation(String studentId){
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("students").child(studentId);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String className = snapshot.child("className").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    Long age = snapshot.child("age").getValue(Long.class);
                    Double score = snapshot.child("score").getValue(Double.class);
                    NameEditText.setText(name);
                    ClassNameEditText.setText(className);
                    AddressEditText.setText(address);
                    AgeEditText.setText(String.valueOf(age));
                    ScoreEditText.setText(String.valueOf(score));
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
            Intent intent = new Intent(EditStudentActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_profile) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            Intent intent = new Intent(EditStudentActivity.this, ProfileActivity.class);
            intent.putExtra("userName", email);
            startActivity(intent);
            return true;
        }  else if (itemId == R.id.action_history) {
            Intent intent = new Intent(EditStudentActivity.this, LoginHistoryActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_view_student_list) {
                Intent intent = new Intent(EditStudentActivity.this, StudentListActivity.class);
                startActivity(intent);
                return true;
        } else if (itemId == R.id.action_add_student) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(EditStudentActivity.this, AddStudentActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(EditStudentActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_add_user) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(EditStudentActivity.this, AddUserActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(EditStudentActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if (itemId == R.id.action_view_user_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(EditStudentActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(EditStudentActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_view_student_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(EditStudentActivity.this, StudentListActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(EditStudentActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }  else {
            return super.onOptionsItemSelected(item);
        }
    }
}
