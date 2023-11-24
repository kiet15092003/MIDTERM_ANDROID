package com.example.androidmidterm_firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.myViewHolder> {

    private List<StudentModel> students;
    private Context context;
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_2 = 2;
    public StudentAdapter(Context context, List<StudentModel> students) {
        this.students = students;
        this.context = context;
    }
    @NonNull
    @Override
    public StudentAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_rv_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.myViewHolder holder, int position) {
        StudentModel student = students.get(position);
        holder.titleTextView.setText(student.getName());
        holder.contentTextView.setText(student.getClassName());
        holder.iv_imgSrc.getLayoutParams().width = 150;
        holder.iv_imgSrc.getLayoutParams().height = 150;
        Picasso.with(holder.itemView.getContext())
                .load("https://static.vecteezy.com/system/resources/previews/015/278/806/original/the-avatar-of-the-graduate-student-icon-illustration-in-a-flat-style-isolated-on-a-white-background-vector.jpg")
                .into(holder.iv_imgSrc);
        holder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentDetailActivity.class);
                intent.putExtra("studentId", student.getStudentId());
                // Start the new activity with the Intent, expecting a result
                ((Activity) context).startActivityForResult(intent,REQUEST_CODE);
            }
        });
        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                String email = currentUser.getEmail();
                if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                    Intent intent = new Intent(context, EditStudentActivity.class);
                    intent.putExtra("studentId", student.getStudentId());
                    // Start the new activity with the Intent, expecting a result
                    ((Activity) context).startActivityForResult(intent,REQUEST_CODE_2);
                } else{
                    Toast.makeText(context, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                String email = currentUser.getEmail();
                if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                    RemoveStudentById(student.getStudentId());
                } else{
                    Toast.makeText(context, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView contentTextView;
        public ImageView iv_imgSrc;
        public MaterialButton btn_detail;
        public ImageView iv_edit;
        public ImageView iv_delete;
        public myViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.tv_name);
            contentTextView = view.findViewById(R.id.tv_class);
            iv_imgSrc = view.findViewById(R.id.iv_imgSrc);
            btn_detail = view.findViewById(R.id.btn_detail);
            iv_edit = view.findViewById(R.id.iv_edit);
            iv_delete = view.findViewById(R.id.iv_delete);
        }
    }

    public void RemoveStudentById(String studentId){
        //Remove all certificates of student
        DatabaseReference certificateRefs = FirebaseDatabase.getInstance().getReference().child("certificates");
        certificateRefs.orderByChild("studentId").equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot certificateSnapshot : snapshot.getChildren()) {
                    certificateRefs.child(certificateSnapshot.getKey()).removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        // Remove student
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("students").child(studentId);
        studentRef.removeValue();
    }
}
