package com.example.savesthekunti;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Delete extends AppCompatActivity {

    private Button deleteButton;
    private DatabaseReference databaseReference;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_layout);

        // Inisialisasi Firebase dan UID
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Inisialisasi view
        deleteButton = findViewById(R.id.deleteButton);

        // Delete button click
        deleteButton.setOnClickListener(v -> deleteUser());
    }

    private void deleteUser() {
        // Hapus data dari Realtime Database
        databaseReference.child("Akun").child(userUid).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Hapus akun pengguna dari Firebase Auth
                FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        Toast.makeText(Delete.this, "Akun berhasil dihapus!", Toast.LENGTH_SHORT).show();
                        finish(); // Kembali ke halaman sebelumnya
                    } else {
                        Toast.makeText(Delete.this, "Gagal menghapus akun!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(Delete.this, "Gagal menghapus data dari database!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
