package com.example.androidmidterm_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUserActivity extends AppCompatActivity {
    Spinner spinnerStatus;
    TextInputLayout NameInputLayout,PhoneInputLayout,AgeInputLayout,UserNameInputLayout,PasswordInputLayout;
    TextInputEditText NameEditText,PhoneEditText, AgeEditText,UserNameEditText,PasswordEditText;
    Button btnAddNewUser;
    private String selectedSpinnerItem;
    Toolbar toolbar;
    boolean checkErrorLength = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        NameEditText = (TextInputEditText) findViewById(R.id.NameEditText);
        PhoneEditText = (TextInputEditText) findViewById(R.id.PhoneEditText);
        AgeEditText = (TextInputEditText) findViewById(R.id.AgeEditText);
        NameInputLayout = (TextInputLayout) findViewById(R.id.NameInputLayout);
        PhoneInputLayout = (TextInputLayout) findViewById(R.id.PhoneInputLayout);
        AgeInputLayout = (TextInputLayout) findViewById(R.id.AgeInputLayout);
        UserNameEditText = (TextInputEditText) findViewById(R.id.UserNameEditText);
        PasswordEditText = (TextInputEditText) findViewById(R.id.PasswordEditText);
        UserNameInputLayout = (TextInputLayout) findViewById(R.id.UserNameInputLayout);
        PasswordInputLayout = (TextInputLayout) findViewById(R.id.PasswordInputLayout);
        btnAddNewUser = (Button) findViewById(R.id.btnAddUser);
        LoadSpinnerStatus();
        LoadEditTextAndInputLayout(NameInputLayout,NameEditText);
        LoadEditTextAndInputLayout(PhoneInputLayout,PhoneEditText);
        LoadEditTextAndInputLayout(AgeInputLayout,AgeEditText);
        LoadEditTextAndInputLayout(NameInputLayout,NameEditText);
        LoadEditTextAndInputLayout(PasswordInputLayout,PasswordEditText);
        UserNameEditText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        PasswordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LoadToolBar();
        btnAddNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = NameEditText.getText().toString();
                String age = AgeEditText.getText().toString();
                String phone = PhoneEditText.getText().toString();
                String userName = UserNameEditText.getText().toString();
                String password = PasswordEditText.getText().toString();
                if (name.isEmpty() || age.isEmpty() || phone.isEmpty() || userName.isEmpty() || password.isEmpty()){
                    Toast.makeText(AddUserActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkErrorLength){
                        Toast.makeText(AddUserActivity.this, "Please check length of information", Toast.LENGTH_SHORT).show();
                    } else{
                        long ageValue = 0;
                        try{
                            ageValue = Long.parseLong(age);
                        }catch (Exception e){
                            Toast.makeText(AddUserActivity.this, "Invalid age", Toast.LENGTH_SHORT).show();
                        }
                        if (ageValue>0){
                            //Correct information, continue to add
                            if (userName.toLowerCase().contains("manager") || userName.toLowerCase().contains("employee")){
                                String defaultImgSrc = "https://img.freepik.com/premium-photo/young-man-is-holding-book-smiling_905085-17.jpg";
                                // Create new account to login for user
                                String email = userName;
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                // Save the current user before creating a new one
                                FirebaseUser currentUser = auth.getCurrentUser();
                                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(AddUserActivity.this,
                                        new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Registration success
                                                    Toast.makeText(AddUserActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                                    // Push the new user to the database
                                                    long status = 1L;
                                                    if (selectedSpinnerItem.equals("Locked")){
                                                        status = 0L;
                                                    }
                                                    UserModel newUser = new UserModel(name,phone,defaultImgSrc,status,Long.parseLong(age),userName,password);
                                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                                                    String userId = usersRef.push().getKey(); // Generate a unique key for the new user
                                                    usersRef.child(userId).setValue(newUser);
                                                    //Clear all information
                                                    NameEditText.setText("");
                                                    PhoneEditText.setText("");
                                                    AgeEditText.setText("");
                                                    UserNameEditText.setText("");
                                                    PasswordEditText.setText("");
                                                    if (currentUser != null) {
                                                        auth.signInWithCredential(EmailAuthProvider.getCredential(currentUser.getEmail(), "admin1abc"))
                                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                                        if (task.isSuccessful()) {
                                                                            // Admin user re-signed in successfully
                                                                        } else {
                                                                            // Handle re-sign in failure
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    // If registration fails, display a message to the user
                                                    Toast.makeText(AddUserActivity.this, "Registration failed. " + task.getException().getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            else{
                                Toast.makeText(AddUserActivity.this, "User name format is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        } else{
                            Toast.makeText(AddUserActivity.this, "Invalid age", Toast.LENGTH_SHORT).show();
                        }
                    }
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
    public void LoadSpinnerStatus(){
        spinnerStatus = findViewById(R.id.spinner_status);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_status_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinnerItem = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
            Intent intent = new Intent(AddUserActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_profile) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            Intent intent = new Intent(AddUserActivity.this, ProfileActivity.class);
            intent.putExtra("userName", email);
            startActivity(intent);
            return true;
        }  else if (itemId == R.id.action_history) {
            Intent intent = new Intent(AddUserActivity.this, LoginHistoryActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_view_student_list) {
            Intent intent = new Intent(AddUserActivity.this, StudentListActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_add_student) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(AddUserActivity.this, AddStudentActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(AddUserActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_add_user) {
//            FirebaseAuth auth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = auth.getCurrentUser();
//            String email = currentUser.getEmail();
//            if (email.toLowerCase().contains("admin")){
//                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
//                startActivity(intent);
//            } else{
//                Toast.makeText(MainActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
//            }
            return true;
        }else if (itemId == R.id.action_view_user_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(AddUserActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(AddUserActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_view_student_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(AddUserActivity.this, StudentListActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(AddUserActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }  else {
            return super.onOptionsItemSelected(item);
        }
    }
}
