package com.example.androidmidterm_firebase;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.Collections;
import java.util.List;

public class LoginHistoryActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rv_loginHistory;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_login);
        toolbar = findViewById(R.id.toolbar);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            LoadToolBar();
            String accountName = currentUser.getEmail();
            rv_loginHistory =  findViewById(R.id.rv_loginHistory);
            rv_loginHistory.setLayoutManager(new LinearLayoutManager(this));
            DatabaseReference loginHistoryReference = FirebaseDatabase.getInstance().getReference().child("loginHistories");
            loginHistoryReference.orderByChild("userName").equalTo(accountName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<LoginHistoryModel> loginHistories = new ArrayList<>();
                    for (DataSnapshot loginHistorySnapshot : snapshot.getChildren()) {
                        LoginHistoryModel loginHistory = loginHistorySnapshot.getValue(LoginHistoryModel.class);
                        loginHistories.add(loginHistory);
                    }
                    Collections.reverse(loginHistories);
                    LoginHistoryAdapter adapter = new LoginHistoryAdapter(LoginHistoryActivity.this,loginHistories);
                    rv_loginHistory.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
            Intent intent = new Intent(LoginHistoryActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_profile) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            Intent intent = new Intent(LoginHistoryActivity.this, ProfileActivity.class);
            intent.putExtra("userName", email);
            startActivity(intent);
            return true;
        }  else if (itemId == R.id.action_history) {
//            Intent intent = new Intent(AddStudentActivity.this, LoginHistoryActivity.class);
//            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_view_student_list) {
            Intent intent = new Intent(LoginHistoryActivity.this, StudentListActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_add_student) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(LoginHistoryActivity.this, AddStudentActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(LoginHistoryActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_add_user) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(LoginHistoryActivity.this, AddUserActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(LoginHistoryActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if (itemId == R.id.action_view_user_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(LoginHistoryActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(LoginHistoryActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_view_student_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(LoginHistoryActivity.this, StudentListActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(LoginHistoryActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }  else {
            return super.onOptionsItemSelected(item);
        }
    }
}