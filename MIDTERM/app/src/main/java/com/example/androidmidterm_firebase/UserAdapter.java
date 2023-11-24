package com.example.androidmidterm_firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.myViewHolder>{

    private List<UserModel> users;
    private Context context;
    private static final int REQUEST_CODE = 1;
    public UserAdapter(Context context, List<UserModel> users) {
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_rv_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        UserModel user = users.get(position);
        holder.titleTextView.setText(user.getName());
        holder.contentTextView.setText(user.getPhoneNum());
        holder.iv_imgSrc.getLayoutParams().width = 150;
        holder.iv_imgSrc.getLayoutParams().height = 150;
        Picasso.with(holder.itemView.getContext())
                .load(user.getImgSrc())
                .into(holder.iv_imgSrc);
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findUserIdAndRemoveUser(user.userName);
            }
        });
        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditUserActivity.class);
                intent.putExtra("accountName", user.userName);
                // Start the new activity with the Intent, expecting a result
                ((Activity) context).startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }
    private void findUserIdAndRemoveUser(String accountName) {
        // Query the database to find the user with the specified accountName
        // Remove tha account of user
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        usersReference.orderByChild("userName").equalTo(accountName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve the userId of the user with the specified phoneNum
                    String userId = userSnapshot.getKey();
                    // Call a method to remove the user by providing the userId
                    // Find user authentication and delete
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);;
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String email = snapshot.child("userName").getValue(String.class);
                            String password = snapshot.child("password").getValue(String.class);
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            try{
                                auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                    if (user != null) {
                                                        user.delete()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            //Remove user
                                                                            usersReference.child(userId).removeValue();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        });
                            } catch (Exception e){
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView contentTextView;
        public ImageView iv_imgSrc;
        public ImageView iv_delete;
        public ImageView iv_edit;
        public myViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.tv_name);
            contentTextView = view.findViewById(R.id.tv_phone);
            iv_imgSrc = view.findViewById(R.id.iv_imgSrc);
            iv_delete = view.findViewById(R.id.iv_delete);
            iv_edit = view.findViewById(R.id.iv_edit);
        }
    }
}
