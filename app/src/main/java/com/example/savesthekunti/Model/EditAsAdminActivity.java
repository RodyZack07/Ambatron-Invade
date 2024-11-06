package com.example.savesthekunti.Model;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.savesthekunti.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EditAsAdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private AdaptorUser adaptorUser;
    private List<String> accounts;  // Daftar nickname akun
    private List<String> accountIds; // Daftar ID akun
    private String adminUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_as_admin);  // Pastikan nama layout benar: edit_as_admin

        // Inisialisasi Firestore dan RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        accounts = new ArrayList<>();
        accountIds = new ArrayList<>();

        adaptorUser = new AdaptorUser(accounts, accountIds, new AdaptorUser.OnItemClickListener() {
            @Override
            public void onUpdateClick(String account, String accountId, int position) {
                // Menampilkan Toast atau tampilkan dialog untuk mengupdate nama akun
                Toast.makeText(EditAsAdminActivity.this, "Update clicked for: " + account, Toast.LENGTH_SHORT).show();
                // Misalnya, buat dialog untuk memperbarui akun
                updateAccount(accountId, "NewNickname"); // Ubah nama sesuai input
            }

            @Override
            public void onDeleteClick(String accountId, int position) {
                // Hapus akun
                deleteAccount(accountId, position);
            }
        });

        recyclerView.setAdapter(adaptorUser);

        // Ambil data akun dari Firestore dan tampilkan di RecyclerView
        adminUser = getIntent().getStringExtra("adminUser"); // Ambil user admin dari intent
        fetchAdminAccounts();
    }

    private void fetchAdminAccounts() {
        db.collection("Akun")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result != null && !result.isEmpty()) {
                            for (QueryDocumentSnapshot document : result) {
                                String accountNickname = document.getString("nickname");
                                String accountId = document.getId(); // Ambil ID akun untuk update/delete
                                if (accountNickname != null && accountId != null) {
                                    accounts.add(accountNickname);
                                    accountIds.add(accountId);
                                }
                            }
                            adaptorUser.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(EditAsAdminActivity.this, "Error fetching accounts: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAccount(String accountId, String newNickname) {
        db.collection("Akun").document(accountId)
                .update("nickname", newNickname)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditAsAdminActivity.this, "Account updated successfully", Toast.LENGTH_SHORT).show();
                    // Update UI dengan mengganti nama yang baru
                    int position = accountIds.indexOf(accountId);
                    accounts.set(position, newNickname);
                    adaptorUser.notifyItemChanged(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditAsAdminActivity.this, "Failed to update account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteAccount(String accountId, int position) {
        db.collection("Akun").document(accountId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditAsAdminActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                    accounts.remove(position);
                    accountIds.remove(position);
                    adaptorUser.notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditAsAdminActivity.this, "Failed to delete account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
