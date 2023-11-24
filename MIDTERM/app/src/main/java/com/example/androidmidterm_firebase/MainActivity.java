package com.example.androidmidterm_firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv_user;
    Toolbar toolbar;
    private static final int REQUEST_CODE_EDIT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Check user logged
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            //// User is already logged in
            LoadRecyclerView();
            LoadToolBar();
            String uid = currentUser.getUid();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Toast.makeText(MainActivity.this, "Logged by admin", Toast.LENGTH_SHORT).show();
                //This page is for admin to add or update user
            } else if (email.toLowerCase().contains("manager")){
                Toast.makeText(MainActivity.this, "Logged by manager", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, StudentListActivity.class);
                startActivity(intent);
                //Use intent to move to student page
            } else{
                Toast.makeText(MainActivity.this, "Logged by employee", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, StudentListActivity.class);
                startActivity(intent);
                //Use intent to move to student page
            }
        } else {
            //// User is not logged in
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_profile) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("userName", email);
            startActivity(intent);
            return true;
        }  else if (itemId == R.id.action_history) {
            Intent intent = new Intent(MainActivity.this, LoginHistoryActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_view_student_list) {
            Intent intent = new Intent(MainActivity.this, StudentListActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_add_student) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(MainActivity.this, AddStudentActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(MainActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_add_user) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(MainActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if (itemId == R.id.action_view_user_list) {
//            FirebaseAuth auth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = auth.getCurrentUser();
//            String email = currentUser.getEmail();
//            if (email.toLowerCase().contains("admin")){
//                Intent intent = new Intent(StudentListActivity.this, MainActivity.class);
//                startActivity(intent);
//                return true;
//            } else{
//                Toast.makeText(StudentListActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
//            }
            return true;
        } else if (itemId == R.id.action_view_student_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(MainActivity.this, StudentListActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(MainActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }  else {
            return super.onOptionsItemSelected(item);
        }
    }
    public void LoadRecyclerView(){
        //Load users to recycler view
        rv_user = findViewById(R.id.rv_user);
        rv_user.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserModel> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserModel user = userSnapshot.getValue(UserModel.class);
                    users.add(user);
                }
                UserAdapter adapter = new UserAdapter(MainActivity.this,users);
                rv_user.setAdapter(adapter);
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
        if (requestCode == REQUEST_CODE_EDIT) {
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