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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditUserActivity extends AppCompatActivity {
    Spinner spinnerStatus;
    TextInputLayout NameInputLayout,PhoneInputLayout,AgeInputLayout;
    TextInputEditText NameEditText,PhoneEditText, AgeEditText;
    Button btnConfirm;
    private String selectedSpinnerItem;
    Toolbar toolbar;
    String accountName;
    long status;
    boolean checkErrorLength = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        NameEditText = (TextInputEditText) findViewById(R.id.NameEditText);
        PhoneEditText = (TextInputEditText) findViewById(R.id.PhoneEditText);
        AgeEditText = (TextInputEditText) findViewById(R.id.AgeEditText);
        NameInputLayout = (TextInputLayout) findViewById(R.id.NameInputLayout);
        PhoneInputLayout = (TextInputLayout) findViewById(R.id.PhoneInputLayout);
        AgeInputLayout = (TextInputLayout) findViewById(R.id.AgeInputLayout);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        LoadSpinnerStatus();
        LoadEditTextAndInputLayout(NameInputLayout,NameEditText);
        LoadEditTextAndInputLayout(PhoneInputLayout,PhoneEditText);
        LoadEditTextAndInputLayout(AgeInputLayout,AgeEditText);
        LoadToolBar();
        Intent intent = getIntent();
        // Retrieve the data from the Intent using the key
        String userName = intent.getStringExtra("accountName");
        accountName = userName;
        // Find userId and load information
        FindUserIdAndLoadAllInformation(userName);
        //Update user when click button
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditUserInformation();
            }
        });
    }
    public void EditUserInformation(){
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        usersReference.orderByChild("userName").equalTo(accountName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve the userId of the user with the specified phoneNum
                    String userId =  userSnapshot.getKey();
                    DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                    String name = NameEditText.getText().toString();
                    String ageStr = AgeEditText.getText().toString();
                    String phone = PhoneEditText.getText().toString();
                    if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty()){
                        Toast.makeText(EditUserActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
                    } else {
                        if (checkErrorLength){
                            Toast.makeText(EditUserActivity.this, "Please check length of information", Toast.LENGTH_SHORT).show();
                        } else{
                            long ageValue = 0;
                            try{
                                ageValue = Long.parseLong(ageStr);
                            }catch (Exception e){
                                Toast.makeText(EditUserActivity.this, "Invalid age", Toast.LENGTH_SHORT).show();
                            }
                            if (ageValue>0){
                                usersReference.child("name").setValue(name);
                                usersReference.child("phoneNum").setValue(phone);
                                usersReference.child("age").setValue(Long.parseLong(ageStr));
                                if (selectedSpinnerItem.equals("Locked")){
                                    usersReference.child("status").setValue(0L);
                                }
                                //Move to list user
                                Intent intent = new Intent(EditUserActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else{
                                Toast.makeText(EditUserActivity.this, "Invalid age", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void FindUserIdAndLoadAllInformation(String accountName) {
        // Query the database to find the user with the specified accountName
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        usersReference.orderByChild("userName").equalTo(accountName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve the userId of the user with the specified phoneNum
                    String userId =  userSnapshot.getKey();
                    // Call a method to load data
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);;
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Check if the dataSnapshot exists and contains data
                            if (dataSnapshot.exists()) {
                                // Retrieve the user data from the dataSnapshot
                                String name = dataSnapshot.child("name").getValue(String.class);
                                String phoneNum = dataSnapshot.child("phoneNum").getValue(String.class);
                                Long age = dataSnapshot.child("age").getValue(Long.class);
                                Long status =  dataSnapshot.child("status").getValue(Long.class);
                                // Update the EditText fields with the retrieved data
                                NameEditText.setText(name);
                                PhoneEditText.setText(phoneNum);
                                AgeEditText.setText(String.valueOf(age));
                                if (status==1){
                                    spinnerStatus.setSelection(0);
                                } else{
                                    spinnerStatus.setSelection(1);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors or onCancelled events here
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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
                    checkErrorLength=true;
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
            Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_profile) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            Intent intent = new Intent(EditUserActivity.this, ProfileActivity.class);
            intent.putExtra("userName", email);
            startActivity(intent);
            return true;
        }  else if (itemId == R.id.action_history) {
            Intent intent = new Intent(EditUserActivity.this, LoginHistoryActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_view_student_list) {
            Intent intent = new Intent(EditUserActivity.this, StudentListActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_add_student) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(EditUserActivity.this, AddStudentActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(EditUserActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_add_user) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(EditUserActivity.this, AddUserActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(EditUserActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if (itemId == R.id.action_view_user_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(EditUserActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(EditUserActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_view_student_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(EditUserActivity.this, StudentListActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(EditUserActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }  else {
            return super.onOptionsItemSelected(item);
        }
    }
}
