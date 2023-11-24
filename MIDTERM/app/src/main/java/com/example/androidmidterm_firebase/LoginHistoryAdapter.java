package com.example.androidmidterm_firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LoginHistoryAdapter extends RecyclerView.Adapter<LoginHistoryAdapter.myViewHolder>{
    private List<LoginHistoryModel> loginHistories;
    private Context context;
    public LoginHistoryAdapter(Context context, List<LoginHistoryModel> loginHistories) {
        this.context = context;
        this.loginHistories = loginHistories;
    }

    @NonNull
    @Override
    public LoginHistoryAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_rv_item, parent, false);
        return new LoginHistoryAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoginHistoryAdapter.myViewHolder holder, int position) {
        LoginHistoryModel loginHistory = loginHistories.get(position);
        holder.tv_timeLogin.setText(loginHistory.getLoginStart());
    }

    @Override
    public int getItemCount() {
        return loginHistories.size();
    }
    public static class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_timeLogin;
        public myViewHolder(View view) {
            super(view);
            tv_timeLogin = view.findViewById(R.id.tv_timeLogin);
        }
    }
}
