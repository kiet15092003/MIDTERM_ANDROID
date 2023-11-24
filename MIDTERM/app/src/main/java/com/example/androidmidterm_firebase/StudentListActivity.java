package com.example.androidmidterm_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentListActivity extends AppCompatActivity {
    RecyclerView rv_student;
    Toolbar toolbar;
    private static final int REQUEST_CODE_DETAIL = 1;
    private static final int REQUEST_CODE_EDIT = 2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        rv_student = findViewById(R.id.rv_student);
        LoadToolBar();
        LoadRecyclerView();
    }
    public void LoadRecyclerView(){
        //Load users to recycler view
        rv_student = findViewById(R.id.rv_student);
        rv_student.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("students");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<StudentModel> students = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    StudentModel student = userSnapshot.getValue(StudentModel.class);
                    students.add(student);
                }
                StudentAdapter adapter = new StudentAdapter(StudentListActivity.this,students);
                rv_student.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if the result is for the correct request code
        if (requestCode == REQUEST_CODE_DETAIL) {
            // Check if the operation was successful
            if (resultCode == RESULT_OK) {
                // Retrieve the result data from the Intent
                String resultData = data.getStringExtra("result_key");
                // Do something with the result data
            } else {
                // Handle the case where the operation was not successful
            }
        } else if (resultCode == REQUEST_CODE_EDIT) {
            if (resultCode == RESULT_OK) {
                // Retrieve the result data from the Intent
                String resultData = data.getStringExtra("result_key");
                // Do something with the result data
            } else {
                // Handle the case where the operation was not successful
            }
        }
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
            Intent intent = new Intent(StudentListActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_profile) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            Intent intent = new Intent(StudentListActivity.this, ProfileActivity.class);
            intent.putExtra("userName", email);
            startActivity(intent);
            return true;
        }  else if (itemId == R.id.action_history) {
            Intent intent = new Intent(StudentListActivity.this, LoginHistoryActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_view_student_list) {
//            Intent intent = new Intent(EditUserActivity.this, StudentListActivity.class);
//            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_add_student) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(StudentListActivity.this, AddStudentActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(StudentListActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_add_user) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(StudentListActivity.this, AddUserActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(StudentListActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if (itemId == R.id.action_view_user_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(StudentListActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(StudentListActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_view_student_list) {
//            FirebaseAuth auth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = auth.getCurrentUser();
//            String email = currentUser.getEmail();
//            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
//                Intent intent = new Intent(StudentListActivity.this, StudentListActivity.class);
//                startActivity(intent);
//                return true;
//            } else{
//                Toast.makeText(StudentListActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
//            }
            return true;
        }  else {
            return super.onOptionsItemSelected(item);
        }
    }
}
