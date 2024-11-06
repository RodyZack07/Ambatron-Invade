package com.example.savesthekunti.Model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.savesthekunti.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdaptorUser extends RecyclerView.Adapter<AdaptorUser.AccountViewHolder> {

    private List<String> accounts;  // Daftar akun (nickname)
    private List<String> accountIds; // Daftar ID akun untuk keperluan update/delete
    private OnItemClickListener listener;
    private FirebaseFirestore db;

    public AdaptorUser(List<String> accounts, List<String> accountIds, OnItemClickListener listener) {
        this.accounts = accounts;
        this.accountIds = accountIds;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance(); // Inisialisasi Firestore
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_as_admin, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        String account = accounts.get(position);
        holder.nicknameTextView.setText(account);

        // Menangani klik tombol Update
        holder.updateButton.setOnClickListener(v -> {
            listener.onUpdateClick(account, accountIds.get(position), position); // Mengirimkan account dan ID
        });

        // Menangani klik tombol Delete
        holder.deleteButton.setOnClickListener(v -> {
            listener.onDeleteClick(accountIds.get(position), position); // Mengirimkan ID akun untuk dihapus
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView nicknameTextView;
        Button updateButton, deleteButton;

        public AccountViewHolder(View itemView) {
            super(itemView);
            nicknameTextView = itemView.findViewById(R.id.nicknameTextView);
            updateButton = itemView.findViewById(R.id.updateButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    // Interface untuk menangani klik update dan delete
    public interface OnItemClickListener {
        void onUpdateClick(String account, String accountId, int position);
        void onDeleteClick(String accountId, int position);
    }
}
