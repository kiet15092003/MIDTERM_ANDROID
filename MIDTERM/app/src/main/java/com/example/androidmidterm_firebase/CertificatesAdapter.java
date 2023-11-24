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

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CertificatesAdapter extends RecyclerView.Adapter<CertificatesAdapter.myViewHolder>{
    private List<CertificateModel> certificates;
    private Context context;
    private static final int REQUEST_CODE_EDIT = 2, REQUEST_CODE_DELETE=3;
    public CertificatesAdapter(List<CertificateModel> certificate, Context context) {
        this.certificates = certificate;
        this.context = context;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.certificates_rv_item, parent, false);
        return new CertificatesAdapter.myViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        CertificateModel certificate = certificates.get(position);
        holder.tv_certificates_name.setText(certificate.getCertificateName());
        holder.tv_score.setText(String.valueOf(certificate.getScore()));
        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                String email = currentUser.getEmail();
                if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                    Intent intent = new Intent(context, EditCertificateActivity.class);
                    intent.putExtra("certificateName", certificate.getCertificateName());
                    intent.putExtra("studentId", certificate.getStudentId());
                    intent.putExtra("score",String.valueOf(certificate.getScore()));
                    // Start the new activity with the Intent, expecting a result
                    ((Activity) context).startActivityForResult(intent,REQUEST_CODE_EDIT);
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
                    DatabaseReference certificatesRef = FirebaseDatabase.getInstance().getReference().child("certificates");
                    certificatesRef.orderByChild("studentId").equalTo(certificate.getStudentId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot certificateSnapshot : snapshot.getChildren()){
                                String keys = certificateSnapshot.getKey();
                                DatabaseReference certificateRef = FirebaseDatabase.getInstance().getReference().child("certificates").child(keys);
                                certificateRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String certificateNames = snapshot.child("certificateName").getValue(String.class);
                                        if (certificateNames!=null) {
                                            if (certificateNames.equals(certificate.getCertificateName())) {
                                                certificateRef.removeValue();
                                                Intent intent = new Intent(context, StudentDetailActivity.class);
                                                intent.putExtra("studentId", certificate.getStudentId());
                                                // Start the new activity with the Intent, expecting a result
                                                ((Activity) context).startActivityForResult(intent,REQUEST_CODE_DELETE);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else{
                    Toast.makeText(context, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return certificates.size();
    }
    public static class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_certificates_name;
        public TextView tv_score;
        public ImageView iv_edit;
        public ImageView iv_delete;
        public myViewHolder(View view) {
            super(view);
            tv_certificates_name = view.findViewById(R.id.tv_certificates_name);
            tv_score = view.findViewById(R.id.tv_score);
            iv_edit = view.findViewById(R.id.iv_edit);
            iv_delete = view.findViewById(R.id.iv_delete);
        }
    }
}
